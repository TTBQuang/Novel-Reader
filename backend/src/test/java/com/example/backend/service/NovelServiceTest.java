package com.example.backend.service;

import com.example.backend.dto.novel.NovelDetailDto;
import com.example.backend.dto.novel.NovelGetterCriteria;
import com.example.backend.dto.novel.NovelListItemDto;
import com.example.backend.entity.Novel;
import com.example.backend.enums.NovelSortOption;
import com.example.backend.enums.NovelStatus;
import com.example.backend.repository.NovelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NovelServiceTest {
    @Mock
    private NovelRepository novelRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NovelService novelService;

    // Constants for test data
    private static final long FIRST_NOVEL_ID = 1L;
    private static final long SECOND_NOVEL_ID = 2L;
    private static final String FIRST_NOVEL_NAME = "A Novel";
    private static final String SECOND_NOVEL_NAME = "B Novel";
    private static final int DEFAULT_PAGE_SIZE = 10;

    // Test objects
    private Novel singleNovel;
    private NovelDetailDto singleNovelDto;
    private Page<Novel> pagedNovels;
    private NovelListItemDto firstNovelDto;
    private NovelListItemDto secondNovelDto;

    @BeforeEach
    void setUp() {
        setupSingleNovelTest();
        setupPagedNovelsTest();
    }

    private void setupSingleNovelTest() {
        singleNovel = new Novel();
        singleNovel.setId(FIRST_NOVEL_ID);

        singleNovelDto = new NovelDetailDto();
        singleNovelDto.setId(FIRST_NOVEL_ID);
    }

    private void setupPagedNovelsTest() {
        Novel firstNovel = createNovel(
                FIRST_NOVEL_ID,
                FIRST_NOVEL_NAME,
                1000L,
                LocalDateTime.now(),
                NovelStatus.DANG_TIEN_HANH
        );

        Novel secondNovel = createNovel(
                SECOND_NOVEL_ID,
                SECOND_NOVEL_NAME,
                2000L,
                LocalDateTime.now().minusDays(1),
                NovelStatus.DA_HOAN_THANH
        );

        firstNovelDto = createNovelDto(FIRST_NOVEL_ID, FIRST_NOVEL_NAME);
        secondNovelDto = createNovelDto(SECOND_NOVEL_ID, SECOND_NOVEL_NAME);

        pagedNovels = new PageImpl<>(Arrays.asList(firstNovel, secondNovel));
    }

    private Novel createNovel(Long id, String name, Long wordsCount, LocalDateTime updateDate, NovelStatus status) {
        Novel novel = new Novel();
        novel.setId(id);
        novel.setName(name);
        novel.setWordsCount(wordsCount);
        novel.setCreationDate(updateDate);
        novel.setLastUpdateDate(updateDate);
        novel.setStatus(status);
        return novel;
    }

    private NovelListItemDto createNovelDto(Long id, String name) {
        NovelListItemDto dto = new NovelListItemDto();
        dto.setId(id);
        dto.setName(name);
        return dto;
    }

    @Nested
    @DisplayName("getNovelDetail tests")
    class NovelDetailTest {
        @Test
        void whenNovelExists_ShouldReturnNovelDetailDto() {
            when(novelRepository.findById(eq(FIRST_NOVEL_ID))).thenReturn(Optional.of(singleNovel));
            when(modelMapper.map(singleNovel, NovelDetailDto.class)).thenReturn(singleNovelDto);

            NovelDetailDto result = novelService.getNovelDetail(FIRST_NOVEL_ID);

            assertEquals(singleNovelDto.getId(), result.getId());
            verify(novelRepository).findById(FIRST_NOVEL_ID);
            verify(modelMapper).map(any(Novel.class), eq(NovelDetailDto.class));
        }

        @Test
        void whenNovelNotExists_ShouldThrowException() {
            when(novelRepository.findById(eq(FIRST_NOVEL_ID))).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> novelService.getNovelDetail(FIRST_NOVEL_ID));

            verify(novelRepository).findById(FIRST_NOVEL_ID);
            verify(modelMapper, never()).map(any(), any());
        }
    }

    @Nested
    @DisplayName("getNovels tests")
    class GetNovelsTest {
        @Test
        void withDefaultSort_ShouldReturnSortedByLastUpdateDate() {
            NovelGetterCriteria criteria = new NovelGetterCriteria(null, null, null, null);
            Pageable expectedPageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by("lastUpdateDate").descending());
            when(novelRepository.findNovelsByCriteria(null, null, null, expectedPageable))
                    .thenReturn(pagedNovels);
            when(modelMapper.map(any(Novel.class), eq(NovelListItemDto.class)))
                    .thenReturn(firstNovelDto, secondNovelDto);

            Page<NovelListItemDto> result = novelService.getNovels(0, DEFAULT_PAGE_SIZE, criteria);

            assertEquals(pagedNovels.getSize(), result.getContent().size());
            verify(novelRepository).findNovelsByCriteria(null, null, null, expectedPageable);
            verify(modelMapper, times(2)).map(any(Novel.class), eq(NovelListItemDto.class));
        }

        @Test
        void whenNoNovelsExist_ShouldReturnEmptyList() {
            NovelGetterCriteria criteria = new NovelGetterCriteria(null, null, null, null);
            Pageable expectedPageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by("lastUpdateDate").descending());
            when(novelRepository.findNovelsByCriteria(null, null, null, expectedPageable))
                    .thenReturn(Page.empty());

            Page<NovelListItemDto> result = novelService.getNovels(0, DEFAULT_PAGE_SIZE, criteria);

            assertEquals(0, result.getContent().size());
            verify(novelRepository).findNovelsByCriteria(null, null, null, expectedPageable);
            verify(modelMapper, never()).map(any(Novel.class), eq(NovelListItemDto.class));
        }

        @Test
        void whenMappingFails_ShouldThrowException() {
            NovelGetterCriteria criteria = new NovelGetterCriteria(null, null, null, null);
            Pageable expectedPageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by("lastUpdateDate").descending());
            when(novelRepository.findNovelsByCriteria(null, null, null, expectedPageable))
                    .thenReturn(pagedNovels);
            when(modelMapper.map(any(Novel.class), eq(NovelListItemDto.class)))
                    .thenThrow(new RuntimeException());

            assertThrows(RuntimeException.class, () -> novelService.getNovels(0, DEFAULT_PAGE_SIZE, criteria));

            verify(novelRepository).findNovelsByCriteria(null, null, null, expectedPageable);
            verify(modelMapper, times(1)).map(any(Novel.class), eq(NovelListItemDto.class));
        }

        @Test
        void withAZSort_ShouldReturnSortedByNameAscending() {
            NovelGetterCriteria criteria = new NovelGetterCriteria(null, NovelSortOption.AZ, null, null);
            Pageable expectedPageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by("name").ascending());
            when(novelRepository.findNovelsByCriteria(null, null, null, expectedPageable))
                    .thenReturn(pagedNovels);
            when(modelMapper.map(any(Novel.class), eq(NovelListItemDto.class)))
                    .thenReturn(firstNovelDto, secondNovelDto);

            Page<NovelListItemDto> result = novelService.getNovels(0, DEFAULT_PAGE_SIZE, criteria);

            assertEquals(pagedNovels.getSize(), result.getContent().size());
            verify(novelRepository).findNovelsByCriteria(null, null, null, expectedPageable);
            verify(modelMapper, times(2)).map(any(Novel.class), eq(NovelListItemDto.class));
        }

        @Test
        void withKeywordAndStatus_ShouldFilterResults() {
            String keyword = "Novel";
            String status = "DANG_TIEN_HANH";
            NovelGetterCriteria criteria = new NovelGetterCriteria(keyword, null, status, null);
            Set<NovelStatus> statusSet = new HashSet<>(List.of(NovelStatus.DANG_TIEN_HANH));
            Pageable expectedPageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by("lastUpdateDate").descending());

            when(novelRepository.findNovelsByCriteria(eq(keyword), eq(statusSet), eq(null), eq(expectedPageable)))
                    .thenReturn(pagedNovels);
            when(modelMapper.map(any(Novel.class), eq(NovelListItemDto.class)))
                    .thenReturn(firstNovelDto, secondNovelDto);

            Page<NovelListItemDto> result = novelService.getNovels(0, DEFAULT_PAGE_SIZE, criteria);

            assertEquals(pagedNovels.getSize(), result.getContent().size());
            verify(novelRepository).findNovelsByCriteria(eq(keyword), eq(statusSet), eq(null), eq(expectedPageable));
            verify(modelMapper, times(2)).map(any(Novel.class), eq(NovelListItemDto.class));
        }

        @Test
        void withGenreIds_ShouldFilterByGenres() {
            String genreIds = "1,2,3";
            NovelGetterCriteria criteria = new NovelGetterCriteria(null, null, null, genreIds);
            Set<Long> genreIdSet = new HashSet<>(Arrays.asList(1L, 2L, 3L));
            Pageable expectedPageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by("lastUpdateDate").descending());

            when(novelRepository.findNovelsByCriteria(null, null, genreIdSet, expectedPageable))
                    .thenReturn(pagedNovels);
            when(modelMapper.map(any(Novel.class), eq(NovelListItemDto.class)))
                    .thenReturn(firstNovelDto, secondNovelDto);

            Page<NovelListItemDto> result = novelService.getNovels(0, DEFAULT_PAGE_SIZE, criteria);

            assertEquals(pagedNovels.getSize(), result.getContent().size());
            verify(novelRepository).findNovelsByCriteria(null, null, genreIdSet, expectedPageable);
            verify(modelMapper, times(2)).map(any(Novel.class), eq(NovelListItemDto.class));
        }

        @Test
        void withWordsCountSort_ShouldReturnSortedByWordsCount() {
            NovelGetterCriteria criteria = new NovelGetterCriteria(null, NovelSortOption.WORDS_COUNT, null, null);
            Pageable expectedPageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by("wordsCount").descending());
            when(novelRepository.findNovelsByCriteria(null, null, null, expectedPageable))
                    .thenReturn(pagedNovels);
            when(modelMapper.map(any(Novel.class), eq(NovelListItemDto.class)))
                    .thenReturn(firstNovelDto, secondNovelDto);

            Page<NovelListItemDto> result = novelService.getNovels(0, DEFAULT_PAGE_SIZE, criteria);

            assertEquals(pagedNovels.getSize(), result.getContent().size());
            verify(novelRepository).findNovelsByCriteria(null, null, null, expectedPageable);
            verify(modelMapper, times(2)).map(any(Novel.class), eq(NovelListItemDto.class));
        }

        @Test
        void withAllCriteria_ShouldApplyAllFilters() {
            String keyword = "Novel";
            String status = "DANG_TIEN_HANH";
            String genreIds = "1,2";
            NovelGetterCriteria criteria = new NovelGetterCriteria(
                    keyword,
                    NovelSortOption.WORDS_COUNT,
                    status,
                    genreIds
            );
            Set<NovelStatus> statusSet = new HashSet<>(List.of(NovelStatus.DANG_TIEN_HANH));
            Set<Long> genreIdSet = new HashSet<>(Arrays.asList(1L, 2L));
            Pageable expectedPageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by("wordsCount").descending());
            when(novelRepository.findNovelsByCriteria(
                    eq(keyword),
                    eq(statusSet),
                    eq(genreIdSet),
                    eq(expectedPageable)
            )).thenReturn(pagedNovels);
            when(modelMapper.map(any(Novel.class), eq(NovelListItemDto.class)))
                    .thenReturn(firstNovelDto, secondNovelDto);

            Page<NovelListItemDto> result = novelService.getNovels(0, DEFAULT_PAGE_SIZE, criteria);

            assertEquals(pagedNovels.getSize(), result.getContent().size());
            verify(novelRepository).findNovelsByCriteria(
                    eq(keyword),
                    eq(statusSet),
                    eq(genreIdSet),
                    eq(expectedPageable)
            );
            verify(modelMapper, times(2)).map(any(Novel.class), eq(NovelListItemDto.class));
        }
    }
}