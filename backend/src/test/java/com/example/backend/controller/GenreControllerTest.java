package com.example.backend.controller;

import com.example.backend.dto.genre.GenreDto;
import com.example.backend.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GenreControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GenreService genreService;

    @InjectMocks
    private GenreController genreController;

    private List<GenreDto> genreDtoList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(genreController).build();

        GenreDto genreDto1 = new GenreDto();
        genreDto1.setId(1L);
        genreDto1.setName("Action");

        GenreDto genreDto2 = new GenreDto();
        genreDto2.setId(2L);
        genreDto2.setName("Comedy");

        genreDtoList = Arrays.asList(genreDto1, genreDto2);
    }

    @Nested
    @DisplayName("GetAllGenres Tests")
    class GetAllGenresTests {

        @Test
        void whenGenresFound_ShouldReturnListOfGenres() throws Exception {
            when(genreService.getAllGenres()).thenReturn(genreDtoList);

            mockMvc.perform(get("/genres")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("Action"))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].name").value("Comedy"));

            verify(genreService, times(1)).getAllGenres();
        }

        @Test
        void whenNoGenresExist_ShouldReturnEmptyList() throws Exception {
            when(genreService.getAllGenres()).thenReturn(List.of());

            mockMvc.perform(get("/genres")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(0));

            verify(genreService, times(1)).getAllGenres();
        }
    }
}
