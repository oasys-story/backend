package com.inspection.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
@Service
public class SttService {

    // application.yml에 정의된 값을 읽어옴
    @Value("${clova.speech.secret}")
    private String secretKey;

    @Value("${clova.speech.invoke-url}")
    private String apiUrl;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String convertSpeechToText(MultipartFile audioFile) throws IOException {
        // log.info("STT 변환 시작. 파일 크기: {}", audioFile.getSize());
        
        // 수신한 파일 정보 상세 로깅
        // log.info("파일 정보: name={}, contentType={}, originalFilename={}", 
        //     audioFile.getName(), 
        //     audioFile.getContentType(), 
        //     audioFile.getOriginalFilename()
        // );

        // Clova Speech API 요청 파라미터 설정
        Map<String, Object> params = new HashMap<>();  // String -> Object로 변경
        
        // 기본 파라미터
        params.put("language", "ko-KR");
        params.put("completion", "sync");
        params.put("sampleRate", 16000);  // 문자열 -> 숫자로 변경
        params.put("encoding", "wav");

        // 오디오 설정
        Map<String, Object> audioConfig = new HashMap<>();
        audioConfig.put("channel", 1);     // 모노
        audioConfig.put("sampleRate", 16000);
        audioConfig.put("format", "pcm");
        audioConfig.put("bitDepth", 16);
        params.put("audio", audioConfig);

        String jsonParams = objectMapper.writeValueAsString(params);
        // log.info("API 요청 파라미터: {}", jsonParams);

        // multipart/form-data 요청 생성
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("params", jsonParams)
                .addFormDataPart("media", audioFile.getOriginalFilename(),
                        RequestBody.create(MediaType.parse("audio/wav"), audioFile.getBytes()))
                .build();

        String fullUrl = apiUrl + "/recognizer/upload";
        // log.info("요청 URL: {}", fullUrl);

        // API 요청 생성
        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("X-CLOVASPEECH-API-KEY", secretKey)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "multipart/form-data")
                .post(requestBody)
                .build();

        // API 호출 및 응답 처리
        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new IOException("응답 본문이 비어있습니다");
            }
            String responseBody = response.body().string();
            // log.info("Clova STT 응답: {}", responseBody);
            
            if (!response.isSuccessful()) {
                throw new IOException("API 호출 실패: " + responseBody);
            }

            return objectMapper.readTree(responseBody)
                    .path("text")
                    .asText();
        }
    }
} 