package com.example.demospringsecurity.security.config;

import com.example.demospringsecurity.security.jwt.JwtAccessDeniedHandler;
import com.example.demospringsecurity.security.jwt.JwtAuthenticationEntryPoint;
import com.example.demospringsecurity.security.jwt.TokenProvider;
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
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)      // 인증되지 않은 사용자일 때 UnAuthorized(401) 오류 반환
                .accessDeniedHandler(jwtAccessDeniedHandler)                // 필요 권한이 없는 사용자일 때 Forbidden(403) 오류 반환

                // 세션을 사용하지 않기 때문에 세션을 Stateless로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/token/reissue").permitAll()
                .antMatchers("/signup").anonymous()
                .antMatchers("/member/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().authenticated()  // 나머지 API는 모두 인증 필요

                // TODO: 로그아웃 관련 기능 개발 요망!
//                .and()
//                .logout().logoutUrl("/logout").invalidateHttpSession(true)

                .and()
                .apply(new JwtSecurityConfig(tokenProvider));

        return http.build();
    }
}
