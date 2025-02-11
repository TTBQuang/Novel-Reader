package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Table(name = "chapters")
@Setter
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "chapter_group_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ChapterGroup chapterGroup;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String content;

    @Column(name = "words_count", nullable = false)
    private long wordsCount;

    @Column(name = "chapter_order", nullable = false)
    private double chapterOrder;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "chapter", fetch = FetchType.LAZY)
    private Set<Comment> comments;
}
