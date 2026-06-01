package com.pakit.kidscafe.repository;

import com.pakit.kidscafe.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByReservationDate(LocalDate date);

    List<Reservation> findByPhone(String phone);

    List<Reservation> findByReservationDateAndStatus(
            LocalDate date,
            Reservation.ReservationStatus status
    );

    Optional<Reservation> findByNameAndPhone(String name, String phone);
}