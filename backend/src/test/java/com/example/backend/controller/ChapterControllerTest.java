package com.example.backend.controller;

import com.example.backend.dto.chapter.ChapterDetailDto;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.service.ChapterService;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ChapterControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ChapterService chapterService;

    @InjectMocks
    private ChapterController chapterController;

    private ChapterDetailDto chapterDetailDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(chapterController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        chapterDetailDto = new ChapterDetailDto();
        chapterDetailDto.setId(1L);
        chapterDetailDto.setChapterGroupName("Group1");
        chapterDetailDto.setContent("Content 1");
    }

    @Test
    void getChapterDetail_ShouldReturnChapterDetailDto() throws Exception {
        when(chapterService.getChapterDetail(eq(1L))).thenReturn(chapterDetailDto);

        mockMvc.perform(get("/chapters/{chapter-id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.chapterGroupName").value("Group1"))
                .andExpect(jsonPath("$.content").value("Content 1"));

        verify(chapterService, times(1)).getChapterDetail(eq(1L));
    }

    @Test
    void getChapterDetail_WhenChapterNotFound_ShouldReturnNotFound() throws Exception {
        when(chapterService.getChapterDetail(eq(1L)))
                .thenThrow(new EntityNotFoundException("Chapter not found"));

        mockMvc.perform(get("/chapters/{chapter-id}", 1L))
                .andExpect(status().isNotFound());
    }
}
