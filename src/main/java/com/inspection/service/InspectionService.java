package com.inspection.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inspection.dto.InspectionBoardDTO;
import com.inspection.dto.InspectionCreateDTO;
import com.inspection.dto.InspectionDetailDTO;
import com.inspection.dto.InspectionListDTO;
import com.inspection.entity.Company;
import com.inspection.entity.Inspection;
import com.inspection.exception.InspectionNotFoundException;
import com.inspection.repository.CompanyRepository;
import com.inspection.repository.InspectionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InspectionService {
    
    private final InspectionRepository inspectionRepository;
    private final CompanyRepository companyRepository;
    
    @Transactional
    public Long createInspection(InspectionCreateDTO dto) {
        try {
            // Company 엔티티 조회
            Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

            // Inspection 엔티티 생성
            Inspection inspection = new Inspection();
            inspection.setCompany(company);
            inspection.setInspectionDate(dto.getInspectionDate());
            inspection.setManagerName(dto.getManagerName());
            
            // 기본사항
            inspection.setFaucetVoltage(dto.getFaucetVoltage());
            inspection.setFaucetCapacity(dto.getFaucetCapacity());
            inspection.setGenerationVoltage(dto.getGenerationVoltage());
            inspection.setGenerationCapacity(dto.getGenerationCapacity());
            inspection.setSolarCapacity(dto.getSolarCapacity());
            inspection.setContractCapacity(dto.getContractCapacity());
            inspection.setInspectionType(dto.getInspectionType());
            inspection.setInspectionCount(dto.getInspectionCount());
            
            // 점검내역
            inspection.setWiringInlet(dto.getWiringInlet());
            inspection.setDistributionPanel(dto.getDistributionPanel());
            inspection.setMoldedCaseBreaker(dto.getMoldedCaseBreaker());
            inspection.setEarthLeakageBreaker(dto.getEarthLeakageBreaker());
            inspection.setSwitchGear(dto.getSwitchGear());
            inspection.setWiring(dto.getWiring());
            inspection.setMotor(dto.getMotor());
            inspection.setHeatingEquipment(dto.getHeatingEquipment());
            inspection.setWelder(dto.getWelder());
            inspection.setCapacitor(dto.getCapacitor());
            inspection.setLighting(dto.getLighting());
            inspection.setGrounding(dto.getGrounding());
            inspection.setInternalWiring(dto.getInternalWiring());
            inspection.setGenerator(dto.getGenerator());
            inspection.setOtherEquipment(dto.getOtherEquipment());
            
            // measurements를 JSON 문자열로 변환
            if (dto.getMeasurements() != null) {
                String measurementsJson = new ObjectMapper().writeValueAsString(dto.getMeasurements());
                inspection.setMeasurements(measurementsJson);
            }
            
            // 특이사항
            inspection.setSpecialNotes(dto.getSpecialNotes());
            
            // 서명
            inspection.setSignature(dto.getSignature());
            
            // 이미지
            if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                inspection.setImages(new ObjectMapper().writeValueAsString(dto.getImages()));
            }
            
            // 저장
            inspection = inspectionRepository.save(inspection);
            return inspection.getInspectionId();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 처리 중 오류 발생: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("잘못된 데이터 형식: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("점검 데이터 저장 중 오류 발생: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public InspectionDetailDTO getInspectionDetail(Long inspectionId) {
        try {
            Inspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new InspectionNotFoundException(inspectionId));
            
            InspectionDetailDTO detailDTO = new InspectionDetailDTO();
            
            // 기본 정보 매핑
            detailDTO.setInspectionId(inspection.getInspectionId());
            
            // Company 정보 매핑
            Company company = inspection.getCompany(); // Company 엔티티 가져오기
            detailDTO.setCompanyId(company.getCompanyId());
            detailDTO.setCompanyName(company.getCompanyName()); // Company 엔티티에서 이름 가져오기
            
            detailDTO.setInspectionDate(inspection.getInspectionDate());
            detailDTO.setManagerName(inspection.getManagerName());
            
            // 기본사항
            detailDTO.setFaucetVoltage(inspection.getFaucetVoltage());
            detailDTO.setFaucetCapacity(inspection.getFaucetCapacity());
            detailDTO.setGenerationVoltage(inspection.getGenerationVoltage());
            detailDTO.setGenerationCapacity(inspection.getGenerationCapacity());
            detailDTO.setSolarCapacity(inspection.getSolarCapacity());
            detailDTO.setContractCapacity(inspection.getContractCapacity());
            detailDTO.setInspectionType(inspection.getInspectionType());
            detailDTO.setInspectionCount(inspection.getInspectionCount());
            
            // 점검내역
            detailDTO.setWiringInlet(inspection.getWiringInlet());
            detailDTO.setDistributionPanel(inspection.getDistributionPanel());
            detailDTO.setMoldedCaseBreaker(inspection.getMoldedCaseBreaker());
            detailDTO.setEarthLeakageBreaker(inspection.getEarthLeakageBreaker());
            detailDTO.setSwitchGear(inspection.getSwitchGear());
            detailDTO.setWiring(inspection.getWiring());
            detailDTO.setMotor(inspection.getMotor());
            detailDTO.setHeatingEquipment(inspection.getHeatingEquipment());
            detailDTO.setWelder(inspection.getWelder());
            detailDTO.setCapacitor(inspection.getCapacitor());
            detailDTO.setLighting(inspection.getLighting());
            detailDTO.setGrounding(inspection.getGrounding());
            detailDTO.setInternalWiring(inspection.getInternalWiring());
            detailDTO.setGenerator(inspection.getGenerator());
            detailDTO.setOtherEquipment(inspection.getOtherEquipment());
            
            // 측정개소
            detailDTO.setMeasurements(inspection.getMeasurements());
            
            // 특이사항
            detailDTO.setSpecialNotes(inspection.getSpecialNotes());
            
            // 서명 정보 매핑 - 필드명 일치시키기
            detailDTO.setSignature(inspection.getSignature());  // inspectorSignature가 아닌 signature
            detailDTO.setManagerSignature(inspection.getManagerSignature());
            
            // 이미지 데이터 처리
            if (inspection.getImages() != null && !inspection.getImages().isEmpty()) {
                List<String> imagesList = new ObjectMapper().readValue(
                    inspection.getImages(),
                    new TypeReference<List<String>>() {}
                );
                detailDTO.setImages(imagesList);
            }
            
            return detailDTO;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("이미지 데이터 처리 중 오류 발생", e);
        }
    }
    
    @Transactional(readOnly = true)
    public List<InspectionListDTO> getAllInspections() {
        return inspectionRepository.findAll().stream()
            .map(inspection -> {
                InspectionListDTO dto = new InspectionListDTO();
                dto.setInspectionId(inspection.getInspectionId());
                dto.setCompanyId(inspection.getCompany().getCompanyId());
                dto.setInspectionDate(inspection.getInspectionDate());
                dto.setManagerName(inspection.getManagerName());
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<InspectionBoardDTO> getInspections(Pageable pageable) {
        return inspectionRepository.findAll(pageable)
            .map(inspection -> {
                InspectionBoardDTO dto = new InspectionBoardDTO();
                dto.setInspectionId(inspection.getInspectionId());
                dto.setCompanyName(inspection.getCompany().getCompanyName());  // 업체명 직접 가져오기
                dto.setInspectionDate(inspection.getInspectionDate());
                dto.setManagerName(inspection.getManagerName());
                return dto;
            });
    }

    // 업체별 점검 내역 조회 메서드 추가
    @Transactional(readOnly = true)
    public List<InspectionListDTO> getInspectionsByCompany(Long companyId) {
        return inspectionRepository.findByCompany_CompanyId(companyId).stream()
            .map(inspection -> {
                InspectionListDTO dto = new InspectionListDTO();
                dto.setInspectionId(inspection.getInspectionId());
                dto.setCompanyId(inspection.getCompany().getCompanyId());
                dto.setInspectionDate(inspection.getInspectionDate());
                dto.setManagerName(inspection.getManagerName());
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public InspectionDetailDTO saveManagerSignature(Long inspectionId, String signature) {
        Inspection inspection = inspectionRepository.findById(inspectionId)
            .orElseThrow(() -> new InspectionNotFoundException(inspectionId));
        
        inspection.setManagerSignature(signature);
        inspectionRepository.save(inspection);
        
        return getInspectionDetail(inspectionId);
    }
} 