package com.inspection.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Inspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inspectionId;
    
    // 기본 정보
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;        // 업체 ID
    private LocalDate inspectionDate;   // 점검일자
    private String managerName;         // 관리사 이름
    
    // 기본사항 필드들
    private Integer faucetVoltage;              // 수전전압(V)
    private Integer faucetCapacity;             // 수전용량(kW)
    private Integer generationVoltage;          // 발전전압(V)
    private Integer generationCapacity;         // 발전용량(kW)
    private Integer solarCapacity;              // 태양광(kW)
    private Integer contractCapacity;           // 계약용량(kW)
    private String inspectionType;              // 점검종별
    private Integer inspectionCount;            // 점검횟수
    
    // 점검 내역 필드들
    private Character wiringInlet;           // 인입구 배선
    private Character distributionPanel;     // 배*분전반
    private Character moldedCaseBreaker;     // 배선용 차단기
    private Character earthLeakageBreaker;   // 누전 차단기
    private Character switchGear;            // 개폐기
    private Character wiring;                // 배선
    private Character motor;                 // 전동기
    private Character heatingEquipment;      // 가열장치
    private Character welder;                // 용접기
    private Character capacitor;             // 콘덴서
    private Character lighting;              // 조명설비
    private Character grounding;             // 접지설비
    private Character internalWiring;        // 구내배선
    private Character generator;             // 발전기
    private Character otherEquipment;        // 기타설비
    
    // 측정개소 필드들 (JSON으로 저장)
    @Column(columnDefinition = "JSON")
    private String measurements;             // 측정개소 데이터 (JSON 형식)
    
    // 특이사항 필드
    @Column(columnDefinition = "TEXT")
    private String specialNotes;            // 특이사항
    
    // 서명 필드들
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String signature;               // 점검자 서명 데이터
    
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String managerSignature;        // 관리자 서명 데이터
    
    @Column(columnDefinition = "JSON")
    private String images;    // 첨부 이미지 파일명들을 JSON 배열로 저장
} 