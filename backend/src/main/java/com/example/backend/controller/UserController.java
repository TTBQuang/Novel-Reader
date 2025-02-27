package com.example.backend.controller;

import com.example.backend.dto.user.UserDto;
import com.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(required = false) String keyword) {
        Page<UserDto> users = userService.getUsers(page, size, keyword);
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
}