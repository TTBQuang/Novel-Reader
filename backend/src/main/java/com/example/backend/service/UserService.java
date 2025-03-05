package com.example.backend.service;

import com.example.backend.dto.user.UserBasicInfoDto;
import com.example.backend.dto.user.UserDetailDto;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public Page<UserBasicInfoDto> getUsers(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;

        if (keyword == null || keyword.trim().isEmpty()) {
            userPage = userRepository.findByIsAdminFalse(pageable);
        } else {
            userPage = userRepository.searchByKeyword(keyword, pageable);
        }

        return userPage.map(user -> modelMapper.map(user, UserBasicInfoDto.class));
    }

    public void updateUserCommentBlockStatus(Long userId, boolean isBlocked) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setIsCommentBlocked(isBlocked);
        userRepository.save(user);
    }

    public UserDetailDto getUserDetailById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException ("User not found"));
        return modelMapper.map(user, UserDetailDto.class);
    }

    public UserBasicInfoDto getUserBasicInfoById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return modelMapper.map(user, UserBasicInfoDto.class);
    }

    public void updateUserAvatar(Long userId, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setAvatar(imageUrl);
        userRepository.save(user);
    }

    public void updateUserCoverImage(Long userId, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setCoverImage(imageUrl);
        userRepository.save(user);
    }

    public void updateUserDisplayName(Long userId, String displayName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setDisplayName(displayName);
        userRepository.save(user);
    }
}

