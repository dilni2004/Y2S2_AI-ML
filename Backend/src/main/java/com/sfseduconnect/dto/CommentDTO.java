package com.sfseduconnect.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long id;
    private String content;
    private boolean internal;
    private String authorName;
    private LocalDateTime createdAt;
}