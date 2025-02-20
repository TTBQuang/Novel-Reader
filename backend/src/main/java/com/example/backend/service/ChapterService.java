package com.example.backend.service;

import com.example.backend.dto.chapter.ChapterDetailDto;
import com.example.backend.repository.ChapterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChapterService {
    private final ChapterRepository chapterRepository;
    private final ModelMapper modelMapper;

    public ChapterDetailDto getChapterDetail(long id) {
        return chapterRepository.findById(id)
                .map(chapter -> modelMapper.map(chapter, ChapterDetailDto.class))
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id: " + id));
    }
}
