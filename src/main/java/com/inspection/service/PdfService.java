package com.inspection.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inspection.dto.InspectionDetailDTO;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PdfService {
    
    private final InspectionService inspectionService;
    
    public byte[] generateInspectionPdf(Long inspectionId) {
        try {
            InspectionDetailDTO data = inspectionService.getInspectionDetail(inspectionId);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (PdfWriter writer = new PdfWriter(baos);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf, PageSize.A4)) {
                
                document.setMargins(50, 50, 50, 50);  // 여백 설정

                // 제목
                Paragraph title = new Paragraph("전기설비 점검 결과서")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(24)
                    .setBold()
                    .setMarginBottom(20);
                document.add(title);

                // 기본 정보 테이블
                Table basicInfo = new Table(new float[]{1, 2})
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20);
                
                // 테이블 스타일 적용
                basicInfo.setBackgroundColor(ColorConstants.WHITE, 0.5f);
                basicInfo.setBorder(new SolidBorder(ColorConstants.GRAY, 1));

                addTableHeader(basicInfo, "기본 정보", 2);
                addStyledRow(basicInfo, "업체명", data.getCompanyName());
                addStyledRow(basicInfo, "점검일", data.getInspectionDate().toString());
                addStyledRow(basicInfo, "점검자", data.getManagerName());
                document.add(basicInfo);

                // 기본사항 테이블
                document.add(new Paragraph("\n기본사항").setBold());
                Table specs = new Table(new float[]{1, 1})  
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20);
                
                addTableHeader(specs, "기본사항", 2);

                // 왼쪽 열
                Table leftColumn = new Table(1).setWidth(UnitValue.createPercentValue(100));
                addStyledRow(leftColumn, "수전전압/용량", 
                    String.format("%sV / %skW", data.getFaucetVoltage(), data.getFaucetCapacity()));
                addStyledRow(leftColumn, "발전전압/용량",
                    String.format("%sV / %skW", data.getGenerationVoltage(), data.getGenerationCapacity()));
                addStyledRow(leftColumn, "태양광",
                    data.getSolarCapacity() + "kW");

                // 오른쪽 열
                Table rightColumn = new Table(1).setWidth(UnitValue.createPercentValue(100));
                addStyledRow(rightColumn, "계약용량",
                    data.getContractCapacity() + "kW");
                addStyledRow(rightColumn, "점검종별",
                    data.getInspectionType());
                addStyledRow(rightColumn, "점검횟수",
                    data.getInspectionCount() + "회");

                // 왼쪽, 오른쪽 열을 메인 테이블에 추가
                specs.addCell(new Cell().add(leftColumn).setBorder(Border.NO_BORDER));
                specs.addCell(new Cell().add(rightColumn).setBorder(Border.NO_BORDER));

                document.add(specs);

                // 점검내역 테이블
                document.add(new Paragraph("\n점검내역").setBold());
                Table checkList = new Table(2).setWidth(UnitValue.createPercentValue(100));
                addChecklistItem(checkList, "인입구 배선", data.getWiringInlet());
                addChecklistItem(checkList, "배·분전반", data.getDistributionPanel());
                addChecklistItem(checkList, "배선용 차단기", data.getMoldedCaseBreaker());
                addChecklistItem(checkList, "누전 차단기", data.getEarthLeakageBreaker());
                addChecklistItem(checkList, "개폐기", data.getSwitchGear());
                addChecklistItem(checkList, "배선", data.getWiring());
                addChecklistItem(checkList, "전동기", data.getMotor());
                addChecklistItem(checkList, "전열설비", data.getHeatingEquipment());
                addChecklistItem(checkList, "용접기", data.getWelder());
                addChecklistItem(checkList, "콘덴서", data.getCapacitor());
                addChecklistItem(checkList, "조명설비", data.getLighting());
                addChecklistItem(checkList, "접지설비", data.getGrounding());
                addChecklistItem(checkList, "구내 전선로", data.getInternalWiring());
                addChecklistItem(checkList, "발전기", data.getGenerator());
                addChecklistItem(checkList, "기타설비", data.getOtherEquipment());
                document.add(checkList);

                // 측정개소
                if (data.getMeasurements() != null) {
                    document.add(new Paragraph("\n측정개소").setBold());
                    List<Map<String, Object>> measurements = 
                        new ObjectMapper().readValue(data.getMeasurements(), 
                            new TypeReference<List<Map<String, Object>>>() {});
                    
                    for (Map<String, Object> measurement : measurements) {
                        Table measureTable = new Table(4).setWidth(UnitValue.createPercentValue(100));
                        measureTable.addCell(new Cell().add(new Paragraph("구분")));
                        measureTable.addCell(new Cell().add(new Paragraph("전압(V)")));
                        measureTable.addCell(new Cell().add(new Paragraph("전류(A)")));
                        measureTable.addCell(new Cell().add(new Paragraph("온도(℃)")));
                        
                        String[] phases = {"A", "B", "C", "N"};
                        for (String phase : phases) {
                            measureTable.addCell(new Cell().add(new Paragraph(phase)));
                            measureTable.addCell(new Cell().add(new Paragraph(
                                String.valueOf(measurement.get("voltage" + phase)))));
                            measureTable.addCell(new Cell().add(new Paragraph(
                                String.valueOf(measurement.get("current" + phase)))));
                            measureTable.addCell(new Cell().add(new Paragraph(
                                String.valueOf(measurement.get("temperature" + phase)))));
                        }
                        document.add(measureTable);
                        document.add(new Paragraph("\n"));
                    }
                }

                // 특이사항
                document.add(new Paragraph("\n특이사항").setBold());
                document.add(new Paragraph(data.getSpecialNotes() != null ? 
                    data.getSpecialNotes() : "없음"));

                // 첨부 이미지
                if (data.getImages() != null && !data.getImages().isEmpty()) {
                    document.add(new Paragraph("\n첨부 이미지").setBold());
                    for (String imageName : data.getImages()) {
                        try {
                            Path imagePath = Paths.get("uploads/images/" + imageName);
                            ImageData imageData = ImageDataFactory.create(imagePath.toAbsolutePath().toString());
                            Image pdfImage = new Image(imageData);
                            // 이미지 크기 조정 (너비를 페이지의 80%로)
                            float pageWidth = pdf.getDefaultPageSize().getWidth();
                            pdfImage.setWidth(pageWidth * 0.8f);
                            document.add(pdfImage);
                            document.add(new Paragraph("\n"));
                        } catch (IOException e) {
                            document.add(new Paragraph("이미지 파일을 찾을 수 없습니다: " + imageName));
                        } catch (IllegalArgumentException e) {
                            document.add(new Paragraph("잘못된 이미지 형식입니다: " + imageName));
                        }
                    }
                }

                // 서명
                document.add(new Paragraph("\n서명").setBold());
                Table signatures = new Table(2).setWidth(UnitValue.createPercentValue(100));

                // 점검자 서명
                signatures.addCell(new Cell().add(new Paragraph("점검자 서명")));
                if (data.getSignature() != null) {
                    try {
                        Path signaturePath = Paths.get("uploads/signatures/" + data.getSignature());
                        ImageData imageData = ImageDataFactory.create(signaturePath.toAbsolutePath().toString());
                        Image signatureImage = new Image(imageData);
                        signatureImage.setHeight(50); // 서명 이미지 높이 고정
                        signatures.addCell(new Cell().add(signatureImage));
                    } catch (IOException e) {
                        signatures.addCell(new Cell().add(new Paragraph("서명 이미지 파일을 찾을 수 없습니다")));
                    } catch (IllegalArgumentException e) {
                        signatures.addCell(new Cell().add(new Paragraph("잘못된 서명 이미지 형식입니다")));
                    }
                } else {
                    signatures.addCell(new Cell().add(new Paragraph("서명 없음")));
                }

                // 관리자 서명
                signatures.addCell(new Cell().add(new Paragraph("관리자 서명")));
                if (data.getManagerSignature() != null) {
                    try {
                        Path signaturePath = Paths.get("uploads/signatures/" + data.getManagerSignature());
                        ImageData imageData = ImageDataFactory.create(signaturePath.toAbsolutePath().toString());
                        Image signatureImage = new Image(imageData);
                        signatureImage.setHeight(50);
                        signatures.addCell(new Cell().add(signatureImage));
                    } catch (IOException e) {
                        signatures.addCell(new Cell().add(new Paragraph("서명 이미지 파일을 찾을 수 없습니다")));
                    } catch (IllegalArgumentException e) {
                        signatures.addCell(new Cell().add(new Paragraph("잘못된 서명 이미지 형식입니다")));
                    }
                } else {
                    signatures.addCell(new Cell().add(new Paragraph("서명 없음")));
                }

                document.add(signatures);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("PDF 파일 입출력 오류: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("PDF 생성 파라미터 오류: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("PDF 생성 중 예기치 않은 오류: " + e.getMessage(), e);
        }
    }

    // 테이블 헤더 스타일링 메서드
    private void addTableHeader(Table table, String title, int colspan) {
        Cell headerCell = new Cell(1, colspan)
            .add(new Paragraph(title))
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            .setTextAlignment(TextAlignment.CENTER)
            .setBold()
            .setPadding(10);
        table.addCell(headerCell);
    }

    // 테이블 행 스타일링 메서드
    private void addStyledRow(Table table, String label, String value) {
        table.addCell(
            new Cell()
                .add(new Paragraph(label))
                .setBackgroundColor(ColorConstants.WHITE)
                .setPadding(5)
        );
        table.addCell(
            new Cell()
                .add(new Paragraph(value != null ? value : "-"))
                .setPadding(5)
        );
    }

    // 체크리스트 아이템 스타일링
    private void addChecklistItem(Table table, String label, Character status) {
        table.addCell(
            new Cell()
                .add(new Paragraph(label))
                .setPadding(5)
        );
        
        String statusText = getStatusText(status != null ? status.toString() : null);
        Cell statusCell = new Cell()
            .add(new Paragraph(statusText))
            .setPadding(5)
            .setTextAlignment(TextAlignment.CENTER);
        
        // 상태에 따른 배경색 설정
        switch(statusText) {
            case "적합" -> statusCell.setBackgroundColor(ColorConstants.LIGHT_GRAY, 0.3f);
            case "부적합" -> statusCell.setBackgroundColor(ColorConstants.PINK, 0.3f);
        }
        
        table.addCell(statusCell);
    }

    private String getStatusText(String status) {
        if (status == null) return "-";
        return switch(status) {
            case "O" -> "적합";
            case "X" -> "부적합";
            case "/" -> "해당없음";
            default -> "-";
        };
    }
} 