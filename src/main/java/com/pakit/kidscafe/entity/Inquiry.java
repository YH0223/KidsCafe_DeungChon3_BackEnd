package com.pakit.kidscafe.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;
    private String password;
    private String category;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Boolean isSecret;
    private Boolean isReplied;
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String reply;
    private LocalDateTime repliedAt;

    @Builder
    public Inquiry(String author, String password, String category, String title, String content, Boolean isSecret) {
        this.author = author;
        this.password = password;
        this.category = category;
        this.title = title;
        this.content = content;
        this.isSecret = isSecret != null ? isSecret : false;
        this.isReplied = false;
        this.createdAt = LocalDateTime.now();
    }

    // [핵심] 비밀번호 검증 책임을 엔티티가 가짐
    public void authenticate(String rawPassword, PasswordEncoder passwordEncoder) {
        if (!passwordEncoder.matches(rawPassword, this.password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    // [핵심] 수정 비즈니스 로직
    public void update(String category, String title, String content, Boolean isSecret) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.isSecret = isSecret;

    }

    // [핵심] 답변 비즈니스 로직
    public void answer(String replyContent) {
        this.reply = replyContent;
        this.isReplied = true;
        this.repliedAt = LocalDateTime.now();
    }
}