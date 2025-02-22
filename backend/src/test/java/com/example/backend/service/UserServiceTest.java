package com.example.backend.service;

import com.example.backend.dto.user.UserDto;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@email.com");
        user.setUsername("testUser");
        user.setIsAdmin(false);
        user.setIsCommentBlocked(false);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@email.com");
        userDto.setUsername("testUser");
        userDto.setAdmin(false);
        userDto.setCommentBlocked(false);
    }

    @Nested
    @DisplayName("getUsers Tests")
    class GetUsersTests {

        @Test
        void whenKeywordIsNull_ShouldReturnNonAdminUsers() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

            when(userRepository.findByIsAdminFalse(pageable)).thenReturn(userPage);
            when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

            Page<UserDto> result = userService.getUsers(0, 10, null);

            assertEquals(1, result.getTotalElements());
            verify(userRepository).findByIsAdminFalse(pageable);
            verify(modelMapper).map(any(User.class), eq(UserDto.class));
        }

        @Test
        void whenKeywordExists_ShouldSearchByKeyword() {
            String keyword = "test";
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

            when(userRepository.searchByKeyword(keyword, pageable)).thenReturn(userPage);
            when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

            Page<UserDto> result = userService.getUsers(0, 10, keyword);

            assertEquals(1, result.getTotalElements());
            verify(userRepository).searchByKeyword(keyword, pageable);
            verify(modelMapper).map(any(User.class), eq(UserDto.class));
        }

        @Test
        void whenMappingFails_ShouldThrowException() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

            when(userRepository.findByIsAdminFalse(pageable)).thenReturn(userPage);
            when(modelMapper.map(any(User.class), eq(UserDto.class)))
                    .thenThrow(RuntimeException.class);

            assertThrows(RuntimeException.class,
                    () -> userService.getUsers(0, 10, null));

            verify(userRepository).findByIsAdminFalse(pageable);
            verify(modelMapper).map(any(User.class), eq(UserDto.class));
        }

        @Test
        void whenNoUsersExist_ShouldReturnEmptyPage() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> userPage = new PageImpl<>(Collections.emptyList());

            when(userRepository.findByIsAdminFalse(pageable)).thenReturn(userPage);

            Page<UserDto> result = userService.getUsers(0, 10, null);

            assertEquals(0, result.getTotalElements());
            verify(userRepository).findByIsAdminFalse(pageable);
            verify(modelMapper, never()).map(any(), any());
        }
    }

    @Nested
    @DisplayName("updateUserCommentBlockStatus Tests")
    class UpdateUserCommentBlockStatusTests {

        @Test
        void whenUserExists_ShouldUpdateBlockStatus() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            userService.updateUserCommentBlockStatus(1L, true);

            assertTrue(user.getIsCommentBlocked());
            verify(userRepository).findById(1L);
            verify(userRepository).save(user);
        }

        @Test
        void whenUserNotExists_ShouldThrowException() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> userService.updateUserCommentBlockStatus(1L, true));

            verify(userRepository).findById(1L);
            verify(userRepository, never()).save(any());
        }

        @Test
        void whenSaveFails_ShouldThrowException() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenThrow(RuntimeException.class);

            assertThrows(RuntimeException.class,
                    () -> userService.updateUserCommentBlockStatus(1L, true));

            verify(userRepository).findById(1L);
            verify(userRepository).save(user);
        }
    }

    @Nested
    @DisplayName("getUserById Tests")
    class GetUserByIdTests {

        @Test
        void whenUserExists_ShouldReturnUserDto() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

            UserDto result = userService.getUserById(1L);

            assertEquals(userDto.getId(), result.getId());
            assertEquals(userDto.getEmail(), result.getEmail());
            verify(userRepository).findById(1L);
            verify(modelMapper).map(user, UserDto.class);
        }

        @Test
        void whenUserNotExists_ShouldThrowException() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> userService.getUserById(1L));

            verify(userRepository).findById(1L);
            verify(modelMapper, never()).map(any(), any());
        }

        @Test
        void whenMappingFails_ShouldThrowException() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(modelMapper.map(any(User.class), eq(UserDto.class)))
                    .thenThrow(RuntimeException.class);

            assertThrows(RuntimeException.class,
                    () -> userService.getUserById(1L));

            verify(userRepository).findById(1L);
            verify(modelMapper).map(any(User.class), eq(UserDto.class));
        }
    }
}
