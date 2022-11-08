package com.example.demospringsecurity.dto;

import lombok.Getter;

@Getter
public class TokenRequestDto {
    private String accessToken;
    private String refreshToken;
}
