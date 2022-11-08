package com.example.demospringsecurity.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Slf4j
public class SecurityUtil {

    // 현재 인증 회원 번호 반환
    public static Optional<Long> getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            log.info("현재 Security Context에 인증 정보가 없습니다.");
            return Optional.empty();
        }

        Long memberId = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails securityUser = (UserDetails) authentication.getPrincipal();
            memberId = Long.valueOf(securityUser.getUsername());
        }
        else if (authentication.getPrincipal() instanceof String) {
            memberId = Long.valueOf((String) authentication.getPrincipal());
        }
        else if (authentication.getPrincipal() instanceof Long) {
            memberId =(Long) authentication.getPrincipal();
        }

        return Optional.ofNullable(memberId);
    }
}
