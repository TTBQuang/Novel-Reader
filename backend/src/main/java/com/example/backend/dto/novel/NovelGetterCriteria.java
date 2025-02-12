package com.example.backend.dto.novel;

import com.example.backend.enums.NovelSortOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class NovelGetterCriteria {
    private String keyword;
    private NovelSortOption sortOption;
    private String status;
    private String genreIds;
}
