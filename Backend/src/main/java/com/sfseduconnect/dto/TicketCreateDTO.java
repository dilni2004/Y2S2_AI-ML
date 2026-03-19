package com.sfseduconnect.dto;

import lombok.Data;

@Data
public class TicketCreateDTO {
    private String studentIdNumber;
    private String studentName;
    private String studentPhone;
    private String studentEmail;
    private String inquiryType;
    private Long departmentId;      // selected department
    private String description;
}