package com.example.backend.dto.chaptergroup;

import com.example.backend.dto.chapter.ChapterListItemDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChapterGroupDto {
    private Long id;
    private String name;
    private double groupOrder;
    private List<ChapterListItemDto> chapters;
}
