package com.example.demospringsecurity.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    private Date accessTokenExpireDate;
}