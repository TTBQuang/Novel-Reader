package com.example.backend.controller;

import com.example.backend.dto.user.UpdateDisplayNameRequest;
import com.example.backend.dto.user.UpdateImageRequest;
import com.example.backend.dto.user.UserBasicInfoDto;
import com.example.backend.dto.user.UserDetailDto;
import com.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<UserBasicInfoDto>> getUsers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(required = false) String keyword) {
        Page<UserBasicInfoDto> users = userService.getUsers(page, size, keyword);
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{userId}/comment-blocked")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> updateUserCommentBlockStatus(
            @PathVariable Long userId, @RequestParam boolean isBlocked) {
        userService.updateUserCommentBlockStatus(userId, isBlocked);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailDto> getUserPublicInfo(@PathVariable Long userId) {
        UserDetailDto user = userService.getUserDetailById(userId);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/avatar")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> updateUserAvatar(@RequestBody UpdateImageRequest updateImageRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Không xác thực được người dùng");
        }

        Long userId = Long.valueOf(authentication.getName());

        userService.updateUserAvatar(userId, updateImageRequest.getImageUrl());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/cover-image")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> updateUserCoverImage(@RequestBody UpdateImageRequest updateImageRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Không xác thực được người dùng");
        }

        Long userId = Long.valueOf(authentication.getName());

        userService.updateUserCoverImage(userId, updateImageRequest.getImageUrl());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/display-name")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> updateUserDisplayName(
            @RequestBody UpdateDisplayNameRequest updateDisplayNameRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Không xác thực được người dùng");
        }

        Long userId = Long.valueOf(authentication.getName());

        userService.updateUserDisplayName(userId, updateDisplayNameRequest.getDisplayName());
        return ResponseEntity.noContent().build();
    }
}