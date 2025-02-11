package com.example.backend.service;

import com.example.backend.dto.genre.GenreDto;
import com.example.backend.entity.Genre;
import com.example.backend.repository.GenreRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;
    private final ModelMapper modelMapper;

    public List<GenreDto> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();

        return genres.stream()
                .map(genre -> modelMapper.map(genre, GenreDto.class))
                .collect(Collectors.toList());
    }
}
