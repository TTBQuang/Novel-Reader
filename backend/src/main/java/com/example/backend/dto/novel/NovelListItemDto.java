package com.example.backend.dto.novel;

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
}
