package com.inspection.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inspection.entity.Inspection;

public interface InspectionRepository extends JpaRepository<Inspection, Long> {
    List<Inspection> findByCompany_CompanyId(Long companyId);
} 