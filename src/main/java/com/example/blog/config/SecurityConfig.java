package com.example.blog.config;

import com.example.blog.web.filter.CsrfCookieFilter;
import com.example.blog.web.filter.JsonUsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http ,
            SecurityContextRepository securityContextRepository ,
            //現在認証されたセキュリティ関連情報を格納するためのコンテナ　SecurityContext
            SessionAuthenticationStrategy sessionAuthenticationStrategy,
            AuthenticationManager authenticationManager ,
            ObjectMapper objectMapper
    ) throws Exception {
        http
                .csrf((csrf) -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                )
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
        //JsonUsernamePasswordAuthenticationFilterはSpringSecurityのデフォルトではない 実装する必要あり。
                .addFilterAt(
                        new JsonUsernamePasswordAuthenticationFilter(
                                securityContextRepository ,
                                sessionAuthenticationStrategy ,
                                authenticationManager ,
                                objectMapper
                                ),
                        UsernamePasswordAuthenticationFilter.class//書き換え追加JSON認証を受け取り、変換
                )
                .securityContext(context -> context.securityContextRepository(securityContextRepository))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/").permitAll()
                        .requestMatchers(HttpMethod.POST,"/users").permitAll()
                        .requestMatchers("/articles/**").permitAll()
                        .anyRequest().authenticated() //アプリケーションに対する全てのリクエストに対してログインしていなければアクセスを許可しない
                )
                .exceptionHandling(customizer -> customizer.accessDeniedHandler((req,res,ex) ->{
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }))
                .logout(logout -> logout.logoutSuccessHandler((req,res,auth) ->{
                    res.setStatus(HttpServletResponse.SC_OK);
                }));

        return http.build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository(){
        return new HttpSessionSecurityContextRepository();
    };

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            PasswordEncoder passwordEncoder ,
            UserDetailsService userDetailsService
    ){
        var provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return new ProviderManager(provider);
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy(){
        return new ChangeSessionIdAuthenticationStrategy();
    }
}