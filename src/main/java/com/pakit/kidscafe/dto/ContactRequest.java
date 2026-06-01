package com.pakit.kidscafe.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequest {

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @NotBlank(message = "연락처는 필수입니다")
    private String phone;

    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "문의 유형을 선택해 주세요")
    private String category;

    @NotBlank(message = "문의 내용을 입력해 주세요")
    @Size(min = 10, message = "문의 내용은 최소 10자 이상이어야 합니다")
    private String message;
}