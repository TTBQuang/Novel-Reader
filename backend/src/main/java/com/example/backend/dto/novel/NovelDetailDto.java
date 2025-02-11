package com.example.backend.dto.novel;

import com.example.backend.dto.chaptergroup.ChapterGroupDto;
import com.example.backend.dto.genre.GenreDto;
import com.example.backend.dto.user.UserDto;
import com.example.backend.enums.NovelStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class NovelDetailDto {
    private Long id;
    private UserDto poster;
    private List<ChapterGroupDto> chapterGroups;
    private String name;
    private String author;
    private String artist;
    private String cover;
    private String summary;
    private NovelStatus status;
    private long wordsCount;
    private LocalDateTime lastUpdateDate;
    private Set<GenreDto> genres;
}
