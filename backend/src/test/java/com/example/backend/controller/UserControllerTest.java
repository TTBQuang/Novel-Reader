package com.example.backend.controller;

import com.example.backend.dto.user.UserDto;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("GetUsers Tests")
    class GetUsersTests {

        @Test
        void whenUsersFound_ShouldReturnPageOfUsers() throws Exception {
            int page = 0;
            int size = 10;
            String keyword = "test";
            List<UserDto> userList = Arrays.asList(
                    createUserDto(1L, "test@email.com", "testUser"),
                    createUserDto(2L, "test2@email.com", "testUser2")
            );
            Pageable pageable = PageRequest.of(0, 10);
            Page<UserDto> userPage = new PageImpl<>(userList, pageable, 0);
            when(userService.getUsers(page, size, keyword)).thenReturn(userPage);

            mockMvc.perform(get("/users")
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size))
                            .param("keyword", keyword))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].id").value(1))
                    .andExpect(jsonPath("$.content[0].email").value("test@email.com"))
                    .andExpect(jsonPath("$.content[1].id").value(2))
                    .andExpect(jsonPath("$.content[1].email").value("test2@email.com"));

            verify(userService).getUsers(page, size, keyword);
        }

        @Test
        void whenUsersNotFound_ShouldReturnEmptyPageWhenNoUsers() throws Exception {
            Pageable pageable = PageRequest.of(0, 10);
            Page<UserDto> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(userService.getUsers(0, 10, null)).thenReturn(emptyPage);

            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(userService).getUsers(0, 10, null);
        }

        @Test
        void whenSizeInvalid_ShouldReturnBadRequest() throws Exception {
            mockMvc.perform(get("/users")
                            .param("page", "0")
                            .param("size", "-1"))
                    .andExpect(status().isBadRequest());

            verify(userService, never()).getUsers(anyInt(), anyInt(), any(String.class));
        }

        @Test
        void whenPageInvalid_ShouldReturnBadRequest() throws Exception {
            mockMvc.perform(get("/users")
                            .param("page", "-1")
                            .param("size", "10"))
                    .andExpect(status().isBadRequest());

            verify(userService, never()).getUsers(anyInt(), anyInt(), any(String.class));
        }
    }

    @Nested
    @DisplayName("UpdateUserCommentBlockStatus Tests")
    class UpdateUserCommentBlockStatusTests {

        @Test
        void whenUserFound_ShouldCallServiceAndReturn204() throws Exception {
            Long userId = 1L;
            boolean isBlocked = true;
            doNothing().when(userService).updateUserCommentBlockStatus(userId, isBlocked);

            mockMvc.perform(patch("/users/{userId}/comment-blocked", userId)
                            .param("isBlocked", String.valueOf(isBlocked)))
                    .andExpect(status().isNoContent());

            verify(userService).updateUserCommentBlockStatus(userId, isBlocked);
        }

        @Test
        void whenUserNotFound_shouldReturn404() throws Exception {
            Long userId = 999L;
            boolean isBlocked = true;
            doThrow(new EntityNotFoundException("User not found"))
                    .when(userService)
                    .updateUserCommentBlockStatus(userId, isBlocked);

            mockMvc.perform(patch("/users/{userId}/comment-blocked", userId)
                            .param("isBlocked", String.valueOf(isBlocked)))
                    .andExpect(status().isNotFound());

            verify(userService).updateUserCommentBlockStatus(userId, isBlocked);
        }
    }

    private UserDto createUserDto(Long id, String email, String username) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setEmail(email);
        dto.setUsername(username);
        dto.setAdmin(false);
        dto.setCommentBlocked(false);
        return dto;
    }
}
