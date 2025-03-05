package com.example.backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateDisplayNameRequest {
    @NotBlank(message = "Tên hiển thị không được để trống")
    private String displayName;
}
