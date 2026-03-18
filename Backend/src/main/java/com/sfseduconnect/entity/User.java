package com.sfs.educonnect.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String role;          // "STUDENT", "DEPT_ADMIN", "SUPER_ADMIN"
    private String fullName;
    private String email;
    private String phone;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;  // for admins (which dept they belong to)
}