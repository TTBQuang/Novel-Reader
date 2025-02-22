package com.example.backend.service;

import com.example.backend.dto.comment.CommentRequestDto;
import com.example.backend.dto.comment.CommentResponseDto;
import com.example.backend.entity.Comment;
import com.example.backend.entity.User;
import com.example.backend.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private CommentResponseDto commentResponseDto;
    private CommentRequestDto commentRequestDto;
    private Page<Comment> commentPage;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);

        comment = new Comment();
        comment.setId(1L);
        comment.setUser(user);
        comment.setContent("Test comment");

        commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(1L);
        commentResponseDto.setContent("Test comment");

        commentRequestDto = new CommentRequestDto();
        commentRequestDto.setContent("Test comment");

        commentPage = new PageImpl<>(Collections.singletonList(comment));
    }

    @Nested
    @DisplayName("getCommentsByNovelId Tests")
    class GetCommentsByNovelIdTests {

        @Test
        void whenCommentsExists_ShouldReturnCommentsPage() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
            when(commentRepository.findByNovelId(1L, pageable)).thenReturn(commentPage);
            when(modelMapper.map(comment, CommentResponseDto.class)).thenReturn(commentResponseDto);

            Page<CommentResponseDto> result = commentService.getCommentsByNovelId(0, 10, 1L);

            assertEquals(1, result.getTotalElements());
            verify(commentRepository).findByNovelId(1L, pageable);
            verify(modelMapper).map(any(Comment.class), eq(CommentResponseDto.class));
        }

        @Test
        void whenCommentsNotExists_ShouldReturnEmptyPage() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
            when(commentRepository.findByNovelId(1L, pageable)).thenReturn(Page.empty());

            Page<CommentResponseDto> result = commentService.getCommentsByNovelId(0, 10, 1L);

            assertEquals(0, result.getTotalElements());
            verify(commentRepository).findByNovelId(1L, pageable);
            verify(modelMapper, never()).map(any(Comment.class), eq(CommentResponseDto.class));
        }

        @Test
        void whenMappingFails_ShouldThrowException() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
            when(commentRepository.findByNovelId(1L, pageable)).thenReturn(commentPage);
            when(modelMapper.map(comment, CommentResponseDto.class)).thenThrow(new RuntimeException());

            assertThrows(RuntimeException.class, () -> commentService.getCommentsByNovelId(0, 10, 1L));

            verify(commentRepository).findByNovelId(1L, pageable);
            verify(modelMapper).map(any(Comment.class), eq(CommentResponseDto.class));
        }
    }

    @Nested
    @DisplayName("getCommentsByChapterId Tests")
    class GetCommentsByChapterIdTests {

        @Test
        void whenCommentsExists_ShouldReturnCommentsPage() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
            when(commentRepository.findByChapterId(1L, pageable)).thenReturn(commentPage);
            when(modelMapper.map(comment, CommentResponseDto.class)).thenReturn(commentResponseDto);

            Page<CommentResponseDto> result = commentService.getCommentsByChapterId(0, 10, 1L);

            assertEquals(1, result.getTotalElements());
            verify(commentRepository).findByChapterId(1L, pageable);
            verify(modelMapper).map(any(Comment.class), eq(CommentResponseDto.class));
        }

        @Test
        void whenCommentsNotExists_ShouldReturnEmptyPage() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
            when(commentRepository.findByChapterId(1L, pageable)).thenReturn(Page.empty());

            Page<CommentResponseDto> result = commentService.getCommentsByChapterId(0, 10, 1L);

            assertEquals(0, result.getTotalElements());
            verify(commentRepository).findByChapterId(1L, pageable);
            verify(modelMapper, never()).map(any(Comment.class), eq(CommentResponseDto.class));
        }

        @Test
        void whenMappingFails_ShouldThrowException() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
            when(commentRepository.findByChapterId(1L, pageable)).thenReturn(commentPage);
            when(modelMapper.map(comment, CommentResponseDto.class)).thenThrow(new RuntimeException());

            assertThrows(RuntimeException.class, () -> commentService.getCommentsByChapterId(0, 10, 1L));
            verify(commentRepository).findByChapterId(1L, pageable);
            verify(modelMapper).map(any(Comment.class), eq(CommentResponseDto.class));
        }
    }

    @Nested
    @DisplayName("isCommentOwner Tests")
    class IsCommentOwnerTests {

        @Test
        void whenCommentExists_AndUserIsOwner_ShouldReturnTrue() {
            when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

            boolean result = commentService.isCommentOwner(1L, "1");

            assertTrue(result);
            verify(commentRepository).findById(1L);
        }

        @Test
        void whenCommentExists_AndUserIsNotOwner_ShouldReturnFalse() {
            when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

            boolean result = commentService.isCommentOwner(1L, "2");

            assertFalse(result);
            verify(commentRepository).findById(1L);
        }

        @Test
        void whenCommentNotExists_ShouldReturnFalse() {
            when(commentRepository.findById(1L)).thenReturn(Optional.empty());

            boolean result = commentService.isCommentOwner(1L, "1");

            assertFalse(result);
            verify(commentRepository).findById(1L);
        }
    }

    @Nested
    @DisplayName("deleteComment Tests")
    class DeleteCommentTests {

        @Test
        void whenCommentExists_ShouldDeleteComment() {
            when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

            commentService.deleteComment(1L);

            verify(commentRepository).findById(1L);
            verify(commentRepository).delete(comment);
        }

        @Test
        void whenCommentNotExists_ShouldThrowException() {
            when(commentRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(1L));

            verify(commentRepository).findById(1L);
            verify(commentRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("insertComment Tests")
    class InsertCommentTests {

        @BeforeEach
        void setUpSecurityContext() {
            SecurityContextHolder.setContext(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
        }

        @AfterEach
        void clearSecurityContext() {
            SecurityContextHolder.clearContext();
        }

        @Test
        void whenUserAuthenticated_ShouldInsertComment() {
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getName()).thenReturn("1");
            when(modelMapper.map(commentRequestDto, Comment.class)).thenReturn(comment);
            when(commentRepository.save(comment)).thenReturn(comment);
            when(commentRepository.getCommentDetail(1L)).thenReturn(comment);
            when(modelMapper.map(comment, CommentResponseDto.class)).thenReturn(commentResponseDto);

            CommentResponseDto result = commentService.insertComment(commentRequestDto);

            assertNotNull(result);
            assertEquals(commentResponseDto.getId(), result.getId());
            verify(commentRepository).save(comment);
            verify(commentRepository).getCommentDetail(1L);
        }

        @Test
        void whenUserNotAuthenticated_ShouldThrowException() {
            when(authentication.isAuthenticated()).thenReturn(false);

            assertThrows(RuntimeException.class, () -> commentService.insertComment(commentRequestDto));

            verify(commentRepository, never()).save(any());
            verify(commentRepository, never()).getCommentDetail(anyLong());
        }

        @Test
        void whenAuthenticationNull_ShouldThrowException() {
            when(securityContext.getAuthentication()).thenReturn(null);

            assertThrows(RuntimeException.class, () -> commentService.insertComment(commentRequestDto));

            verify(commentRepository, never()).save(any());
            verify(commentRepository, never()).getCommentDetail(anyLong());
        }
    }
}
