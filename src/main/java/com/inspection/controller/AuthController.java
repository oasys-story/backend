package com.inspection.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inspection.dto.LoginRequest;
import com.inspection.dto.LoginResponse;
import com.inspection.dto.UserCreateDTO;
import com.inspection.dto.UserResponseDTO;
import com.inspection.entity.User;
import com.inspection.security.JwtTokenProvider;
import com.inspection.service.UserService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;


    /* 회원가입 */
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signup(@RequestBody UserCreateDTO userCreateDTO) {
        User savedUser = userService.createUser(userCreateDTO);
        UserResponseDTO responseDTO = userService.getCurrentUserDTO(savedUser.getUsername());
        return ResponseEntity.ok(responseDTO);
    }

    /* 현재 사용자 정보 조회 */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponseDTO userDTO = userService.getCurrentUserDTO(userDetails.getUsername());
        return ResponseEntity.ok(userDTO);
    }

    /* 아이디 중복 체크 */
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        if (userService.existsByUsername(username)) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)  
                .body("이미 사용중인 아이디입니다.");
        }
        return ResponseEntity.ok("사용 가능한 아이디입니다.");
    }

    /* 로그인 */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            )
        );

        String token = tokenProvider.generateToken(authentication);
        UserResponseDTO userResponse = userService.getCurrentUserDTO(loginRequest.getUsername());
        
        return ResponseEntity.ok(new LoginResponse(token, userResponse));
    }



    /* 로그아웃 */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().body("로그아웃 되었습니다.");
    }
} 