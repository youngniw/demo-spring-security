package com.example.demospringsecurity.repository;

import com.example.demospringsecurity.domain.Member;
import com.example.demospringsecurity.domain.MemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Commit
@SpringBootTest
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testMemberInsert() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            MemberRole memberRole;
            if (i <= 5) {
                memberRole = MemberRole.ADMIN;
            }
            else {
                memberRole = MemberRole.USER;
            }

            Member member = Member.builder()
                    .loginId("user"+i)
                    .loginPassword(passwordEncoder.encode("pwuser"+i))
                    .name("사용자"+i)
                    .memberRole(memberRole)
                    .build();

            memberRepository.save(member);
        });
    }
}