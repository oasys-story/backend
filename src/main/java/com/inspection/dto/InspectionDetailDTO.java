package com.inspection.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InspectionDetailDTO {
    private Long inspectionId;
    
    // 기본 정보
    private Long companyId;
    private String companyName;
    private LocalDate inspectionDate;
    private String managerName;
    
    // 기본사항
    private Integer faucetVoltage;
    private Integer faucetCapacity;
    private Integer generationVoltage;
    private Integer generationCapacity;
    private Integer solarCapacity;
    private Integer contractCapacity;
    private String inspectionType;
    private Integer inspectionCount;
    
    // 점검내역
    private Character wiringInlet;
    private Character distributionPanel;
    private Character moldedCaseBreaker;
    private Character earthLeakageBreaker;
    private Character switchGear;
    private Character wiring;
    private Character motor;
    private Character heatingEquipment;
    private Character welder;
    private Character capacitor;
    private Character lighting;
    private Character grounding;
    private Character internalWiring;
    private Character generator;
    private Character otherEquipment;
    
    // 측정개소
    private String measurements;  // JSON 문자열
    
    // 특이사항
    private String specialNotes;
    
    private String signature;  // inspectorSignature가 아닌 signature로 변경
    private String managerSignature;
    
    private List<String> images;  // 이미지 파일명 리스트
    
    // 작성자 정보
    private Long userId;          // 작성자 ID
    private String username;      // 작성자 이름
    
    public String getSignature() {
        return signature;
    }
    
    public void setSignature(String signature) {
        this.signature = signature;
    }
    
    public String getManagerSignature() {
        return managerSignature;
    }
    
    public void setManagerSignature(String managerSignature) {
        this.managerSignature = managerSignature;
    }
} 