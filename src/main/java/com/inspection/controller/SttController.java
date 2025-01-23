package com.inspection.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.inspection.service.SttService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/stt")
@RequiredArgsConstructor
public class SttController {

    private final SttService sttService;

    @PostMapping("/convert")
    public ResponseEntity<String> convertSpeechToText(
            @RequestParam("audio") MultipartFile audioFile) {
        try {
            // log.info("음성 파일 수신. 크기: {} bytes", audioFile.getSize());
            String text = sttService.convertSpeechToText(audioFile);
            return ResponseEntity.ok(text);
        } catch (IOException e) {
            log.error("STT 서비스 통신 실패", e);
            return ResponseEntity.internalServerError().body("음성 인식 서비스 연결에 실패했습니다: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("STT 파라미터 오류", e);
            return ResponseEntity.badRequest().body("잘못된 음성 파일입니다: " + e.getMessage());
        }
    }
}