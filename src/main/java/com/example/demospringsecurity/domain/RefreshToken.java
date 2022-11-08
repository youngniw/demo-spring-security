package com.example.demospringsecurity.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "refreshTokenId")
@Table(indexes = {@Index(name = "refresh_token_key", columnList = "key", unique = true)})
@Entity
@EntityListeners(value = AuditingEntityListener.class)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenId;

    // 회원 번호
    @Column
    private Long key;

    @Column
    private String value;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Builder
    public RefreshToken(Long key, String value) {
        this.key = key;
        this.value = value;
    }

    // 토큰 값 변경
    public RefreshToken updateValue(String token) {
        this.value = token;
        return this;
    }
}
