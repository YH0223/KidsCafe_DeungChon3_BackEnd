package com.pakit.kidscafe.controller;

import com.pakit.kidscafe.dto.ReservationRequest;
import com.pakit.kidscafe.entity.Reservation;
import com.pakit.kidscafe.service.ReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*") // 클래스 단 전체 허용 보완
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
    public ResponseEntity<?> getReservations(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (date == null) {
            return ResponseEntity.ok(reservationService.getAllReservations());
        }
        return ResponseEntity.ok(reservationService.getReservationsByDate(date));
    }

    // 🌟 기존 취소 전용에서 [확정/취소 동적 상태 변경 API]로 전면 업그레이드
    @PatchMapping("/{id}/status")
    @CrossOrigin(origins = "*", methods = {RequestMethod.PATCH, RequestMethod.OPTIONS})
    public ResponseEntity<?> updateReservationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String statusStr = body.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "status 값이 누락되었습니다."));
        }

        try {
            // 🌟 엔티티 내부에 선언된 Enum이므로 Reservation.ReservationStatus 로 가치 변환을 합니다.
            Reservation.ReservationStatus newStatus = Reservation.ReservationStatus.valueOf(statusStr.toUpperCase());

            // 서비스단 호출
            reservationService.updateStatus(id, newStatus);

            return ResponseEntity.ok(Map.of("message", "예약 상태가 " + statusStr + "(으)로 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            // 프론트가 엉뚱한 문자열을 보냈을 때 예외 처리
            return ResponseEntity.badRequest().body(Map.of("message", "올바르지 않은 상태 규격입니다."));
        }
    }

    // 1. 예약자 이름과 전화번호로 예약 정보 매칭 및 단건 조회 (POST /api/reservations/auth)
    @PostMapping("/auth")
    public ResponseEntity<?> authenticateReservation(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String phone = body.get("phone");

        if (name == null || phone == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "예약자명과 전화번호를 모두 입력해 주세요."));
        }

        try {
            // 💡 서비스 단 메서드 (기존 필드명에 맞춤)
            Reservation reservation = reservationService.getReservationByNameAndPhone(name, phone);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 2. 예약 내용 직접 변경 (PUT /api/reservations/{id})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        try {
            // 🌟 엔티티 규격에 맞춘 날짜/시간 파싱
            LocalDate date = LocalDate.parse((String) body.get("date"));
            LocalTime time = LocalTime.parse((String) body.get("time") + ":00");
            Integer guests = (Integer) body.get("guests");

            // 💡 서비스단 호출 (엔티티의 정확한 컬럼 수정 지시)
            reservationService.updateReservationDetails(id, date, time, guests);

            return ResponseEntity.ok(Map.of("message", "예약이 성공적으로 변경되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "예약 변경 중 오류가 발생했습니다."));
        }
    }


}