package com.example.demospringsecurity.security.service;

import com.example.demospringsecurity.domain.Member;
import com.example.demospringsecurity.domain.MemberRole;
import com.example.demospringsecurity.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();    // 현재 진행 중인 서비스 구분 (ex. google)
        String oauthUserId = oAuth2User.getAttribute("sub");
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();   // OAuth2 로그인 진행 시 키가 되는 필드 (구글의 경우: sub)
        Map<String, Object> information = oAuth2User.getAttributes();
        Member member = createMember(oauthUserId, information);

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(member.getMemberRole().getValue())), information, userNameAttributeName);
    }

    // 회원을 찾음. 만약 없는 회원이라면 회원 가입 처리
    private Member createMember(String oauthUserId, Map<String, Object> information) {
        Member member = memberRepository.findByEmail((String) information.get("email"))
                .map(entity -> entity.updateName((String) information.get("name")))
                .orElse(Member.builder()
                        .name((String) information.get("name"))
                        .email((String) information.get("email"))
                        .providerId(oauthUserId)
                        .memberRole(MemberRole.USER)
                        .build());

        return memberRepository.save(member);
    }
}
