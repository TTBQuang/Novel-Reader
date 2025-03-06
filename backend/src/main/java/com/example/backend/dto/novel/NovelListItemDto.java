package com.example.backend.dto.novel;

import com.example.backend.enums.NovelStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NovelListItemDto {
    private Long id;
    private String name;
    private String cover;
    private String summary;
    private NovelStatus status;
    private long wordsCount;
}
