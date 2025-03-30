package com.example.backend.controller;

import com.example.backend.dto.comment.CommentRequestDto;
import com.example.backend.dto.comment.CommentResponseDto;
import com.example.backend.service.CommentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/novel/{novel-id}")
    public ResponseEntity<Page<CommentResponseDto>> getCommentsByNovelId(
            @PathVariable("novel-id") long novelId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        Page<CommentResponseDto> comments = commentService.getCommentsByNovelId(page, size, novelId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/chapter/{chapter-id}")
    public ResponseEntity<Page<CommentResponseDto>> getCommentsByChapterId(
            @PathVariable("chapter-id") long chapterId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        Page<CommentResponseDto> comments = commentService.getCommentsByChapterId(page, size, chapterId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{comment-id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or @commentService.isCommentOwner(#commentId, authentication.name)")
    public ResponseEntity<Void> deleteComment(@PathVariable("comment-id") long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('COMMENT')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CommentResponseDto> insertComment(@RequestBody CommentRequestDto commentRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Không xác thực được người dùng");
        }
        Long userId = Long.valueOf(authentication.getName());

        CommentResponseDto response = commentService.insertComment(userId, commentRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
