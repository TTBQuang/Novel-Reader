package com.example.backend.dto.comment;

import com.example.backend.dto.user.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private UserDto user;
    private String content;
    private LocalDateTime createdAt;
}
