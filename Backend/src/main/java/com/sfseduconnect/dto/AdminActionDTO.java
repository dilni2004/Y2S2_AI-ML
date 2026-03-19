package com.sfseduconnect.dto;

import lombok.Data;

@Data
public class AdminActionDTO {
    private Long ticketId;
    private String action;          // "UPDATE_STATUS", "ADD_COMMENT", "REASSIGN", "SUBMIT_APPROVAL", "APPROVE", "SEND_BACK"
    private String status;           // if action UPDATE_STATUS
    private String comment;          // if ADD_COMMENT or any action with comment
    private boolean internal;        // if comment is internal
    private Long newDepartmentId;    // if REASSIGN
}