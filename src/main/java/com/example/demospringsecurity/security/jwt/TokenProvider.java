package com.example.demospringsecurity.security.jwt;

import com.example.demospringsecurity.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "auth";

    private final String secret;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;
    private Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidity,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidity) {
        this.secret = secret;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 회원 정보를 넘겨받아 access token과 refresh token 생성
    public TokenDto generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date accessValidity = new Date(now + this.accessTokenValidity);
        Date refreshValidity = new Date(now + this.refreshTokenValidity);

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())   // 회원 번호
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("young")
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(accessValidity)
                .compact();

        String refreshToken = Jwts.builder()
                .setIssuer("young")
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(refreshValidity)
                .compact();

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpireDate(accessValidity)
                .build();
    }

    // jwt 토큰을 복호화하여 토큰에 들어있는 정보 추출
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // jwt 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 jwt 서명입니다.");
        } catch (ExpiredJwtException e) {
            // TODO: refresh token 확인 후 토큰 재발급 받도록 error code 를 변경
            log.info("만료된 jwt 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 jwt 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("jwt 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // jwt 내의 정보 반환
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
