package com.example.blog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat; //static import

@SpringBootTest
class BlogApplicationTests {

	@Autowired
	private ApplicationContext applicationContext; //DIコンテナからアプリケーションコンテキストを受け取る

	@Test
	void contextLoads() {
		// ## Arrange ##
		var bean = applicationContext.getBean("userRestController"); //getBeanで登録されているBean取得

		// ## Act ##

		// ## Assert ##
		assertThat(bean).isNotNull();
	}
}
