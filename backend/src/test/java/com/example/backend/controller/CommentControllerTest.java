package com.example.backend.controller;

import com.example.backend.dto.comment.CommentRequestDto;
import com.example.backend.dto.comment.CommentResponseDto;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.lettuce.core.KillArgs.Builder.user;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("GetCommentsByNovel Tests")
    class GetCommentsByNovelTests {

        @Test
        void whenCommentsFoundForNovel_ShouldReturnPageOfComments() throws Exception {
            long novelId = 1L;
            int page = 0;
            int size = 10;
            List<CommentResponseDto> commentList = Arrays.asList(
                    createCommentResponseDto(1L, "Great novel!"),
                    createCommentResponseDto(2L, "Really enjoyed it!")
            );
            Pageable pageable = PageRequest.of(page, size);
            Page<CommentResponseDto> commentPage = new PageImpl<>(commentList, pageable, commentList.size());
            when(commentService.getCommentsByNovelId(page, size, novelId)).thenReturn(commentPage);

            mockMvc.perform(get("/comments/novel/{novel-id}", novelId)
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].id").value(1))
                    .andExpect(jsonPath("$.content[0].content").value("Great novel!"))
                    .andExpect(jsonPath("$.content[1].id").value(2))
                    .andExpect(jsonPath("$.content[1].content").value("Really enjoyed it!"));

            verify(commentService).getCommentsByNovelId(page, size, novelId);
        }

        @Test
        void whenCommentsNotFoundForNovel_ShouldReturnEmptyPage() throws Exception {
            long novelId = 1L;
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);
            Page<CommentResponseDto> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(commentService.getCommentsByNovelId(page, size, novelId)).thenReturn(emptyPage);

            mockMvc.perform(get("/comments/novel/{novel-id}", novelId)
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(commentService).getCommentsByNovelId(page, size, novelId);
        }

        @Test
        void whenInvalidPage_ShouldReturnBadRequest() throws Exception {
            mockMvc.perform(get("/comments/novel/{novel-id}", 1L)
                            .param("page", "-1")
                            .param("size", "10"))
                    .andExpect(status().isBadRequest());

            verify(commentService, never()).getCommentsByNovelId(anyInt(), anyInt(), anyLong());
        }

        @Test
        void whenInvalidSize_ShouldReturnBadRequest() throws Exception {
            mockMvc.perform(get("/comments/novel/{novel-id}", 1L)
                            .param("page", "1")
                            .param("size", "-1"))
                    .andExpect(status().isBadRequest());

            verify(commentService, never()).getCommentsByNovelId(anyInt(), anyInt(), anyLong());
        }
    }

    @Nested
    @DisplayName("GetCommentsByChapter Tests")
    class GetCommentsByChapterTests {

        @Test
        void whenCommentsFoundForChapter_ShouldReturnPageOfComments() throws Exception {
            long chapterId = 1L;
            int page = 0;
            int size = 10;
            List<CommentResponseDto> commentList = Arrays.asList(
                    createCommentResponseDto(1L, "Amazing chapter!"),
                    createCommentResponseDto(2L, "Can't wait for the next one!")
            );
            Pageable pageable = PageRequest.of(page, size);
            Page<CommentResponseDto> commentPage = new PageImpl<>(commentList, pageable, commentList.size());
            when(commentService.getCommentsByChapterId(page, size, chapterId)).thenReturn(commentPage);

            mockMvc.perform(get("/comments/chapter/{chapter-id}", chapterId)
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].id").value(1))
                    .andExpect(jsonPath("$.content[0].content").value("Amazing chapter!"))
                    .andExpect(jsonPath("$.content[1].id").value(2))
                    .andExpect(jsonPath("$.content[1].content").value("Can't wait for the next one!"));

            verify(commentService).getCommentsByChapterId(page, size, chapterId);
        }

        @Test
        void whenCommentsNotFoundForChapter_ShouldReturnEmptyPage() throws Exception {
            long chapterId = 1L;
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);
            Page<CommentResponseDto> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(commentService.getCommentsByChapterId(page, size, chapterId)).thenReturn(emptyPage);

            mockMvc.perform(get("/comments/chapter/{chapter-id}", chapterId)
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(commentService).getCommentsByChapterId(page, size, chapterId);
        }

        @Test
        void whenInvalidPageOrSizeForChapter_ShouldReturnBadRequest() throws Exception {
            long chapterId = 1L;
            mockMvc.perform(get("/comments/chapter/{chapter-id}", chapterId)
                            .param("page", "-1")
                            .param("size", "10"))
                    .andExpect(status().isBadRequest());
            verify(commentService, never()).getCommentsByChapterId(anyInt(), anyInt(), anyLong());

            mockMvc.perform(get("/comments/chapter/{chapter-id}", chapterId)
                            .param("page", "0")
                            .param("size", "0"))
                    .andExpect(status().isBadRequest());

            verify(commentService, never()).getCommentsByChapterId(anyInt(), anyInt(), anyLong());
        }
    }

    @Nested
    @DisplayName("DeleteComment Tests")
    class DeleteCommentTests {

//        @Test
//        void whenCommentExists_ShouldDeleteCommentAndReturnNoContent() throws Exception {
//            long commentId = 1L;
//            doNothing().when(commentService).deleteComment(commentId);
//
//            mockMvc.perform(delete("/comments/{comment-id}", commentId)
//                            .with(user("admin").roles("ADMIN")))
//                    .andExpect(status().isNoContent());
//
//            verify(commentService).deleteComment(commentId);
//        }
    }

    @Nested
    @DisplayName("InsertComment Tests")
    class InsertCommentTests {

//        @Test
//        void whenInsertComment_ShouldReturnCreatedComment() throws Exception {
//            CommentRequestDto requestDto = createCommentRequestDto();
//            CommentResponseDto responseDto = createCommentResponseDto(1L, "Nice read!");
//            when(commentService.insertComment(any(CommentRequestDto.class))).thenReturn(responseDto);
//
//            mockMvc.perform(post("/comments")
//                            .with(user("commenter").authorities(new SimpleGrantedAuthority("COMMENT")))
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(requestDto)))
//                    .andExpect(status().isCreated())
//                    .andExpect(jsonPath("$.id").value(1))
//                    .andExpect(jsonPath("$.content").value("Nice read!"));
//
//            verify(commentService).insertComment(any(CommentRequestDto.class));
//        }
    }

    private CommentResponseDto createCommentResponseDto(Long id, String content) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(id);
        dto.setContent(content);
        return dto;
    }

    private CommentRequestDto createCommentRequestDto() {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setContent("Content");
        dto.setNovelId(1L);
        dto.setChapterId(null);
        return dto;
    }
}

