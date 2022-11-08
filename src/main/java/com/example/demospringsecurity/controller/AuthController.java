package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.dto.LoginDto;
import com.example.demospringsecurity.dto.SignUpDto;
import com.example.demospringsecurity.dto.TokenDto;
import com.example.demospringsecurity.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpDto signUpDto) {
        authService.signup(signUpDto);

        return ResponseEntity.ok("success");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto) {
        // Jwt 전달
        TokenDto tokenDto = authService.login(loginDto);

        return ResponseEntity
                .ok()
                .header("Authorization", "Bearer ".concat(tokenDto.getAccessToken()))
                .body(tokenDto);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("success - logout");
    }
}
