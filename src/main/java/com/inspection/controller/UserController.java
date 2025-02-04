package com.inspection.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inspection.dto.UserResponseDTO;
import com.inspection.dto.UserUpdateDTO;
import com.inspection.entity.User;
import com.inspection.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        UserResponseDTO userDTO = userService.getCurrentUserDTO(user.getUsername());
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(
        @PathVariable Long userId,
        @RequestBody UserUpdateDTO userUpdateDTO
    ) {
        User updatedUser = userService.updateUser(userId, userUpdateDTO);
        UserResponseDTO userDTO = userService.getCurrentUserDTO(updatedUser.getUsername());
        return ResponseEntity.ok(userDTO);
    }

    

} 