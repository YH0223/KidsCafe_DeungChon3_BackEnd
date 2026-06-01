package com.pakit.kidscafe.service;

import com.pakit.kidscafe.dto.InquiryRequest;
import com.pakit.kidscafe.dto.ReplyRequest;
import com.pakit.kidscafe.entity.Inquiry;
import com.pakit.kidscafe.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final PasswordEncoder passwordEncoder; // BCryptPasswordEncoder의 상위 인터페이스 사용 권장

    /**
     * 문의사항 등록
     */
    @Transactional
    public Inquiry createInquiry(InquiryRequest request) {
        Inquiry inquiry = Inquiry.builder()
                .author(request.getAuthor())
                .password(passwordEncoder.encode(request.getPassword())) // 암호화하여 저장
                .category(request.getCategory())
                .title(request.getTitle())
                .content(request.getContent())
                .isSecret(request.getIsSecret())
                .build();

        return inquiryRepository.save(inquiry);
    }

    /**
     * 전체 목록 조회 (최신순)
     */
    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 답변 대기 중인 목록 조회 (최신순)
     */
    public List<Inquiry> getPendingInquiries() {
        return inquiryRepository.findByIsRepliedFalseOrderByCreatedAtDesc();
    }

    /**
     * 단건 조회 (Id 기반 기본 조회)
     */
    public Inquiry getInquiryById(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다. ID: " + id));
    }

    /**
     * 단순 비밀번호 일치 여부 확인 (boolean 반환)
     */
    public boolean verifyPassword(Long id, String password) {
        Inquiry inquiry = getInquiryById(id);
        return passwordEncoder.matches(password, inquiry.getPassword());
    }

    /**
     * 비밀글 접근 및 조회 상세 (비밀글인 경우만 체크)
     */
    public Inquiry getInquiryWithPasswordCheck(Long id, String password) {
        Inquiry inquiry = getInquiryById(id);

        if (inquiry.getIsSecret()) {
            inquiry.authenticate(password, passwordEncoder); // 검증 위임
        }
        return inquiry;
    }

    /**
     * 문의사항 수정 (Dirty Checking 활용)
     */
    @Transactional
    public Inquiry updateInquiry(Long id, String password, InquiryRequest request) {
        Inquiry inquiry = getInquiryById(id);
        inquiry.authenticate(password, passwordEncoder); // 1. 비밀번호 검증

        // 2. 비즈니스 메서드로 수정 (save() 명시 불필요 - 변경 감지로 인해 자동 반영)
        inquiry.update(
                request.getCategory(),
                request.getTitle(),
                request.getContent(),
                request.getIsSecret()
        );
        return inquiry;
    }

    /**
     * 문의사항 삭제
     */
    @Transactional
    public void deleteInquiry(Long id, String password) {
        Inquiry inquiry = getInquiryById(id);
        inquiry.authenticate(password, passwordEncoder); // 비밀번호 검증 후 삭제

        inquiryRepository.delete(inquiry);
    }

    /**
     * 관리자 전용 - 답변 등록/수정 (Dirty Checking 활용)
     */
    @Transactional
    public Inquiry addReply(Long id, ReplyRequest request) {
        Inquiry inquiry = getInquiryById(id);
        inquiry.answer(request.getContent()); // 엔티티 내 메서드 실행

        return inquiry;
    }
}