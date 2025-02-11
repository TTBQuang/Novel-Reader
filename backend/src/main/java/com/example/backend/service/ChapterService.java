package com.example.backend.service;

import com.example.backend.dto.chapter.ChapterDetailDto;
import com.example.backend.entity.Chapter;
import com.example.backend.repository.ChapterRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ChapterService {
    private final ChapterRepository chapterRepository;
    private final ModelMapper modelMapper;

    public ChapterDetailDto getChapterDetail(long id) {
        Optional<Chapter> chapter = chapterRepository.findById(id);
        if (chapter.isPresent()) {
            ChapterDetailDto dto = modelMapper.map(chapter.get(), ChapterDetailDto.class);
            dto.setCommentCount(chapter.get().getComments().size());
            dto.setChapterGroupName(chapter.get().getChapterGroup().getName());
            return dto;
        }
        return null;
    }
}
