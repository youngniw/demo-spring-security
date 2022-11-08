package com.example.demospringsecurity.controller;

import com.example.demospringsecurity.domain.Member;
import com.example.demospringsecurity.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberRepository memberRepository;

    // @Secured("ROLE_USER")와 같이 접근 제한 가능
    @GetMapping("/list")
    public ResponseEntity<List<Member>> getMemberList() {
        return ResponseEntity.ok(memberRepository.findAll());
    }
}
