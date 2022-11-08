package com.example.demospringsecurity.service;

import com.example.demospringsecurity.domain.Member;
import com.example.demospringsecurity.domain.MemberRole;
import com.example.demospringsecurity.dto.LoginDto;
import com.example.demospringsecurity.dto.SignUpDto;
import com.example.demospringsecurity.dto.TokenDto;
import com.example.demospringsecurity.repository.MemberRepository;
import com.example.demospringsecurity.security.SecurityUtil;
import com.example.demospringsecurity.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    // 회원 가입 (회원 가입 유무 확인 및 생성)
    public void signup(SignUpDto signUpDto) {
        if (memberRepository.existsByLoginId(signUpDto.getId()))
            throw new RuntimeException("이미 가입되어 있는 회원입니다.");

        Member member = Member.builder()
                .loginId(signUpDto.getId())
                .loginPassword(passwordEncoder.encode(signUpDto.getPassword()))
                .name(signUpDto.getName())
                .memberRole(MemberRole.USER)    // 회원가입한 회원은 모두 USER 역할을 취득함
                .build();

        memberRepository.save(member);
    }

    // 로그인 (회원 검증 및 토큰 반환)
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

    // SecurityContext 내의 회원 반환
    public Optional<Member> getMemberInContextWithAuthorities() {
        return SecurityUtil.getCurrentMemberLoginId().flatMap(memberRepository::findByLoginId);
    }
}
