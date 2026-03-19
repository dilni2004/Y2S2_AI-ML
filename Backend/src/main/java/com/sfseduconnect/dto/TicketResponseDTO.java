package com.sfseduconnect.dto;

import com.sfseduconnect.entity.TicketStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TicketResponseDTO {
    private Long id;
    private String studentIdNumber;
    private String studentName;
    private String studentPhone;
    private String studentEmail;
    private String inquiryType;
    private String departmentName;
    private Long departmentId;
    private String description;
    private TicketStatus status;
    private boolean pendingApproval;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentDTO> comments;
    private List<AttachmentDTO> attachments;
}