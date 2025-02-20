package com.example.backend.controller;

import com.example.backend.dto.chapter.ChapterListItemDto;
import com.example.backend.dto.chaptergroup.ChapterGroupDto;
import com.example.backend.dto.genre.GenreDto;
import com.example.backend.dto.novel.NovelDetailDto;
import com.example.backend.dto.user.UserDto;
import com.example.backend.enums.NovelStatus;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.service.NovelService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class NovelControllerTest {
    private MockMvc mockMvc;

    @Mock
    private NovelService novelService;

    @InjectMocks
    private NovelController novelController;

    private NovelDetailDto novelDetailDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(novelController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        novelDetailDto = createNovelDetailDto();
    }

    @Test
    void getChapterDetail_ShouldReturnChapterDetailDto() throws Exception {
        when(novelService.getNovelDetail(eq(1L))).thenReturn(novelDetailDto);

        mockMvc.perform(get("/novels/{novel-id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Test novel fields
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.author").value("Author 1"))
                // Test poster fields
                .andExpect(jsonPath("$.poster.id").value(1))
                .andExpect(jsonPath("$.poster.email").value("user@example.com"))
                .andExpect(jsonPath("$.poster.admin").value(false))
                // Test chapter groups
                .andExpect(jsonPath("$.chapterGroups[0].id").value(1))
                .andExpect(jsonPath("$.chapterGroups[0].name").value("Volume 1"))
                .andExpect(jsonPath("$.chapterGroups[0].groupOrder").value(1.0))
                .andExpect(jsonPath("$.chapterGroups[0].chapters[0].id").value(1))
                .andExpect(jsonPath("$.chapterGroups[0].chapters[0].creationDate").exists())
                // Test status
                .andExpect(jsonPath("$.status").value("DANG_TIEN_HANH"))
                // Test lastUpdateDate
                .andExpect(jsonPath("$.lastUpdateDate").exists())
                // Test genres
                .andExpect(jsonPath("$.genres", hasSize(2)))
                .andExpect(jsonPath("$.genres[*].name", containsInAnyOrder("Fantasy", "Adventure")));

        verify(novelService, times(1)).getNovelDetail(eq(1L));
    }

    @Test
    void getChapterDetail_WhenChapterNotFound_ShouldReturnNotFound() throws Exception {
        when(novelService.getNovelDetail(eq(1L)))
                .thenThrow(new EntityNotFoundException("Novel not found"));

        mockMvc.perform(get("/novels/{novel-id}", 1L))
                .andExpect(status().isNotFound());
    }

    private UserDto createTestUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("user@example.com");
        userDto.setAdmin(false);
        return userDto;
    }

    private List<ChapterGroupDto> createTestChapterGroups() {
        ChapterListItemDto chapter1 = new ChapterListItemDto();
        chapter1.setId(1L);
        chapter1.setCreationDate(LocalDateTime.of(2024, 2, 20, 10, 0));

        ChapterGroupDto group1 = new ChapterGroupDto();
        group1.setId(1L);
        group1.setName("Volume 1");
        group1.setGroupOrder(1.0);
        group1.setChapters(Collections.singletonList(chapter1));

        return Collections.singletonList(group1);
    }

    private Set<GenreDto> createTestGenres() {
        GenreDto genre1 = new GenreDto();
        genre1.setId(1L);
        genre1.setName("Fantasy");

        GenreDto genre2 = new GenreDto();
        genre2.setId(2L);
        genre2.setName("Adventure");

        return new HashSet<>(Arrays.asList(genre1, genre2));
    }

    private NovelDetailDto createNovelDetailDto() {
        NovelDetailDto dto = new NovelDetailDto();
        dto.setId(1L);
        dto.setAuthor("Author 1");
        dto.setPoster(createTestUser());
        dto.setChapterGroups(createTestChapterGroups());
        dto.setStatus(NovelStatus.DANG_TIEN_HANH);
        dto.setLastUpdateDate(LocalDateTime.of(2024, 2, 20, 12, 0));
        dto.setGenres(createTestGenres());
        return dto;
    }
}
