package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.dto.*;
import com.example.demospringsecurity.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequiredArgsConstructor
@Controller
public class AuthController {
    private final AuthService authService;

    // 회원가입
    @ResponseBody
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpDto signUpDto) {
        authService.signup(signUpDto);

        return ResponseEntity.ok("success");
    }

    // 로그인
    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto) {
        // Jwt 전달
        TokenDto tokenDto = authService.login(loginDto);

        return ResponseEntity
                .ok()
                .header("Authorization", "Bearer ".concat(tokenDto.getAccessToken()))
                .body(tokenDto);
    }

    // 소셜 로그인(서버 웹페이지)
    @GetMapping("/login/oauth2")
    public String login() {
        return "social_login";
    }

    // 구글 소셜 로그인(Rest API) -> scope: [email, profile]
    @ResponseBody
    @PostMapping("/login/oauth2/google")
    public ResponseEntity<TokenDto> socialLoginGoogle(@RequestBody SocialLoginDto socialLoginDto) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String requestUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer ".concat(socialLoginDto.getAccessToken()));
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, String.class);

            String result = response.getBody();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            Map<String, String> resultJson = mapper.readValue(result, new TypeReference<>(){});

            TokenDto tokenDto = authService.socialLogin(resultJson.get("sub"), resultJson.get("name"), resultJson.get("email"));

            return ResponseEntity
                    .ok()
                    .header("Authorization", "Bearer ".concat(tokenDto.getAccessToken()))
                    .body(tokenDto);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("유저 정보 조회에 문제가 발생했습니다.");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException("서버에 문제가 발생했습니다.");
        }
    }

    // 토큰 재발급
    @ResponseBody
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
