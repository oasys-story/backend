package com.inspection.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InspectionBoardDTO {
    private Long inspectionId; // 점검 아이디
    private String companyName;    // 업체명 (Long -> String으로 수정)
    private LocalDate inspectionDate; // 점검일
    private String managerName; // 담당자명
} 