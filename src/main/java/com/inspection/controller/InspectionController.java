package com.inspection.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inspection.dto.InspectionBoardDTO;
import com.inspection.dto.InspectionCreateDTO;
import com.inspection.dto.InspectionDetailDTO;
import com.inspection.dto.InspectionListDTO;
import com.inspection.exception.InspectionNotFoundException;
import com.inspection.service.InspectionService;
import com.inspection.service.PdfService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/inspections")
@CrossOrigin(origins = "http://localhost:3001")
@RequiredArgsConstructor
@Slf4j
public class InspectionController {
    private final InspectionService inspectionService;
    private final PdfService pdfService;
    // private final SmsService smsService;
    
    /* 점검 내용 저장 */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createInspection(
        @RequestPart("inspectionData") String inspectionDataStr,
        @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // LocalDate 처리를 위해 필요
            InspectionCreateDTO inspectionData = mapper.readValue(inspectionDataStr, InspectionCreateDTO.class);
            
            // 이미지 파일 저장 및 처리
            List<String> savedImageNames = new ArrayList<>();
            if (images != null && !images.isEmpty()) {
                // uploads/images 디렉토리가 존재하는지 확인하고 없으면 생성
                Path uploadPath = Paths.get("uploads/images");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                for (MultipartFile image : images) {
                    String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                    Path path = uploadPath.resolve(fileName);
                    Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    savedImageNames.add(fileName);
                }
            }
            
            inspectionData.setImages(savedImageNames);
            Long inspectionId = inspectionService.createInspection(inspectionData);
            return ResponseEntity.ok(inspectionId);
        } catch (IOException | IllegalArgumentException e) {
            log.error("점검 데이터 저장 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("점검 데이터 저장에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("점검 데이터 저장 중 서버 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 오류가 발생했습니다.");
        }
    }

    /* 점검 내용 조회 (전체조회/페이징) */
    @GetMapping
    public ResponseEntity<Page<InspectionBoardDTO>> getInspections(Pageable pageable) {
        Page<InspectionBoardDTO> inspections = inspectionService.getInspections(pageable);
        return ResponseEntity.ok(inspections);
    }

    /* 점검 내용 상세조회 */
    @GetMapping("/{id}/detail")
    public ResponseEntity<InspectionDetailDTO> getInspectionDetail(@PathVariable Long id) {
        try {
            InspectionDetailDTO detailDTO = inspectionService.getInspectionDetail(id);
            return ResponseEntity.ok(detailDTO);
        } catch (InspectionNotFoundException e) {
            throw e;
        }
    }

    /* SMS 전송 (개발 전) */ 
    // @PostMapping("/{id}/send-sms")
    // public ResponseEntity<String> sendSmsReport(
    //     @PathVariable Long id,
    //     @RequestParam String phoneNumber
    // ) {
    //     try {
    //         InspectionDetailDTO inspection = inspectionService.getInspectionDetail(id);
    //         String message = String.format("""
    //             [전기설비 점검 결과]
    //             설비명: %s
    //             점검일자: %s
    //             점검자: %s
    //             자세한 내용은 웹에서 확인해주세요.""",
    //             inspection.getFacilityName(),
    //             inspection.getInspectionDate(),
    //             inspection.getManagerName()
    //         );
            
    //         smsService.sendSms(phoneNumber, message);
    //         return ResponseEntity.ok().build();
    //     } catch (Exception e) {
    //         log.error("SMS 전송 실패: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //             .body("SMS 전송에 실패했습니다: " + e.getMessage());
    //     }
    // }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<InspectionListDTO>> getInspectionsByCompany(@PathVariable Long companyId) {
        List<InspectionListDTO> inspections = inspectionService.getInspectionsByCompany(companyId);
        return ResponseEntity.ok(inspections);
    }

    @PostMapping("/{id}/manager-signature")
    @PreAuthorize("hasRole('ADMIN')")
    public InspectionDetailDTO saveManagerSignature(
        @PathVariable Long id,
        @RequestBody Map<String, String> payload
    ) {
        String signature = payload.get("signature");
        return inspectionService.saveManagerSignature(id, signature);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> downloadPdf(@PathVariable Long id) {
        try {
            byte[] pdfBytes = pdfService.generateInspectionPdf(id);
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=inspection_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("PDF 다운로드 중 오류 발생", e);
        }
    }
} 