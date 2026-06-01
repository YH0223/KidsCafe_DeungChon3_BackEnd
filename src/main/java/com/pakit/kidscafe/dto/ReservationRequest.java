package com.pakit.kidscafe.dto;

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