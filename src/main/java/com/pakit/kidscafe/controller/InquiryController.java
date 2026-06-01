package com.pakit.kidscafe.controller;

import com.pakit.kidscafe.dto.InquiryRequest;
import com.pakit.kidscafe.dto.PasswordRequest;
import com.pakit.kidscafe.entity.Inquiry;
import com.pakit.kidscafe.service.InquiryService;
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
    @GetMapping("/{id}")
    public ResponseEntity<Inquiry> getInquiry(@PathVariable Long id) {
        // 비밀글 여부 판단과 가림막 처리는 프론트엔드가 훅에서 자동으로 해줍니다!
        Inquiry inquiry = inquiryService.getInquiryById(id);
        return ResponseEntity.ok(inquiry);
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<Boolean> verifyPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String password = body.get("password");
        if (password == null) {
            return ResponseEntity.badRequest().body(false);
        }

        boolean isValid = inquiryService.verifyPassword(id, password);
        return ResponseEntity.ok(isValid); // 프론트로 순수 true / false 리턴
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