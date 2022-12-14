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
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)      // ???????????? ?????? ???????????? ??? UnAuthorized(401) ?????? ??????
                .accessDeniedHandler(jwtAccessDeniedHandler)                // ?????? ????????? ?????? ???????????? ??? Forbidden(403) ?????? ??????

                // ????????? ???????????? ?????? ????????? ????????? Stateless??? ??????
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/login/**").permitAll()
                .antMatchers("/login/oauth/google").permitAll()
                .antMatchers("/token/reissue").permitAll()
                .antMatchers("/signup").permitAll()
                .antMatchers("/member/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().authenticated()  // ????????? API??? ?????? ?????? ??????

                .and()
                .logout().logoutSuccessUrl("/login/oauth2")

                .and()
                .apply(new JwtSecurityConfig(tokenProvider))

                .and()
                .oauth2Login()          // OAuth2 ????????? ????????? ?????? ?????? ?????????
                .userInfoEndpoint()     // ????????? ?????? ??? ????????? ?????? ?????? ?????? ??????
                .userService(customOAuth2UserService);      // ?????? ????????? ?????? ??? ?????? ????????? ?????? ??????

        return http.build();
    }
}
