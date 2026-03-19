package com.sfseduconnect.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {
    private Long id;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;          // optional: MIME type
    private String uploadedAt;         // optional: upload timestamp
    private String uploadedBy;         // optional: who uploaded (student/admin)
    
    // You can add a constructor with just the essential fields
    public AttachmentDTO(Long id, String fileName, String filePath, Long fileSize) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }
}