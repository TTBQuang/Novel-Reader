package com.example.backend.repository;

import com.example.backend.entity.User;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User normalUser1;
    private User normalUser2;
    private User adminUser;

    private User createTestUser(String username, String email, boolean isAdmin) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("hashedPassword");
        user.setIsAdmin(isAdmin);
        user.setIsCommentBlocked(false);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    @BeforeEach
    void setUp() {
        normalUser1 = createTestUser("normaluser1", "normal1@gmail.com", false);
        normalUser2 = createTestUser("normaluser2", "normal2@gmail.com", false);
        adminUser = createTestUser("adminuser", "admin@gmail.com", true);

        normalUser1 = entityManager.persist(normalUser1);
        normalUser2 = entityManager.persist(normalUser2);
        adminUser = entityManager.persist(adminUser);

        entityManager.flush();
    }

    @Nested
    @DisplayName("Find non-admin users test")
    class FindNonAdminUsersTest {
        @Test
        void whenFindByIsAdminFalse_ShouldReturnOnlyNonAdminUsers() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<User> result = userRepository.findByIsAdminFalse(pageable);
            List<User> users = result.getContent();

            assertAll(
                    () -> assertEquals(2, users.size()),
                    () -> assertTrue(users.contains(normalUser1)),
                    () -> assertTrue(users.contains(normalUser2)),
                    () -> assertFalse(users.contains(adminUser))
            );
        }

        @Test
        void whenFindByIsAdminFalse_WithPagination_ShouldReturnCorrectPage() {
            for (int i = 3; i <= 12; i++) {
                User user = createTestUser("normaluser" + i, "normal" + i + "@gmail.com", false);
                entityManager.persist(user);
            }
            entityManager.flush();
            Pageable firstPage = PageRequest.of(0, 5);
            Pageable secondPage = PageRequest.of(1, 5);

            Page<User> firstPageResult = userRepository.findByIsAdminFalse(firstPage);
            Page<User> secondPageResult = userRepository.findByIsAdminFalse(secondPage);

            assertAll(
                    () -> assertEquals(5, firstPageResult.getContent().size()),
                    () -> assertEquals(5, secondPageResult.getContent().size()),
                    () -> assertEquals(12, firstPageResult.getTotalElements())
            );
        }
    }

    @Nested
    @DisplayName("Search users by keyword test")
    class SearchByKeywordTest {
        @Test
        void whenSearchByUsernameKeyword_ShouldReturnMatchingUsers() {
            String keyword = "user1";
            Pageable pageable = PageRequest.of(0, 10);

            Page<User> result = userRepository.searchByKeyword(keyword, pageable);
            List<User> users = result.getContent();

            assertAll(
                    () -> assertEquals(1, users.size()),
                    () -> assertTrue(users.contains(normalUser1)),
                    () -> assertFalse(users.contains(normalUser2)),
                    () -> assertFalse(users.contains(adminUser))
            );
        }

        @Test
        void whenSearchByEmailKeyword_ShouldReturnMatchingUsers() {
            String keyword = "normal1";
            Pageable pageable = PageRequest.of(0, 10);

            Page<User> result = userRepository.searchByKeyword(keyword, pageable);
            List<User> users = result.getContent();

            assertAll(
                    () -> assertEquals(1, users.size()),
                    () -> assertTrue(users.contains(normalUser1)),
                    () -> assertFalse(users.contains(normalUser2)),
                    () -> assertFalse(users.contains(adminUser))
            );
        }

        @Test
        void whenSearchWithEmptyKeyword_ShouldReturnAllNonAdminUsers() {
            String keyword = "";
            Pageable pageable = PageRequest.of(0, 10);

            Page<User> result = userRepository.searchByKeyword(keyword, pageable);
            List<User> users = result.getContent();

            assertAll(
                    () -> assertEquals(2, users.size()),
                    () -> assertTrue(users.contains(normalUser1)),
                    () -> assertTrue(users.contains(normalUser2)),
                    () -> assertFalse(users.contains(adminUser))
            );
        }

        @Test
        void whenSearchWithCaseInsensitiveKeyword_ShouldReturnMatchingUsers() {
            String keyword = "NORMALUSER";
            Pageable pageable = PageRequest.of(0, 10);

            Page<User> result = userRepository.searchByKeyword(keyword, pageable);
            List<User> users = result.getContent();

            assertAll(
                    () -> assertEquals(2, users.size()),
                    () -> assertTrue(users.contains(normalUser1)),
                    () -> assertTrue(users.contains(normalUser2)),
                    () -> assertFalse(users.contains(adminUser))
            );
        }

        @Test
        void whenSearchWithNonMatchingKeyword_ShouldReturnEmptyList() {
            String keyword = "nonexistent";
            Pageable pageable = PageRequest.of(0, 10);

            Page<User> result = userRepository.searchByKeyword(keyword, pageable);
            List<User> users = result.getContent();

            assertTrue(users.isEmpty());
        }
    }
}
