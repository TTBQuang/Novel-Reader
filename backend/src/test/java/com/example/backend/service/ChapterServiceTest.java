package com.example.backend.service;

import com.example.backend.dto.chapter.ChapterDetailDto;
import com.example.backend.entity.Chapter;
import com.example.backend.repository.ChapterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChapterServiceTest {

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ChapterService chapterService;

    private Chapter chapter;
    private ChapterDetailDto chapterDetailDto;

    @BeforeEach
    void setUp() {
        chapter = new Chapter();
        chapter.setId(1L);

        chapterDetailDto = new ChapterDetailDto();
        chapterDetailDto.setId(1L);
    }

    @Nested
    @DisplayName("getChapterDetail Tests")
    class GetChapterDetailTests {

        @Test
        void whenChapterExists_ShouldReturnChapterDetailDto() {
            when(chapterRepository.findById(eq(1L))).thenReturn(Optional.of(chapter));
            when(modelMapper.map(chapter, ChapterDetailDto.class)).thenReturn(chapterDetailDto);

            ChapterDetailDto result = chapterService.getChapterDetail(1L);

            assertEquals(chapterDetailDto.getId(), result.getId());
            verify(chapterRepository, times(1)).findById(1L);
            verify(modelMapper, times(2)).map(any(Chapter.class), eq(ChapterDetailDto.class));
        }

        @Test
        void whenChapterNotExists_ShouldThrowException() {
            when(chapterRepository.findById(eq(1L))).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> chapterService.getChapterDetail(1L));

            verify(chapterRepository, times(1)).findById(1L);
            verify(modelMapper, never()).map(any(), any());
        }

        @Test
        void whenMappingFails_ShouldThrowException() {
            when(chapterRepository.findById(eq(1L))).thenReturn(Optional.of(chapter));
            when(modelMapper.map(any(Chapter.class), eq(ChapterDetailDto.class)))
                    .thenThrow(RuntimeException.class);

            assertThrows(RuntimeException.class, () -> chapterService.getChapterDetail(1L));

            verify(chapterRepository, times(1)).findById(1L);
            verify(modelMapper, times(1)).map(any(Chapter.class), eq(ChapterDetailDto.class));
        }
    }
}

