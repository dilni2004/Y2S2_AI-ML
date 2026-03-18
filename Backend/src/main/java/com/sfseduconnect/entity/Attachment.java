package com.sfs.educonnect.entity;

import jakarta.persistence.*;
import lombok.Data;

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
}