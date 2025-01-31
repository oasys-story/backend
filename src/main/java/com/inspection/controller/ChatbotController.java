package com.inspection.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inspection.dto.ChatbotRequest;
import com.inspection.dto.ChatbotResponse;
import com.inspection.service.ChatbotService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3001")
@RequiredArgsConstructor
public class ChatbotController {
    private final ChatbotService chatbotService;
    private static final Logger log = LoggerFactory.getLogger(ChatbotController.class);
    
    @PostMapping
    public ChatbotResponse chat(
        @RequestBody ChatbotRequest request,
        @AuthenticationPrincipal UserDetails userDetails  // 현재 인증된 사용자 정보
    ) {
        // 사용자 ID 설정 (로그인한 경우 해당 ID, 아닌 경우 'guest')
        String userId = (userDetails != null) ? 
            userDetails.getUsername() : "guest";
            
        log.info("Chat request from user: {}", userId);
        return chatbotService.chat(request.getQuestion(), userId);
    }
} 