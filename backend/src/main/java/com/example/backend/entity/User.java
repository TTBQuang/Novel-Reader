package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin;

    @Column(name = "is_comment_blocked", nullable = false)
    private Boolean isCommentBlocked;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private String avatar;

    @Column(name = "cover_image")
    private String coverImage;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "poster", fetch = FetchType.LAZY)
    @OrderBy("lastUpdateDate DESC")
    private List<Novel> ownNovels;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        if (this.displayName == null) {
            this.displayName = this.username;
        }
    }
}

