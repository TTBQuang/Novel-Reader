package com.example.backend.dto.chapter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChapterBasicInfoDto {
    private Long id;
    private String name;
    private double chapterOrder;
    private LocalDateTime creationDate;
}
