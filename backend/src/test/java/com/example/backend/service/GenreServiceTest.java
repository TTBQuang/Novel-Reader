package com.example.backend.service;

import com.example.backend.dto.genre.GenreDto;
import com.example.backend.entity.Genre;
import com.example.backend.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private GenreService genreService;

    private Genre genre1;
    private Genre genre2;
    private GenreDto genreDto1;
    private GenreDto genreDto2;

    @BeforeEach
    void setUp() {
        genre1 = new Genre();
        genre1.setId(1L);

        genre2 = new Genre();
        genre2.setId(2L);

        genreDto1 = new GenreDto();
        genreDto1.setId(1L);

        genreDto2 = new GenreDto();
        genreDto2.setId(2L);
    }

    @Test
    void getAllGenres_WhenGenresExist_ShouldReturnGenreDtoList() {
        List<Genre> genres = Arrays.asList(genre1, genre2);
        when(genreRepository.findAll()).thenReturn(genres);
        when(modelMapper.map(genre1, GenreDto.class)).thenReturn(genreDto1);
        when(modelMapper.map(genre2, GenreDto.class)).thenReturn(genreDto2);

        List<GenreDto> result = genreService.getAllGenres();

        assertEquals(2, result.size());
        assertEquals(genreDto1.getId(), result.get(0).getId());
        assertEquals(genreDto2.getId(), result.get(1).getId());
        verify(genreRepository, times(1)).findAll();
        verify(modelMapper, times(2)).map(any(Genre.class), eq(GenreDto.class));
    }

    @Test
    void getAllGenres_WhenNoGenresExist_ShouldReturnEmptyList() {
        when(genreRepository.findAll()).thenReturn(Collections.emptyList());

        List<GenreDto> result = genreService.getAllGenres();

        assertTrue(result.isEmpty());
        verify(genreRepository, times(1)).findAll();
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void getAllGenres_WhenMappingFails_ShouldThrowException() {
        List<Genre> genres = Collections.singletonList(genre1);
        when(genreRepository.findAll()).thenReturn(genres);
        when(modelMapper.map(any(), any())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> genreService.getAllGenres());

        verify(genreRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(any(), any());
    }
}