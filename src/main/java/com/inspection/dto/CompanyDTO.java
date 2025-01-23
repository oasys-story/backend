package com.inspection.dto;

import com.inspection.entity.Company;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CompanyDTO {
    private Long companyId;
    private String companyName;
    private String phoneNumber;
    private String faxNumber;
    private String notes;
    private boolean active;
    
    public CompanyDTO() {}
    
    public CompanyDTO(Company company) {
        this.companyId = company.getCompanyId();
        this.companyName = company.getCompanyName();
        this.phoneNumber = company.getPhoneNumber();
        this.faxNumber = company.getFaxNumber();
        this.notes = company.getNotes();
        this.active = company.isActive();
    }
} 