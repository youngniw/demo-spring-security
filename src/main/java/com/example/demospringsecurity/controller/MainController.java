package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.dto.MemberInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MainController {

    @GetMapping("/")
    public String index(Authentication authentication, Model model) {
        log.info("authentication id = {}", authentication.getName());      // providerId 값
        Map<String, Object> attributes = ((DefaultOAuth2User) authentication.getPrincipal()).getAttributes();

        MemberInfoDto memberInfo = MemberInfoDto.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .build();

        model.addAttribute("member", memberInfo);

        return "index";
    }
}
