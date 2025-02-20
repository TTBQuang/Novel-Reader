package com.example.backend.controller;

import com.example.backend.dto.chapter.ChapterDetailDto;
import com.example.backend.service.ChapterService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chapters")
@AllArgsConstructor
public class ChapterController {
    private final ChapterService chapterService;

    @GetMapping("/{chapter-id}")
    public ResponseEntity<ChapterDetailDto> getChapterDetail(@PathVariable("chapter-id") long id) {
        return ResponseEntity.ok(chapterService.getChapterDetail(id));
    }
}
