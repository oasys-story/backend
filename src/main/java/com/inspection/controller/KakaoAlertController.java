package com.inspection.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inspection.service.KakaoAlertService;
import com.inspection.dto.KakaoAlertRequestDTO;

@RestController
@RequestMapping("/api/kakao-alert")
@RequiredArgsConstructor
@Slf4j
public class KakaoAlertController {

    private final KakaoAlertService kakaoAlertService;

    @PostMapping("/inspection/{id}")
    public ResponseEntity<?> sendInspectionAlert(
        @PathVariable Long id,
        @RequestBody KakaoAlertRequestDTO request
    ) {
        try {
            log.info("Sending inspection result alert to: {}", request.getPhoneNumber());
            
            // URL만 전달 (변수 매핑용)
            String url = String.format("safe.jebee.net/inspection/%d", id);
            
            kakaoAlertService.sendAlert(request.getPhoneNumber(), url);
            return ResponseEntity.ok().body("알림톡 전송 성공");
        } catch (Exception e) {
            log.error("Failed to send inspection alert: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("알림톡 전송 실패: " + e.getMessage());
        }
    }

    @PostMapping("/fire-safety-inspection/{id}")
    public ResponseEntity<?> sendFireSafetyInspectionAlert(
        @PathVariable Long id,
        @RequestBody KakaoAlertRequestDTO request
    ) {
        try {
            log.info("Sending fire safety inspection result alert to: {}", request.getPhoneNumber());
            
            // URL만 전달 (변수 매핑용)
            String url = String.format("safe.jebee.net/fire-safety-inspection/%d", id);
            
            kakaoAlertService.sendAlert(request.getPhoneNumber(), url);
            return ResponseEntity.ok().body("알림톡 전송 성공");
        } catch (Exception e) {
            log.error("Failed to send fire safety inspection alert: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("알림톡 전송 실패: " + e.getMessage());
        }
    }
} 