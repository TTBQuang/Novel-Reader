package com.example.backend.dto.chapter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterDetailDto {
    private Long id;
    private String chapterGroupName;
    private String name;
    private String content;
    private long wordsCount;
    private double chapterOrder;
    private LocalDateTime creationDate;
    private int commentCount;
}
