package com.example.blog.it;

import com.example.blog.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationAndLoginIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserService userService;

    private static final String TEST_USERNAME = "user99";
    private static final String TEST_PASSWORD = "password1";

    private static final String DUMMY_SESSION_ID = "session_id_1";

    @BeforeEach
    public void beforeEach() {

        userService.delete(TEST_USERNAME);
    }
    //@Testのついたメソッドが呼び出される前後に実行されるメソッドを実装することでテストに使用した値を実行前後で消去する

    @AfterEach
    public void afterEach() {

        userService.delete(TEST_USERNAME);
    }
    @Test
    public void integrationTest() {

        //ユーザー作成
        var xsrfToken = getRoot();
        register(xsrfToken);

        // ログイン失敗
        // Cookie に XSRF-TOKEN がない
        loginFailure_NoXSRFTokenInCookies(xsrfToken);

        // ヘッダーに X-XSRF-TOKEN がない
        loginFailure_NoXXSRFTokenInHeader(xsrfToken);

        // Cookie の XSRF-TOKEN とヘッダーの X-XSRF-TOKEN の値が異なる
        loginFailure_DifferentToken(xsrfToken);

        // ユーザー名が存在しない
        loginFailure_GivenUsernameDoesNotExistInDatabase(xsrfToken);

        // パスワードがデータベースに保存されているパスワードと違う
        loginFailure_GivenPasswordIsNotSameAsPasswordInDatabase(xsrfToken);

        // ログイン成功
        loginSuccess(xsrfToken);

    }

    private String getRoot(){
        //## Arrange ##

        //## Act ##
        var responseSpec = webTestClient.get().uri("/").exchange();

        //## Assert ##
        var response = responseSpec.returnResult(String.class);
        var xsrfTokenOpt = Optional.ofNullable(response.getResponseCookies().getFirst("XSRF-TOKEN"));
        //optionalでポストマンで表示されるようなXSRF-TOKENの長いコードが戻り値として挿入される
        responseSpec.expectStatus().isNoContent();
        assertThat(xsrfTokenOpt)
                .isPresent()//Optional<null>の時失敗する
                .hasValueSatisfying(xsrfTokenCookie ->
                        assertThat(xsrfTokenCookie.getValue()).isNotBlank()
                ); //Optional<XSRF-TOKEN=xxxxx>

        return xsrfTokenOpt.get().getValue();
    }

    private void register(String xsrfToken){

        //##Arrange##
        var bodyJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """,TEST_USERNAME,TEST_PASSWORD);

        //## Act ##
        var responseSpec = webTestClient
                .post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN",xsrfToken)
                .header("X-XSRF-TOKEN",xsrfToken)
                .bodyValue(bodyJson)
                .exchange();

        //##Assert ##
        responseSpec.expectStatus().isCreated();
    }

    private void loginSuccess(String xsrfToken){
        //##Arrange##
        var bodyJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """,TEST_USERNAME,TEST_PASSWORD);

        //## Act ##
        var responseSpec = webTestClient
                .post().uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN",xsrfToken)
                .cookie("JSESSIONID",DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN",xsrfToken)
                .bodyValue(bodyJson)
                .exchange();

        //##Assert ##
        /*responseSpec
                .expectStatus().isOk() //以下書き換えあり　ログイン実装のテスト
                .expectCookie().value("JSESSIONID", v -> assertThat(v).isNotBlank())
                .expectCookie().value("JSESSIONID", v -> assertThat(v).isNotEqualTo(DUMMY_SESSION_ID));*/
        responseSpec
                .expectStatus().isOk()
                .expectCookie().value("JSESSIONID", v -> assertThat(v)
                        .isNotBlank()
                        .isNotEqualTo(DUMMY_SESSION_ID)
                );
    }

    private void loginFailure_NoXSRFTokenInCookies(String xsrfToken){
        //##Arrange##
        var bodyJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """,TEST_USERNAME,TEST_PASSWORD);

        //## Act ##
        var responseSpec = webTestClient
                .post().uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                //.cookie("XSRF-TOKEN",xsrfToken)
                .cookie("JSESSIONID",DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN",xsrfToken)
                .bodyValue(bodyJson)
                .exchange();

        //##Assert ##
        responseSpec.expectStatus().isForbidden();
    }

    private void loginFailure_NoXXSRFTokenInHeader(String xsrfToken){
        //##Arrange##
        var bodyJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """,TEST_USERNAME,TEST_PASSWORD);

        //## Act ##
        var responseSpec = webTestClient
                .post().uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN",xsrfToken)
                .cookie("JSESSIONID",DUMMY_SESSION_ID)
                //.header("X-XSRF-TOKEN",xsrfToken)
                .bodyValue(bodyJson)
                .exchange();

        //##Assert ##
        responseSpec.expectStatus().isForbidden();
    }

    private void loginFailure_DifferentToken(String xsrfToken){
        //##Arrange##
        var bodyJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """,TEST_USERNAME,TEST_PASSWORD);

        //## Act ##
        var responseSpec = webTestClient
                .post().uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN",xsrfToken)
                .cookie("JSESSIONID",DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN",xsrfToken + "_invalid")
                .bodyValue(bodyJson)
                .exchange();

        //##Assert ##
        responseSpec.expectStatus().isForbidden();
    }

    private void loginFailure_GivenUsernameDoesNotExistInDatabase(String xsrfToken){
        //##Arrange##
        var bodyJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """,TEST_USERNAME + "_invalid",TEST_PASSWORD);

        //## Act ##
        var responseSpec = webTestClient
                .post().uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN",xsrfToken)
                .cookie("JSESSIONID",DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN",xsrfToken)
                .bodyValue(bodyJson)
                .exchange();

        //##Assert ##
        responseSpec.expectStatus().isUnauthorized();
    }

    private void loginFailure_GivenPasswordIsNotSameAsPasswordInDatabase(String xsrfToken){
        //##Arrange##
        var bodyJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """,TEST_USERNAME,TEST_PASSWORD + "_invalid");

        //## Act ##
        var responseSpec = webTestClient
                .post().uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN",xsrfToken)
                .cookie("JSESSIONID",DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN",xsrfToken)
                .bodyValue(bodyJson)
                .exchange();

        //##Assert ##
        responseSpec.expectStatus().isUnauthorized();
    }
}
