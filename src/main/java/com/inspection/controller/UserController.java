package com.inspection.controller;

import com.inspection.dto.UserResponseDTO;
import com.inspection.dto.UserUpdateDTO;
import com.inspection.service.UserService;
import com.inspection.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(new UserResponseDTO(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(
        @PathVariable Long userId,
        @RequestBody UserUpdateDTO userUpdateDTO
    ) {
        User updatedUser = userService.updateUser(userId, userUpdateDTO);
        return ResponseEntity.ok(new UserResponseDTO(updatedUser));
    }
} 