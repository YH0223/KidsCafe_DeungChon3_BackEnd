package com.pakit.kidscafe.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryRequest {

    @NotBlank(message = "작성자명은 필수입니다")
    private String author;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다")
    private String password;

    @NotBlank(message = "문의 유형을 선택해 주세요")
    private String category;

    @NotBlank(message = "제목을 입력해 주세요")
    private String title;

    @NotBlank(message = "내용을 입력해 주세요")
    @Size(min = 10, message = "내용은 10자 이상이어야 합니다")
    private String content;

    private Boolean isSecret = false;
}