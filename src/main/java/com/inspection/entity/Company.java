package com.inspection.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;
    
    @Column(unique = true)
    private String companyName;
    
    private String phoneNumber;
    private String faxNumber;
    private String notes;
    
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean active = true;
} 