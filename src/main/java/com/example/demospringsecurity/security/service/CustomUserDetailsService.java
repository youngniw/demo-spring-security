package com.example.demospringsecurity.security.service;

import com.example.demospringsecurity.domain.Member;
import com.example.demospringsecurity.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        // 인증 처리 방식 정의
        // username: memberId
        return memberRepository.findById(Long.parseLong(memberId))
                .map(member -> createUser(memberId, member))
                .orElseThrow(() -> new UsernameNotFoundException("등록되지 않은 사용자입니다."));
    }

    private User createUser(String username, Member member) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(member.getMemberRole().getValue()));

        return new User(username, member.getLoginPassword(), grantedAuthorities);
    }
}
