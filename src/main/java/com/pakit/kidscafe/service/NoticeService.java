package com.pakit.kidscafe.service;

import com.pakit.kidscafe.entity.Notice;
import com.pakit.kidscafe.repository.NoticeRepository;
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

    // 🌟 [추가됨] 공지사항 등록 (트랜잭션 쓰기)
    @Transactional
    public Notice createNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    // 🌟 [추가됨] 공지사항 삭제 (트랜잭션 쓰기)
    @Transactional
    public void deleteNotice(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 공지사항입니다.");
        }
        noticeRepository.deleteById(id);
    }
}

