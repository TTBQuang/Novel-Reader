package com.example.backend.controller;

import com.example.backend.dto.novel.NovelDetailDto;
import com.example.backend.dto.novel.NovelGetterCriteria;
import com.example.backend.dto.novel.NovelListItemDto;
import com.example.backend.service.NovelService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/novels")
@AllArgsConstructor
public class NovelController {
    private final NovelService novelService;

    @GetMapping()
    public ResponseEntity<Page<NovelListItemDto>> getNovels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute NovelGetterCriteria criteria) {
        Page<NovelListItemDto> novels = novelService.getNovels(page, size, criteria);
        return ResponseEntity.ok(novels);
    }

    @GetMapping("/{novel-id}")
    public ResponseEntity<NovelDetailDto> getNovelDetail(@PathVariable("novel-id") long id) {
        NovelDetailDto novel = novelService.getNovelDetail(id);
        if (novel == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(novel);
    }
}
