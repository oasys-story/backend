package com.inspection.service;

import com.inspection.dto.CompanyDTO;
import com.inspection.entity.Company;
import com.inspection.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public CompanyDTO createCompany(CompanyDTO companyDTO) {
        Company company = new Company();
        updateCompanyFromDTO(company, companyDTO);
        Company savedCompany = companyRepository.save(company);
        return convertToDTO(savedCompany);
    }

    @Transactional
    public Company getOrCreateCompany(String companyName) {
        return companyRepository.findByCompanyName(companyName)
            .orElseGet(() -> {
                Company newCompany = new Company();
                newCompany.setCompanyName(companyName);
                newCompany.setActive(true);
                return companyRepository.save(newCompany);
            });
    }

    @Transactional(readOnly = true)
    public Company getCompanyById(Long companyId) {
        return companyRepository.findById(companyId)
            .orElseThrow(() -> new RuntimeException("업체를 찾을 수 없습니다. ID: " + companyId));
    }

    @Transactional
    public CompanyDTO updateCompany(Long companyId, CompanyDTO companyDTO) {
        Company company = getCompanyById(companyId);
        
        company.setCompanyName(companyDTO.getCompanyName());
        company.setPhoneNumber(companyDTO.getPhoneNumber());
        company.setFaxNumber(companyDTO.getFaxNumber());
        company.setNotes(companyDTO.getNotes());
        company.setActive(companyDTO.isActive());
        
        Company savedCompany = companyRepository.save(company);
        return convertToDTO(savedCompany);
    }

    private CompanyDTO convertToDTO(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setCompanyId(company.getCompanyId());
        dto.setCompanyName(company.getCompanyName());
        dto.setPhoneNumber(company.getPhoneNumber());
        dto.setFaxNumber(company.getFaxNumber());
        dto.setNotes(company.getNotes());
        dto.setActive(company.isActive());
        return dto;
    }

    private void updateCompanyFromDTO(Company company, CompanyDTO dto) {
        company.setCompanyName(dto.getCompanyName());
        company.setPhoneNumber(dto.getPhoneNumber());
        company.setFaxNumber(dto.getFaxNumber());
        company.setNotes(dto.getNotes());
        company.setActive(true);  // 새로 생성시 기본값 true
    }

    @Transactional
    public void deleteCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new RuntimeException("업체를 찾을 수 없습니다. ID: " + companyId));
        companyRepository.delete(company);
    }
} 