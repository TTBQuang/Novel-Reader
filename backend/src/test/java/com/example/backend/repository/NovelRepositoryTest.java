package com.example.backend.repository;

import com.example.backend.entity.Genre;
import com.example.backend.entity.Novel;
import com.example.backend.entity.User;
import com.example.backend.enums.NovelStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NovelRepositoryTest {

    @Autowired
    private NovelRepository novelRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Novel novel1;
    private Novel novel2;
    private Novel novel3;
    private Genre genre1;
    private Genre genre2;

    private User createTestUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@gmail.com");
        user.setPassword("hashedPassword");
        user.setIsAdmin(false);
        user.setIsCommentBlocked(false);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    private Genre createTestGenre(String name) {
        return new Genre(null, name, null);
    }

    private Novel createTestNovel(String name, User poster, NovelStatus status) {
        Novel novel = new Novel();
        novel.setName(name);
        novel.setSummary("Summary for " + name);
        novel.setStatus(status);
        novel.setWordsCount(1000);
        novel.setCreationDate(LocalDateTime.now());
        novel.setLastUpdateDate(LocalDateTime.now());
        novel.setPoster(poster);
        novel.setGenres(new HashSet<>());
        return novel;
    }

    private void addGenreToNovel(Novel novel, Genre genre) {
        Set<Genre> genres = novel.getGenres();
        genres.add(genre);
        novel.setGenres(genres);
    }

    @BeforeEach
    void setUp() {
        user = createTestUser();
        user = entityManager.persist(user);

        genre1 = createTestGenre("Fantasy");
        genre2 = createTestGenre("Science Fiction");
        genre1 = entityManager.persist(genre1);
        genre2 = entityManager.persist(genre2);

        novel1 = createTestNovel("Fantasy Adventure", user, NovelStatus.DANG_TIEN_HANH);
        novel2 = createTestNovel("Sci-Fi Journey", user, NovelStatus.DA_HOAN_THANH);
        novel3 = createTestNovel("Fantasy in Space", user, NovelStatus.DANG_TIEN_HANH);

        addGenreToNovel(novel1, genre1);
        addGenreToNovel(novel2, genre2);
        addGenreToNovel(novel3, genre1);
        addGenreToNovel(novel3, genre2);

        novel1 = entityManager.persist(novel1);
        novel2 = entityManager.persist(novel2);
        novel3 = entityManager.persist(novel3);

        entityManager.flush();
    }

    @Nested
    @DisplayName("Find novels by criteria test")
    class FindNovelsByCriteriaTest {

        @Test
        void whenSearchWithoutFilters_ShouldReturnAllNovels() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<Novel> result = novelRepository.findNovelsByCriteria(null, null, null, pageable);
            List<Novel> novels = result.getContent();

            assertAll(
                    () -> assertEquals(3, novels.size()),
                    () -> assertTrue(novels.contains(novel1)),
                    () -> assertTrue(novels.contains(novel2)),
                    () -> assertTrue(novels.contains(novel3))
            );
        }

        @Test
        void whenSearchByKeyword_ShouldReturnMatchingNovels() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<Novel> result = novelRepository.findNovelsByCriteria("Fantasy", null, null, pageable);
            List<Novel> novels = result.getContent();

            assertAll(
                    () -> assertEquals(2, novels.size()),
                    () -> assertTrue(novels.contains(novel1)),
                    () -> assertFalse(novels.contains(novel2)),
                    () -> assertTrue(novels.contains(novel3))
            );
        }

        @Test
        void whenSearchByStatus_ShouldReturnMatchingNovels() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<Novel> result = novelRepository.findNovelsByCriteria(
                    null, Set.of(NovelStatus.DANG_TIEN_HANH), null, pageable);
            List<Novel> novels = result.getContent();

            assertAll(
                    () -> assertEquals(2, novels.size()),
                    () -> assertTrue(novels.contains(novel1)),
                    () -> assertFalse(novels.contains(novel2)),
                    () -> assertTrue(novels.contains(novel3))
            );
        }

        @Test
        void whenSearchByGenre_ShouldReturnMatchingNovels() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<Novel> result = novelRepository.findNovelsByCriteria(null, null, Set.of(genre2.getId()), pageable);
            List<Novel> novels = result.getContent();

            assertAll(
                    () -> assertEquals(2, novels.size()),
                    () -> assertFalse(novels.contains(novel1)),
                    () -> assertTrue(novels.contains(novel2)),
                    () -> assertTrue(novels.contains(novel3))
            );
        }

        @Test
        void whenSearchWithMultipleGenres_ShouldReturnNovelsMatchingAnyGenre() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<Novel> result = novelRepository.findNovelsByCriteria(
                    null, null, Set.of(genre1.getId(), genre2.getId()), pageable);
            List<Novel> novels = result.getContent();

            assertAll(
                    () -> assertEquals(3, novels.size()),
                    () -> assertTrue(novels.contains(novel1)),
                    () -> assertTrue(novels.contains(novel2)),
                    () -> assertTrue(novels.contains(novel3))
            );
        }

        @Test
        void whenSearchWithMultipleCriteria_ShouldReturnNovelsMatchingAllCriteria() {
            String keyword = "Fantasy";
            Set<NovelStatus> status = Set.of(NovelStatus.DANG_TIEN_HANH);
            Set<Long> genreIds = Set.of(genre1.getId());
            Pageable pageable = PageRequest.of(0, 10);

            Page<Novel> result = novelRepository.findNovelsByCriteria(keyword, status, genreIds, pageable);
            List<Novel> novels = result.getContent();

            assertAll(
                    () -> assertEquals(2, novels.size()),
                    () -> assertTrue(novels.contains(novel1)),
                    () -> assertFalse(novels.contains(novel2)),
                    () -> assertTrue(novels.contains(novel3))
            );
        }

        @Test
        void whenSearchWithNonMatchingCriteria_ShouldReturnEmptyList() {
            String keyword = "Nonexistent";
            Pageable pageable = PageRequest.of(0, 10);

            Page<Novel> result = novelRepository.findNovelsByCriteria(keyword, null, null, pageable);
            List<Novel> novels = result.getContent();

            assertTrue(novels.isEmpty());
        }

        @Test
        void whenSearchWithPagination_ShouldReturnCorrectPage() {
            for (int i = 0; i < 10; i++) {
                Novel novel = createTestNovel("Extra Novel " + i, user, NovelStatus.DANG_TIEN_HANH);
                addGenreToNovel(novel, genre1);
                entityManager.persist(novel);
            }
            entityManager.flush();

            Pageable firstPage = PageRequest.of(0, 5);
            Pageable secondPage = PageRequest.of(1, 5);

            Page<Novel> firstPageResult = novelRepository.findNovelsByCriteria(null, null, null, firstPage);
            Page<Novel> secondPageResult = novelRepository.findNovelsByCriteria(null, null, null, secondPage);

            assertAll(
                    () -> assertEquals(5, firstPageResult.getContent().size()),
                    () -> assertEquals(5, secondPageResult.getContent().size()),
                    () -> assertEquals(13, firstPageResult.getTotalElements()),
                    () -> assertFalse(firstPageResult.getContent().containsAll(secondPageResult.getContent()))
            );
        }
    }
}
