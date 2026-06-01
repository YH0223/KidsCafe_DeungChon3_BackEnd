package com.pakit.kidscafe.controller;

import com.pakit.kidscafe.dto.ReplyRequest;
import com.pakit.kidscafe.entity.Inquiry;
import com.pakit.kidscafe.service.InquiryService;
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