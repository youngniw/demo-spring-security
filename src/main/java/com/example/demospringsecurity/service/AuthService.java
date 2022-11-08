package com.example.demospringsecurity.service;

import com.example.demospringsecurity.dto.LoginDto;
import com.example.demospringsecurity.dto.TokenDto;
import com.example.demospringsecurity.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    public TokenDto login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getLoginId(), loginDto.getLoginPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return tokenProvider.generateToken(authentication);
        } catch (AuthenticationException e) {
            // ex) 아이디가 없음, 비밀번호가 옳지 않음
            throw new RuntimeException("로그인 정보가 옳지 않습니다.");
        }
    }
}
