package com.example.demospringsecurity.repository;

import com.example.demospringsecurity.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);

    Optional<Member> findByEmail(String email);
}
