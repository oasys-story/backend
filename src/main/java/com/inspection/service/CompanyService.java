package com.inspection.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inspection.dto.CompanyDTO;
import com.inspection.entity.Company;
import com.inspection.repository.CompanyRepository;
import com.inspection.util.AESEncryption;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final AESEncryption aesEncryption;

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
        
        // 전화번호 암호화
        if (companyDTO.getPhoneNumber() != null) {
            company.setPhoneNumber(aesEncryption.encrypt(companyDTO.getPhoneNumber()));
        }
        
        // 팩스번호 암호화
        if (companyDTO.getFaxNumber() != null) {
            company.setFaxNumber(aesEncryption.encrypt(companyDTO.getFaxNumber()));
        }
        
        company.setNotes(companyDTO.getNotes());
        company.setActive(companyDTO.isActive());
        
        Company savedCompany = companyRepository.save(company);
        return convertToDTO(savedCompany);
    }

    private CompanyDTO convertToDTO(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setCompanyId(company.getCompanyId());
        dto.setCompanyName(company.getCompanyName());
        
        // 전화번호 복호화
        if (company.getPhoneNumber() != null && !company.getPhoneNumber().isEmpty()) {
            try {
                dto.setPhoneNumber(aesEncryption.decrypt(company.getPhoneNumber()));
            } catch (Exception e) {
                dto.setPhoneNumber(company.getPhoneNumber());
            }
        }
        
        // 팩스번호 복호화
        if (company.getFaxNumber() != null && !company.getFaxNumber().isEmpty()) {
            try {
                dto.setFaxNumber(aesEncryption.decrypt(company.getFaxNumber()));
            } catch (Exception e) {
                dto.setFaxNumber(company.getFaxNumber());
            }
        }
        
        dto.setNotes(company.getNotes());
        dto.setActive(company.isActive());
        return dto;
    }

    private void updateCompanyFromDTO(Company company, CompanyDTO dto) {
        company.setCompanyName(dto.getCompanyName());
        
        // 전화번호 암호화
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isEmpty()) {
            company.setPhoneNumber(aesEncryption.encrypt(dto.getPhoneNumber()));
        }
        
        // 팩스번호 암호화
        if (dto.getFaxNumber() != null && !dto.getFaxNumber().isEmpty()) {
            company.setFaxNumber(aesEncryption.encrypt(dto.getFaxNumber()));
        }
        
        company.setNotes(dto.getNotes());
        company.setActive(true);
    }

    @Transactional
    public void deleteCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new RuntimeException("업체를 찾을 수 없습니다. ID: " + companyId));
        companyRepository.delete(company);
    }

    @Transactional(readOnly = true)
    public CompanyDTO getCompanyDTOById(Long companyId) {
        Company company = getCompanyById(companyId);
        return convertToDTO(company);
    }
} 