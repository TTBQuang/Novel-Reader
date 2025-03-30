package com.example.backend.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserBasicInfoDto {
    private Long id;
    private String email;
    private String username;
    private boolean isCommentBlocked;
    private String displayName;
    private String avatar;
    private boolean isAdmin;
}
