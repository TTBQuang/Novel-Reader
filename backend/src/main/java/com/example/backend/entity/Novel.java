package com.example.backend.entity;

import com.example.backend.converter.NovelStatusConverter;
import com.example.backend.enums.NovelStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "novels")
@Getter
@Setter
public class Novel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poster", nullable = false)
    private User poster;

    @OneToMany(mappedBy = "novel", fetch = FetchType.LAZY)
    @OrderBy("groupOrder ASC")
    @Setter
    private List<ChapterGroup> chapterGroups;

    @Column(nullable = false)
    private String name;

    private String author;
    private String artist;
    private String cover;

    @Column(nullable = false)
    private String summary;

    @Convert(converter = NovelStatusConverter.class)
    @Column(name = "status", nullable = false)
    private NovelStatus status;

    @Column(name = "words_count", nullable = false)
    private long wordsCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime lastUpdateDate;

    @OneToMany(mappedBy = "novel", fetch = FetchType.LAZY)
    private Set<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "novel_genres",
            joinColumns = @JoinColumn(name = "novel_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;
}
