package com.example.backend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NovelSortOption {
    AZ,
    ZA,
    CREATION_DATE,
    LAST_UPDATE_DATE,
    WORDS_COUNT
}
