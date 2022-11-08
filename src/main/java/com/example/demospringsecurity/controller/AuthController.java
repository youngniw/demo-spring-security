package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.dto.LoginDto;
import com.example.demospringsecurity.dto.SignUpDto;
import com.example.demospringsecurity.dto.TokenDto;
import com.example.demospringsecurity.dto.TokenRequestDto;
import com.example.demospringsecurity.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpDto signUpDto) {
        authService.signup(signUpDto);

        return ResponseEntity.ok("success");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto) {
        // Jwt 전달
        TokenDto tokenDto = authService.login(loginDto);

        return ResponseEntity
                .ok()
                .header("Authorization", "Bearer ".concat(tokenDto.getAccessToken()))
                .body(tokenDto);
    }

    // 로그아웃
    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        // TODO: REFRESH TOKEN 삭제

        return ResponseEntity.ok("success - logout");
    }

    // 토큰 재발급
    @PostMapping("/token/reissue")
    public ResponseEntity<TokenDto> tokenReissue(@RequestBody TokenRequestDto tokenRequest) {
        // Jwt 전달
        TokenDto reissueToken = authService.reissue(tokenRequest);

        return ResponseEntity
                .ok()
                .header("Authorization", "Bearer ".concat(reissueToken.getAccessToken()))
                .body(reissueToken);
    }
}
