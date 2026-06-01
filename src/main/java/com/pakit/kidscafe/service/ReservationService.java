package com.pakit.kidscafe.service;

import com.pakit.kidscafe.dto.ReservationRequest;
import com.pakit.kidscafe.entity.Reservation;
import com.pakit.kidscafe.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
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

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
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

    @Transactional
    public void updateStatus(Long id, Reservation.ReservationStatus newStatus) { // 🌟 타입 매핑 확인
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약 번호입니다. id=" + id));

        reservation.setStatus(newStatus); // Dirty Checking에 의해 알아서 DB 업데이터 반영됨
    }

    // 1. 이름과 전화번호로 일치하는 예약 찾기
    public Reservation getReservationByNameAndPhone(String name, String phone) {
        return reservationRepository.findByNameAndPhone(name, phone)
                .orElseThrow(() -> new RuntimeException("일치하는 예약 정보가 존재하지 않습니다."));
    }

    // 2. 예약 정보 변경
    @Transactional
    public void updateReservationDetails(Long id, LocalDate date, LocalTime time, Integer guests) {
        Reservation reservation = getReservationById(id);
        // 🌟 실제 엔티티 세터 필드명 명세 반영
        reservation.setReservationDate(date);
        reservation.setReservationTime(time);

        // 키즈카페 특성상 guests 인원을 성인/아동 중 어디로 넣을지 모호하므로,
        // 여기서는 기본적으로 성인 인원(adultCount)을 조정하거나 비즈니스 규칙에 맞게 쪼개 넣으시면 됩니다!
        reservation.setAdultCount(guests);
    }
}