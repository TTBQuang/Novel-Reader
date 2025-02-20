package com.example.backend.service;

import com.example.backend.dto.novel.NovelDetailDto;
import com.example.backend.entity.Novel;
import com.example.backend.repository.NovelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

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

    private Novel novel;
    private NovelDetailDto novelDetailDto;

    @BeforeEach
    void setUp() {
        novel = new Novel();
        novel.setId(1L);

        novelDetailDto = new NovelDetailDto();
        novelDetailDto.setId(1L);
    }

    @Test
    void getNovelDetail_WhenNovelExists_ShouldReturnNovelDetailDto() {
        when(novelRepository.findById(eq(1L))).thenReturn(Optional.of(novel));
        when(modelMapper.map(novel, NovelDetailDto.class)).thenReturn(novelDetailDto);

        NovelDetailDto result = novelService.getNovelDetail(1L);

        assertEquals(novelDetailDto.getId(), result.getId());
        verify(novelRepository, times(1)).findById(1L);
        verify(modelMapper, times(1)).map(any(Novel.class), eq(NovelDetailDto.class));
    }

    @Test
    void getNovelDetail_WhenNovelNotExists_ShouldThrowException() {
        when(novelRepository.findById(eq(1L))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> novelService.getNovelDetail(1L));

        verify(novelRepository, times(1)).findById(1L);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void getNovelDetail_WhenMappingFails_ShouldThrowException() {
        when(novelRepository.findById(eq(1L))).thenReturn(Optional.of(novel));
        when(modelMapper.map(any(Novel.class), eq(NovelDetailDto.class))).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> novelService.getNovelDetail(1L));

        verify(novelRepository, times(1)).findById(1L);
        verify(modelMapper, times(1)).map(any(Novel.class), eq(NovelDetailDto.class));
    }
}
