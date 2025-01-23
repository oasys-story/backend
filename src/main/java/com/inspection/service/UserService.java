package com.inspection.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inspection.dto.UserCreateDTO;
import com.inspection.dto.UserResponseDTO;
import com.inspection.dto.UserUpdateDTO;
import com.inspection.entity.Company;
import com.inspection.entity.User;
import com.inspection.repository.CompanyRepository;
import com.inspection.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRole().name())
            .build();
    }

    @Transactional
    public User createUser(UserCreateDTO userCreateDTO) {
        User user = userCreateDTO.toEntity();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (userCreateDTO.getCompanyId() != null) {
            Company company = companyRepository.findById(userCreateDTO.getCompanyId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사입니다."));
            user.setCompany(company);
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("이미 존재하는 사용자명입니다.");
        }

        return userRepository.save(user);
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean validateAdminCode(String adminCode) {
        return "ADMIN123".equals(adminCode);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(UserResponseDTO::new)
            .collect(Collectors.toList());
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));
    }

    @Transactional
    public User updateUser(Long userId, UserUpdateDTO userUpdateDTO) {
        User user = getUserById(userId);
        
        user.setFullName(userUpdateDTO.getFullName());
        user.setEmail(userUpdateDTO.getEmail());
        user.setPhoneNumber(userUpdateDTO.getPhoneNumber());
        user.setRole(userUpdateDTO.getRole());
        user.setActive(userUpdateDTO.isActive());
        
        if (userUpdateDTO.getCompanyId() != null) {
            Company company = companyRepository.findById(userUpdateDTO.getCompanyId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사입니다."));
            user.setCompany(company);
        }
        
        return userRepository.save(user);
    }
} 