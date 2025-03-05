package com.example.backend.dto.user;

import com.example.backend.dto.novel.NovelListItemDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailDto {
    private Long id;
    private String email;
    private String displayName;
    private String avatar;
    private String coverImage;
    private boolean isAdmin;
    private boolean isCommentBlocked;
    private LocalDateTime createdAt;
    private int CommentsCount;
    private List<NovelListItemDto> ownNovels;
    private int chaptersCount;
}
