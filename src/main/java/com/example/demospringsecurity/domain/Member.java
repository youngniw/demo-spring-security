package com.example.demospringsecurity.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "memberId")
@Table(indexes = { @Index(name = "member_login_id", columnList = "loginId", unique = true) })
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(length = 50, unique = true)
    private String loginId;

    @Column(length = 50)
    private String loginPassword;

    @Column(length = 50)
    private String name;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;
}
