package com.sfseduconnect.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    private String password;
    
    @Column(nullable = false)
    private String role;          // "STUDENT", "DEPT_ADMIN", "SUPER_ADMIN"
    
    private String fullName;
    private String email;
    private String phone;
    
    @Column(name = "student_id_number", unique = true)
    private String studentIdNumber;  // Only for students
    
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;    // for admins (which dept they belong to)
    
    // Helper method to check if user is a student
    public boolean isStudent() {
        return "STUDENT".equals(this.role);
    }
    
    // Helper method to check if user is an admin
    public boolean isAdmin() {
        return "DEPT_ADMIN".equals(this.role) || "SUPER_ADMIN".equals(this.role);
    }
}