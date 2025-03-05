package com.example.backend.controller;

import com.example.backend.dto.chapter.ChapterBasicInfoDto;
import com.example.backend.dto.chaptergroup.ChapterGroupDto;
import com.example.backend.dto.genre.GenreDto;
import com.example.backend.dto.novel.NovelDetailDto;
import com.example.backend.dto.novel.NovelGetterCriteria;
import com.example.backend.dto.novel.NovelListItemDto;
import com.example.backend.dto.user.UserDetailDto;
import com.example.backend.enums.NovelSortOption;
import com.example.backend.enums.NovelStatus;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.service.NovelService;
import jakarta.persistence.EntityNotFoundException;
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

    @Nested
    @DisplayName("GetNovelDetail Tests")
    class GetNovelDetailTests{
        @Test
        void whenNovelFound_ShouldReturnNovelDetailDto() throws Exception {
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
        void whenNovelNotFound_ShouldReturnNotFound() throws Exception {
            when(novelService.getNovelDetail(eq(1L)))
                    .thenThrow(new EntityNotFoundException("Novel not found"));

            mockMvc.perform(get("/novels/{novel-id}", 1L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GetNovels Tests")
    class GetNovelsTest {
        @Test
        void whenNovelsNotFound_ShouldReturnEmptyPage() throws Exception {
            Pageable pageable = PageRequest.of(2, 20);
            Page<NovelListItemDto> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(novelService.getNovels(eq(0), eq(10), any(NovelGetterCriteria.class)))
                    .thenReturn(emptyPage);

            mockMvc.perform(get("/novels")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements").value(0))
                    .andExpect(jsonPath("$.totalPages").value(0));

            verify(novelService).getNovels(eq(0), eq(10), any(NovelGetterCriteria.class));
        }

        @Test
        void whenNovelsFound_ShouldReturnPagedData() throws Exception {
            List<NovelListItemDto> novels = Arrays.asList(
                    createNovelListItemDto(1L, "Novel 1", "cover1.jpg"),
                    createNovelListItemDto(2L, "Novel 2", "cover2.jpg")
            );
            Page<NovelListItemDto> pagedNovels = new PageImpl<>(novels, PageRequest.of(0, 10), novels.size());
            when(novelService.getNovels(eq(0), eq(10), any(NovelGetterCriteria.class)))
                    .thenReturn(pagedNovels);

            mockMvc.perform(get("/novels")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].id").value(1))
                    .andExpect(jsonPath("$.content[0].name").value("Novel 1"))
                    .andExpect(jsonPath("$.content[0].cover").value("cover1.jpg"))
                    .andExpect(jsonPath("$.content[1].id").value(2))
                    .andExpect(jsonPath("$.content[1].name").value("Novel 2"))
                    .andExpect(jsonPath("$.content[1].cover").value("cover2.jpg"))
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.totalPages").value(1));

            verify(novelService).getNovels(eq(0), eq(10), any(NovelGetterCriteria.class));
        }

        @Test
        void whenServiceThrowException_ShouldReturnInternalServerError() throws Exception {
            when(novelService.getNovels(anyInt(), anyInt(), any(NovelGetterCriteria.class)))
                    .thenThrow(new RuntimeException("Internal server error"));

            mockMvc.perform(get("/novels")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

            verify(novelService).getNovels(eq(0), eq(10), any(NovelGetterCriteria.class));
        }

        @Test
        @DisplayName("Should return paged data when novels found with criteria")
        void whenCriteriaPassed_ShouldPassCriteriaToService() throws Exception {
            Pageable pageable = PageRequest.of(2, 20);
            Page<NovelListItemDto> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(novelService.getNovels(anyInt(), anyInt(), any(NovelGetterCriteria.class)))
                    .thenReturn(emptyPage);

            mockMvc.perform(get("/novels")
                            .param("keyword", "test")
                            .param("sortOption", "WORDS_COUNT")
                            .param("status", "DANG_TIEN_HANH")
                            .param("genreIds", "1,2,3")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(novelService).getNovels(eq(0), eq(10), argThat(criteria ->
                    "test".equals(criteria.getKeyword()) &&
                            NovelSortOption.WORDS_COUNT.equals(criteria.getSortOption()) &&
                            "DANG_TIEN_HANH".equals(criteria.getStatus()) &&
                            "1,2,3".equals(criteria.getGenreIds())
            ));
        }

        @Test
        @DisplayName("Should return bad request when size is invalid")
        void whenSizeInvalid_ShouldReturnBadRequest() throws Exception {
            mockMvc.perform(get("/novels")
                            .param("size", "-1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(novelService, never()).getNovels(anyInt(), anyInt(), any(NovelGetterCriteria.class));
        }

        @Test
        @DisplayName("Should return bad request when page is invalid")
        void whenPageInvalid_ShouldReturnBadRequest() throws Exception {
            mockMvc.perform(get("/novels")
                            .param("page", "-1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(novelService, never()).getNovels(anyInt(), anyInt(), any(NovelGetterCriteria.class));
        }
    }

    private NovelListItemDto createNovelListItemDto(Long id, String name, String cover) {
        NovelListItemDto dto = new NovelListItemDto();
        dto.setId(id);
        dto.setName(name);
        dto.setCover(cover);
        return dto;
    }

    private UserDetailDto createTestUser() {
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setId(1L);
        userDetailDto.setEmail("user@example.com");
        userDetailDto.setAdmin(false);
        return userDetailDto;
    }

    private List<ChapterGroupDto> createTestChapterGroups() {
        ChapterBasicInfoDto chapter1 = new ChapterBasicInfoDto();
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
