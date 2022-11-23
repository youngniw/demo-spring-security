package com.example.demospringsecurity.security.config;

import com.example.demospringsecurity.security.jwt.JwtAccessDeniedHandler;
import com.example.demospringsecurity.security.jwt.JwtAuthenticationEntryPoint;
import com.example.demospringsecurity.security.jwt.TokenProvider;
import com.example.demospringsecurity.security.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Log
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final TokenProvider tokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer configure() {
        // configure Web Security
        return web -> web.ignoring().antMatchers(
                "/resources/**"
        );
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // configure Http Security
        http
                .csrf()
                .disable()
                .cors()

                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)      // 인증되지 않은 사용자일 때 UnAuthorized(401) 오류 반환
                .accessDeniedHandler(jwtAccessDeniedHandler)                // 필요 권한이 없는 사용자일 때 Forbidden(403) 오류 반환

                // 세션을 사용하지 않기 때문에 세션을 Stateless로 설정
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/login/**").permitAll()
                .antMatchers("/login/oauth/google").permitAll()
                .antMatchers("/token/reissue").permitAll()
                .antMatchers("/signup").anonymous()
                .antMatchers("/member/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().authenticated()  // 나머지 API는 모두 인증 필요

                .and()
                .logout().logoutSuccessUrl("/login/oauth2")

                .and()
                .apply(new JwtSecurityConfig(tokenProvider))

                .and()
                .oauth2Login()          // OAuth2 로그인 기능에 대한 설정 시작점
                .userInfoEndpoint()     // 로그인 성공 후 사용자 정보 조회 시의 설정
                .userService(customOAuth2UserService);      // 소셜 로그인 성공 시 이후 진행할 기능 정의

        return http.build();
    }
}
