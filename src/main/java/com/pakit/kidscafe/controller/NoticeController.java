package com.pakit.kidscafe.controller;

import com.pakit.kidscafe.entity.Notice;
import com.pakit.kidscafe.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
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

    // 🌟 [수정됨] 공지사항 등록 (POST)
    @PostMapping
    public ResponseEntity<Notice> createNotice(@RequestBody Map<String, Object> body) {
        // 1. 프론트엔드가 보낸 JSON 데이터를 Map으로 안전하게 꺼냅니다.
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        Boolean isPinned = (Boolean) body.get("isPinned");

        // 2. 프론트의 isPinned 여부에 따라 엔티티의 category(필수값)를 자동 지정해줍니다.
        String category = (isPinned != null && isPinned) ? "중요" : "안내";

        // 3. 엔티티의 @Builder를 사용해 실제 DB 규격에 맞게 조립합니다. (author 필드 제거)
        Notice newNotice = Notice.builder()
                .title(title)
                .content(content)
                .category(category)
                .isActive(true) // 기본값 활성화
                .build();

        // 4. 서비스 단에 넘겨 저장 후 리턴
        Notice savedNotice = noticeService.createNotice(newNotice);
        return ResponseEntity.ok(savedNotice);
    }

    // 공지사항 삭제 (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.ok().build();
    }
}