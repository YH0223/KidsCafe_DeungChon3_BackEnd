# 키즈카페 백엔드 - Spring Boot + MariaDB

## 프로젝트 구조

```
kids-cafe-backend/
├── src/main/java/com/happykidsland/
│   ├── HappyKidsLandApplication.java
│   ├── config/
│   │   ├── CorsConfig.java
│   │   └── WebConfig.java
│   ├── controller/
│   │   ├── ReservationController.java
│   │   ├── NoticeController.java
│   │   └── ContactController.java
│   ├── dto/
│   │   ├── ReservationRequest.java
│   │   ├── ReservationResponse.java
│   │   ├── ContactRequest.java
│   │   └── NoticeResponse.java
│   ├── entity/
│   │   ├── Reservation.java
│   │   ├── Notice.java
│   │   └── Contact.java
│   ├── repository/
│   │   ├── ReservationRepository.java
│   │   ├── NoticeRepository.java
│   │   └── ContactRepository.java
│   └── service/
│       ├── ReservationService.java
│       ├── NoticeService.java
│       └── ContactService.java
├── src/main/resources/
│   ├── application.yml
│   └── schema.sql
├── build.gradle (또는 pom.xml)
└── README.md
```

---

## 1. build.gradle

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.0'
    id 'io.spring.dependency-management' version '1.1.4'
}

group = 'com.happykidsland'
version = '0.0.1-SNAPSHOT'

java {
    sourceCompatibility = '17'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    
    // BCrypt 암호화용
    implementation 'org.springframework.security:spring-security-crypto'
    
    // MariaDB
    runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
    
    // Lombok
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

---

## 2. application.yml

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/happykidsland?useUnicode=true&characterEncoding=utf8
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
    driver-class-name: org.mariadb.jdbc.Driver
    
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MariaDBDialect

  jackson:
    time-zone: Asia/Seoul

logging:
  level:
    com.happykidsland: DEBUG
```

---

## 3. Entity 클래스

### Reservation.java

```java
package com.happykidsland.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String phone;
    
    private String email;
    
    @Column(nullable = false)
    private LocalDate reservationDate;
    
    @Column(nullable = false)
    private LocalTime reservationTime;
    
    @Column(nullable = false)
    private Integer childCount;
    
    @Column(nullable = false)
    private Integer adultCount;
    
    @Column(columnDefinition = "TEXT")
    private String memo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ReservationStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }
}
```

### Notice.java

```java
package com.happykidsland.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(nullable = false)
    private String category; // 공지, 이벤트, 안내
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### Contact.java

```java
package com.happykidsland.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inquiries")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inquiry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String author;
    
    @Column(nullable = false)
    private String password; // bcrypt로 암호화 저장
    
    @Column(nullable = false)
    private String category;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(nullable = false)
    private Boolean isSecret = false;
    
    @Column(nullable = false)
    private Boolean isReplied = false;
    
    @Column(columnDefinition = "TEXT")
    private String reply;
    
    private LocalDateTime repliedAt;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

---

## 4. Repository 인터페이스

### ReservationRepository.java

```java
package com.happykidsland.repository;

import com.happykidsland.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByReservationDate(LocalDate date);
    
    List<Reservation> findByPhone(String phone);
    
    List<Reservation> findByReservationDateAndStatus(
        LocalDate date, 
        Reservation.ReservationStatus status
    );
}
```

### NoticeRepository.java

```java
package com.happykidsland.repository;

import com.happykidsland.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    
    List<Notice> findByIsActiveTrueOrderByCreatedAtDesc();
    
    List<Notice> findByCategoryAndIsActiveTrueOrderByCreatedAtDesc(String category);
}
```

### InquiryRepository.java

```java
package com.happykidsland.repository;

import com.happykidsland.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    
    List<Inquiry> findAllByOrderByCreatedAtDesc();
    
    List<Inquiry> findByIsRepliedFalseOrderByCreatedAtDesc();
    
    List<Inquiry> findByIsRepliedTrueOrderByCreatedAtDesc();
}
```

---

## 5. DTO 클래스

### ReservationRequest.java

```java
package com.happykidsland.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    
    @NotBlank(message = "이름은 필수입니다")
    private String name;
    
    @NotBlank(message = "연락처는 필수입니다")
    @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 연락처 형식이 아닙니다")
    private String phone;
    
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
    
    @NotNull(message = "예약 날짜는 필수입니다")
    @FutureOrPresent(message = "과거 날짜는 선택할 수 없습니다")
    private LocalDate reservationDate;
    
    @NotNull(message = "예약 시간은 필수입니다")
    private LocalTime reservationTime;
    
    @Min(value = 1, message = "어린이는 최소 1명 이상이어야 합니다")
    @Max(value = 10, message = "어린이는 최대 10명까지 가능합니다")
    private Integer childCount;
    
    @Min(value = 1, message = "보호자는 최소 1명 이상이어야 합니다")
    @Max(value = 10, message = "보호자는 최대 10명까지 가능합니다")
    private Integer adultCount;
    
    private String memo;
}
```

### InquiryRequest.java

```java
package com.happykidsland.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryRequest {
    
    @NotBlank(message = "작성자명은 필수입니다")
    private String author;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다")
    private String password;
    
    @NotBlank(message = "문의 유형을 선택해 주세요")
    private String category;
    
    @NotBlank(message = "제목을 입력해 주세요")
    private String title;
    
    @NotBlank(message = "내용을 입력해 주세요")
    @Size(min = 10, message = "내용은 10자 이상이어야 합니다")
    private String content;
    
    private Boolean isSecret = false;
}
```

### PasswordRequest.java

```java
package com.happykidsland.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRequest {
    private String password;
}
```

### ReplyRequest.java

```java
package com.happykidsland.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyRequest {
    
    @NotBlank(message = "답변 내용을 입력해 주세요")
    private String content;
}
```

---

## 6. Service 클래스

### ReservationService.java

```java
package com.happykidsland.service;

import com.happykidsland.dto.ReservationRequest;
import com.happykidsland.entity.Reservation;
import com.happykidsland.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    
    @Transactional
    public Reservation createReservation(ReservationRequest request) {
        Reservation reservation = Reservation.builder()
            .name(request.getName())
            .phone(request.getPhone())
            .email(request.getEmail())
            .reservationDate(request.getReservationDate())
            .reservationTime(request.getReservationTime())
            .childCount(request.getChildCount())
            .adultCount(request.getAdultCount())
            .memo(request.getMemo())
            .status(Reservation.ReservationStatus.PENDING)
            .build();
            
        return reservationRepository.save(reservation);
    }
    
    public List<Reservation> getReservationsByDate(LocalDate date) {
        return reservationRepository.findByReservationDate(date);
    }
    
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다"));
    }
    
    @Transactional
    public Reservation cancelReservation(Long id) {
        Reservation reservation = getReservationById(id);
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        return reservationRepository.save(reservation);
    }
}
```

### NoticeService.java

```java
package com.happykidsland.service;

import com.happykidsland.entity.Notice;
import com.happykidsland.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {
    
    private final NoticeRepository noticeRepository;
    
    public List<Notice> getAllActiveNotices() {
        return noticeRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }
    
    public Notice getNoticeById(Long id) {
        return noticeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다"));
    }
    
    public List<Notice> getNoticesByCategory(String category) {
        return noticeRepository.findByCategoryAndIsActiveTrueOrderByCreatedAtDesc(category);
    }
}
```

### InquiryService.java

```java
package com.happykidsland.service;

import com.happykidsland.dto.InquiryRequest;
import com.happykidsland.dto.ReplyRequest;
import com.happykidsland.entity.Inquiry;
import com.happykidsland.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {
    
    private final InquiryRepository inquiryRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Transactional
    public Inquiry createInquiry(InquiryRequest request) {
        Inquiry inquiry = Inquiry.builder()
            .author(request.getAuthor())
            .password(passwordEncoder.encode(request.getPassword()))
            .category(request.getCategory())
            .title(request.getTitle())
            .content(request.getContent())
            .isSecret(request.getIsSecret())
            .isReplied(false)
            .build();
            
        return inquiryRepository.save(inquiry);
    }
    
    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public List<Inquiry> getPendingInquiries() {
        return inquiryRepository.findByIsRepliedFalseOrderByCreatedAtDesc();
    }
    
    public Inquiry getInquiryById(Long id) {
        return inquiryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("문의를 찾을 수 없습니다"));
    }
    
    public boolean verifyPassword(Long id, String password) {
        Inquiry inquiry = getInquiryById(id);
        return passwordEncoder.matches(password, inquiry.getPassword());
    }
    
    public Inquiry getInquiryWithPasswordCheck(Long id, String password) {
        Inquiry inquiry = getInquiryById(id);
        
        // 비밀글인 경우 비밀번호 확인
        if (inquiry.getIsSecret()) {
            if (!passwordEncoder.matches(password, inquiry.getPassword())) {
                throw new RuntimeException("비밀번호가 일치하지 않습니다");
            }
        }
        
        return inquiry;
    }
    
    @Transactional
    public Inquiry updateInquiry(Long id, String password, InquiryRequest request) {
        Inquiry inquiry = getInquiryById(id);
        
        if (!passwordEncoder.matches(password, inquiry.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }
        
        inquiry.setCategory(request.getCategory());
        inquiry.setTitle(request.getTitle());
        inquiry.setContent(request.getContent());
        inquiry.setIsSecret(request.getIsSecret());
        
        return inquiryRepository.save(inquiry);
    }
    
    @Transactional
    public void deleteInquiry(Long id, String password) {
        Inquiry inquiry = getInquiryById(id);
        
        if (!passwordEncoder.matches(password, inquiry.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }
        
        inquiryRepository.delete(inquiry);
    }
    
    // 관리자용 - 답변 등록
    @Transactional
    public Inquiry addReply(Long id, ReplyRequest request) {
        Inquiry inquiry = getInquiryById(id);
        inquiry.setReply(request.getContent());
        inquiry.setIsReplied(true);
        inquiry.setRepliedAt(LocalDateTime.now());
        return inquiryRepository.save(inquiry);
    }
}
```

---

## 7. Controller 클래스

### ReservationController.java

```java
package com.happykidsland.controller;

import com.happykidsland.dto.ReservationRequest;
import com.happykidsland.entity.Reservation;
import com.happykidsland.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReservationController {
    
    private final ReservationService reservationService;
    
    @PostMapping
    public ResponseEntity<Reservation> createReservation(
        @Valid @RequestBody ReservationRequest request
    ) {
        Reservation reservation = reservationService.createReservation(request);
        return ResponseEntity.ok(reservation);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }
    
    @GetMapping
    public ResponseEntity<List<Reservation>> getReservationsByDate(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(reservationService.getReservationsByDate(date));
    }
    
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Map<String, String>> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok(Map.of("message", "예약이 취소되었습니다"));
    }
}
```

### NoticeController.java

```java
package com.happykidsland.controller;

import com.happykidsland.entity.Notice;
import com.happykidsland.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NoticeController {
    
    private final NoticeService noticeService;
    
    @GetMapping
    public ResponseEntity<List<Notice>> getAllNotices() {
        return ResponseEntity.ok(noticeService.getAllActiveNotices());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Notice> getNotice(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getNoticeById(id));
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Notice>> getNoticesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(noticeService.getNoticesByCategory(category));
    }
}
```

### InquiryController.java

```java
package com.happykidsland.controller;

import com.happykidsland.dto.InquiryRequest;
import com.happykidsland.dto.PasswordRequest;
import com.happykidsland.entity.Inquiry;
import com.happykidsland.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InquiryController {
    
    private final InquiryService inquiryService;
    
    // 문의 등록
    @PostMapping
    public ResponseEntity<Map<String, Object>> createInquiry(
        @Valid @RequestBody InquiryRequest request
    ) {
        Inquiry inquiry = inquiryService.createInquiry(request);
        return ResponseEntity.ok(Map.of(
            "message", "문의가 등록되었습니다",
            "id", inquiry.getId()
        ));
    }
    
    // 문의 목록 조회 (공개글만 내용 표시, 비밀글은 제목만)
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllInquiries() {
        List<Inquiry> inquiries = inquiryService.getAllInquiries();
        List<Map<String, Object>> result = inquiries.stream().map(inquiry -> Map.of(
            "id", (Object) inquiry.getId(),
            "title", inquiry.getTitle(),
            "author", maskName(inquiry.getAuthor()),
            "category", inquiry.getCategory(),
            "createdAt", inquiry.getCreatedAt().toString(),
            "isReplied", inquiry.getIsReplied(),
            "isSecret", inquiry.getIsSecret()
        )).toList();
        return ResponseEntity.ok(result);
    }
    
    // 문의 상세 조회 (비밀글인 경우 비밀번호 필요)
    @PostMapping("/{id}/view")
    public ResponseEntity<?> viewInquiry(
        @PathVariable Long id,
        @RequestBody(required = false) PasswordRequest request
    ) {
        try {
            Inquiry inquiry = inquiryService.getInquiryWithPasswordCheck(
                id, 
                request != null ? request.getPassword() : null
            );
            return ResponseEntity.ok(inquiry);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 비밀번호 확인
    @PostMapping("/{id}/verify")
    public ResponseEntity<Map<String, Boolean>> verifyPassword(
        @PathVariable Long id,
        @RequestBody PasswordRequest request
    ) {
        boolean isValid = inquiryService.verifyPassword(id, request.getPassword());
        return ResponseEntity.ok(Map.of("valid", isValid));
    }
    
    // 문의 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInquiry(
        @PathVariable Long id,
        @RequestParam String password,
        @Valid @RequestBody InquiryRequest request
    ) {
        try {
            inquiryService.updateInquiry(id, password, request);
            return ResponseEntity.ok(Map.of("message", "문의가 수정되었습니다"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 문의 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInquiry(
        @PathVariable Long id,
        @RequestParam String password
    ) {
        try {
            inquiryService.deleteInquiry(id, password);
            return ResponseEntity.ok(Map.of("message", "문의가 삭제되었습니다"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 이름 마스킹 (예: 홍길동 -> 홍**)
    private String maskName(String name) {
        if (name == null || name.length() < 2) return name;
        return name.charAt(0) + "*".repeat(name.length() - 1);
    }
}
```

### AdminInquiryController.java (관리자용)

```java
package com.happykidsland.controller;

import com.happykidsland.dto.ReplyRequest;
import com.happykidsland.entity.Inquiry;
import com.happykidsland.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/inquiries")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminInquiryController {
    
    private final InquiryService inquiryService;
    
    // 전체 문의 목록 (관리자용 - 모든 정보 포함)
    @GetMapping
    public ResponseEntity<List<Inquiry>> getAllInquiries() {
        return ResponseEntity.ok(inquiryService.getAllInquiries());
    }
    
    // 답변 대기 문의 목록
    @GetMapping("/pending")
    public ResponseEntity<List<Inquiry>> getPendingInquiries() {
        return ResponseEntity.ok(inquiryService.getPendingInquiries());
    }
    
    // 문의 상세 조회 (관리자용 - 비밀번호 불필요)
    @GetMapping("/{id}")
    public ResponseEntity<Inquiry> getInquiry(@PathVariable Long id) {
        return ResponseEntity.ok(inquiryService.getInquiryById(id));
    }
    
    // 답변 등록/수정
    @PostMapping("/{id}/reply")
    public ResponseEntity<Map<String, String>> addReply(
        @PathVariable Long id,
        @Valid @RequestBody ReplyRequest request
    ) {
        inquiryService.addReply(id, request);
        return ResponseEntity.ok(Map.of("message", "답변이 등록되었습니다"));
    }
}
```

---

## 8. CORS 설정

### CorsConfig.java

```java
package com.happykidsland.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
```

### SecurityConfig.java (Spring Security 설정)

```java
package com.happykidsland.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}
```

---

## 9. MariaDB 스키마 (schema.sql)

```sql
CREATE DATABASE IF NOT EXISTS happykidsland 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE happykidsland;

-- 예약 테이블
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    reservation_date DATE NOT NULL,
    reservation_time TIME NOT NULL,
    child_count INT NOT NULL DEFAULT 1,
    adult_count INT NOT NULL DEFAULT 1,
    memo TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    INDEX idx_reservation_date (reservation_date),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 공지사항 테이블
CREATE TABLE IF NOT EXISTS notices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    INDEX idx_category (category),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 문의 테이블 (게시판 형식)
CREATE TABLE IF NOT EXISTS inquiries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    author VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL, -- bcrypt 암호화
    category VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    is_secret BOOLEAN NOT NULL DEFAULT FALSE,
    is_replied BOOLEAN NOT NULL DEFAULT FALSE,
    reply TEXT,
    replied_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    INDEX idx_is_replied (is_replied),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 샘플 문의 데이터
INSERT INTO inquiries (author, password, category, title, content, is_secret, is_replied, reply, replied_at) VALUES
('김민수', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3s2k9LzNzBx8h8a.wGHa', '파티룸 예약', '파티룸 예약 관련 문의드립니다', '안녕하세요.\n\n아이 생일파티를 계획 중인데 파티룸 예약에 대해 몇 가지 문의드립니다.', FALSE, TRUE, '안녕하세요, 해피키즈랜드입니다.\n\n파티룸은 2시간 단위로 이용 가능합니다.', NOW()),
('이영희', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3s2k9LzNzBx8h8a.wGHa', '이용 관련', '주말 이용 시간 문의', '주말에는 몇 시까지 운영하나요?', TRUE, TRUE, '주말은 10시부터 21시까지 운영합니다.', NOW()),
('박철수', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3s2k9LzNzBx8h8a.wGHa', '예약 관련', '단체 예약 가능한가요?', '어린이집 단체로 방문하려고 합니다. 20명 정도인데 단체 예약이 가능할까요?', FALSE, FALSE, NULL, NULL);
-- 참고: 위 비밀번호 해시는 '1234'를 bcrypt로 암호화한 값입니다
INSERT INTO notices (title, content, category, is_active) VALUES
('2024년 설 연휴 운영 안내', '설 연휴 기간(2/9~2/12) 정상 운영합니다. 다만, 당일 예약이 조기 마감될 수 있으니 사전 예약을 권장드립니다.', '공지', TRUE),
('신규 체험 프로그램 오픈 안내', '새로운 체험 프로그램 "꼬마 요리사"가 오픈되었습니다. 매주 토요일 오후 2시에 진행됩니다.', '이벤트', TRUE),
('겨울방학 특별 이벤트 진행 중', '겨울방학을 맞아 특별 할인 이벤트를 진행합니다. 평일 방문 시 20% 할인!', '이벤트', TRUE),
('시설 리뉴얼 완료 안내', '대형 볼풀장 리뉴얼 공사가 완료되었습니다. 더욱 쾌적하고 안전한 환경에서 이용해 주세요.', '공지', TRUE);
```

---

## 실행 방법

### 1. MariaDB 설치 및 실행
```bash
# MacOS
brew install mariadb
brew services start mariadb

# Windows
# MariaDB 설치 후 서비스 시작

# 데이터베이스 생성
mysql -u root -p < schema.sql
```

### 2. 환경 변수 설정
```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password
```

### 3. Spring Boot 실행
```bash
./gradlew bootRun
```

### 4. API 테스트
```bash
# 예약 생성
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "name": "홍길동",
    "phone": "010-1234-5678",
    "reservationDate": "2024-02-15",
    "reservationTime": "14:00",
    "childCount": 2,
    "adultCount": 1
  }'

# 공지사항 조회
curl http://localhost:8080/api/notices

# 문의 등록
curl -X POST http://localhost:8080/api/inquiries \
  -H "Content-Type: application/json" \
  -d '{
    "author": "홍길동",
    "password": "1234",
    "category": "예약 관련",
    "title": "주말 예약 문의",
    "content": "주말 예약 가능한지 문의드립니다.",
    "isSecret": false
  }'

# 문의 목록 조회
curl http://localhost:8080/api/inquiries

# 문의 상세 조회 (비밀글인 경우 비밀번호 필요)
curl -X POST http://localhost:8080/api/inquiries/1/view \
  -H "Content-Type: application/json" \
  -d '{"password": "1234"}'

# 관리자 - 답변 등록
curl -X POST http://localhost:8080/api/admin/inquiries/1/reply \
  -H "Content-Type: application/json" \
  -d '{"content": "안녕하세요, 주말 예약 가능합니다."}'
```
