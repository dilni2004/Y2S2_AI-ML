package com.sfseduconnect.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String username;
    private String role;
    private String fullName;
    private Long departmentId; // for admins
    private String studentIdNumber; // for students, if needed
}