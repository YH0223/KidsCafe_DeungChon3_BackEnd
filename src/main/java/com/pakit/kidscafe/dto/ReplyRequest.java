package com.pakit.kidscafe.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyRequest {

    @NotBlank(message = "답변 내용을 입력해 주세요")
    private String content;
}