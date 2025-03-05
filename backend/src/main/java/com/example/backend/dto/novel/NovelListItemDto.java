package com.example.backend.dto.novel;

import com.example.backend.enums.NovelStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class NovelListItemDto {
    private Long id;
    private String name;
    private String cover;
    private NovelStatus status;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdateDate;
}
