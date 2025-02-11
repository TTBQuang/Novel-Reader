package com.example.backend.enums;

import com.example.backend.exception.InvalidNovelStatusException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NovelStatus {
    DANG_TIEN_HANH("Đang tiến hành"),
    TAM_NGUNG("Tạm ngưng"),
    DA_HOAN_THANH("Đã hoàn thành");

    private final String status;

    public static NovelStatus fromValue(String value) {
        for (NovelStatus status : values()) {
            if (status.status.equals(value)) {
                return status;
            }
        }
        throw new InvalidNovelStatusException(value);
    }
}
