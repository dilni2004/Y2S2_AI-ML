package com.sfseduconnect.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class TicketUpdateDTO {
    // optional new description, but mostly just for attachments
    private List<MultipartFile> attachments;
}