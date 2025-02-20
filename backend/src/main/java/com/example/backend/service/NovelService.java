package com.example.backend.service;

import com.example.backend.dto.novel.NovelDetailDto;
import com.example.backend.dto.novel.NovelGetterCriteria;
import com.example.backend.dto.novel.NovelListItemDto;
import com.example.backend.entity.Novel;
import com.example.backend.enums.NovelStatus;
import com.example.backend.repository.NovelRepository;
import com.example.backend.util.ConverterUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class NovelService {
    private final NovelRepository novelRepository;
    private final ModelMapper modelMapper;

    public Page<NovelListItemDto> getNovels(int page, int size, NovelGetterCriteria criteria) {
        Sort sort;
        if (criteria.getSortOption() == null) {
            sort = Sort.by("lastUpdateDate").descending();
        } else {
            sort = switch (criteria.getSortOption()) {
                case AZ -> Sort.by("name").ascending();
                case ZA -> Sort.by("name").descending();
                case CREATION_DATE -> Sort.by("creationDate").descending();
                case WORDS_COUNT -> Sort.by("wordsCount").descending();
                default -> Sort.by("lastUpdateDate").descending();
            };
        }
        Pageable pageable = PageRequest.of(page, size, sort);

        String keyword = criteria.getKeyword() != null ? criteria.getKeyword() : null;
        Set<NovelStatus> status = criteria.getStatus() != null ? ConverterUtil.convertStringToEnumSet(criteria.getStatus()) : null;
        Set<Long> genreIds = criteria.getGenreIds() != null ? ConverterUtil.convertStringToSet(criteria.getGenreIds()) : null;

        Page<Novel> novelPage = novelRepository.findNovelsByCriteria(keyword, status, genreIds, pageable);

        return novelPage.map(novel -> modelMapper.map(novel, NovelListItemDto.class));
    }

    public NovelDetailDto getNovelDetail(long id) {
        return novelRepository.findById(id)
                .map(novel -> modelMapper.map(novel, NovelDetailDto.class))
                .orElseThrow(() -> new EntityNotFoundException("Novel not found with id: " + id));
    }
}