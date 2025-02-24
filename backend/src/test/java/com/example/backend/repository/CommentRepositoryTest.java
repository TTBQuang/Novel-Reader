package com.example.backend.repository;

import com.example.backend.entity.*;
import com.example.backend.enums.NovelStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Novel novel;
    private Chapter chapter;
    private Comment comment1;
    private Comment comment2;

    private User createTestUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("hashedPassword");
        user.setIsAdmin(false);
        user.setIsCommentBlocked(false);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    private Novel createTestNovel(User poster) {
        Novel novel = new Novel();
        novel.setName("Test Novel");
        novel.setSummary("Test summary");
        novel.setStatus(NovelStatus.DANG_TIEN_HANH);
        novel.setWordsCount(0);
        novel.setCreationDate(LocalDateTime.now());
        novel.setLastUpdateDate(LocalDateTime.now());
        novel.setPoster(poster);
        return novel;
    }

    private ChapterGroup createTestChapterGroup(Novel novel){
        return new ChapterGroup(null, novel, "name", 1, null);
    }

    private Chapter createTestChapter(ChapterGroup chapterGroup) {
        return new Chapter(null, chapterGroup, "name", "content", 1, 1, LocalDateTime.now(), null);
    }

    private Comment createTestCommentInNovel(User user) {
        Comment comment = new Comment();
        comment.setContent("Comment 1");
        comment.setUser(user);
        comment.setNovel(novel);
        comment.setCreatedAt(LocalDateTime.now());
        return comment;
    }

    private Comment createTestCommentInChapter(User user) {
        Comment comment = new Comment();
        comment.setContent("Comment 2");
        comment.setUser(user);
        comment.setChapter(chapter);
        comment.setCreatedAt(LocalDateTime.now());
        return comment;
    }

    @BeforeEach
    void setUp() {
        User novelPoster = createTestUser("username","email@gmail.com");
        User comment1Poster = createTestUser("username1", "email1@gmail.com");
        User comment2Poster = createTestUser("username2", "email2@gmail.com");
        novelPoster = entityManager.persist(novelPoster);
        comment1Poster = entityManager.persist(comment1Poster);
        comment2Poster = entityManager.persist(comment2Poster);

        novel = createTestNovel(novelPoster);
        novel = entityManager.persist(novel);

        ChapterGroup chapterGroup = createTestChapterGroup(novel);
        chapterGroup = entityManager.persist(chapterGroup);

        chapter = createTestChapter(chapterGroup);
        chapter = entityManager.persist(chapter);

        comment1 = createTestCommentInNovel(comment1Poster);
        comment1 = entityManager.persist(comment1);

        comment2 = createTestCommentInChapter(comment2Poster);
        comment2 = entityManager.persist(comment2);

        entityManager.flush();
    }

    @Nested
    @DisplayName("Get comment detail test")
    class GetCommentDetailTest {
        @Test
        void whenCommentInNovel_ShouldReturnCommentWithUserData() {
            Comment foundComment = commentRepository.getCommentDetail(comment1.getId());

            assertAll(
                    () -> assertNotNull(foundComment),
                    () -> assertEquals(comment1.getId(), foundComment.getId()),
                    () -> assertNotNull(foundComment.getUser()),
                    () -> assertEquals(comment1.getUser().getId(), foundComment.getUser().getId())
            );
        }

        @Test
        void whenCommentInChapter_ShouldReturnCommentWithUserData() {
            Comment foundComment = commentRepository.getCommentDetail(comment2.getId());

            assertAll(
                    () -> assertNotNull(foundComment),
                    () -> assertEquals(comment2.getId(), foundComment.getId()),
                    () -> assertNotNull(foundComment.getUser()),
                    () -> assertEquals(comment2.getUser().getId(), foundComment.getUser().getId())
            );
        }

        @Test
        void getCommentDetail_WhenCommentDoesNotExist_ShouldReturnNull() {
            Comment foundComment = commentRepository.getCommentDetail(999L);

            assertNull(foundComment);
        }
    }
}
