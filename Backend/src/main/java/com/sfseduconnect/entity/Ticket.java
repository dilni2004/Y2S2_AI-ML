package com.sfs.educonnect.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tickets")
@Data
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Student details (denormalized for quick access)
    private String studentIdNumber;      // from User
    private String studentName;
    private String studentPhone;
    private String studentEmail;

    private String inquiryType;           // e.g., ACADEMIC, TECHNICAL, ADMIN
    private String description;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;        // current department

    @ManyToOne
    @JoinColumn(name = "assigned_admin_id")
    private User assignedAdmin;            // optional, if an admin picks it

    // For final approval: when department admin submits to super admin
    private boolean pendingApproval = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();
}