package com.example.backend.dto.novel;

import com.example.backend.enums.NovelSortOption;
import lombok.Getter;

@Getter
public class NovelGetterCriteria {
    private String keyword;
    private NovelSortOption sortOption;
    private String status;
    private String genreIds;
}
