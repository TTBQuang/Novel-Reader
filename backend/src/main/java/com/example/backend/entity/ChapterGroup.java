package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Table(name = "chapter_groups")
@NoArgsConstructor
@AllArgsConstructor
public class ChapterGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id", nullable = false)
    private Novel novel;

    @Column(nullable = false)
    private String name;

    @Column(name = "group_order", nullable = false)
    private double groupOrder;

    @OneToMany(mappedBy = "chapterGroup", fetch = FetchType.LAZY)
    @OrderBy("chapterOrder ASC")
    @Setter
    private List<Chapter> chapters;
}
