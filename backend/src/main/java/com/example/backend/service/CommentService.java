package com.example.backend.service;

import com.example.backend.dto.comment.CommentRequestDto;
import com.example.backend.dto.comment.CommentResponseDto;
import com.example.backend.entity.Comment;
import com.example.backend.entity.User;
import com.example.backend.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    public Page<CommentResponseDto> getCommentsByNovelId(int page, int size, long novelId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> commentPage = commentRepository.findByNovelId(novelId, pageable);
        return commentPage.map(comment -> modelMapper.map(comment, CommentResponseDto.class));
    }

    public Page<CommentResponseDto> getCommentsByChapterId(int page, int size, long chapterId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> commentPage = commentRepository.findByChapterId(chapterId, pageable);
        return commentPage.map(comment -> modelMapper.map(comment, CommentResponseDto.class));
    }

    public boolean isCommentOwner(long commentId, String userId) {
        return commentRepository.findById(commentId)
                .map(comment -> userId.equals(String.valueOf(comment.getUser().getId())))
                .orElse(false);
    }

    public void deleteComment(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        commentRepository.delete(comment);
    }

    public CommentResponseDto insertComment(CommentRequestDto commentRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Không xác thực được người dùng");
        }
        String userIdString = authentication.getName();
        User user = new User();
        user.setId(Long.valueOf(userIdString));

        Comment comment = modelMapper.map(commentRequestDto, Comment.class);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);
        Comment detailedComment = commentRepository.getCommentDetail(savedComment.getId());
        return modelMapper.map(detailedComment, CommentResponseDto.class);
    }
}
