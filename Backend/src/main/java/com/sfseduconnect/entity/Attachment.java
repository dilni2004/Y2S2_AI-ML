package com.sfseduconnect.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "attachments")
@Data
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String filePath;        // or cloud URL
    private Long fileSize;


    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;          // who uploaded
    
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
}