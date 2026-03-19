package com.sfseduconnect.entity;

public enum TicketStatus {
    OPEN,
    AWAITING_INFO,          // waiting for student's additional details
    IN_PROGRESS,
    RESOLVED_PENDING_APPROVAL,  // department admin marks as solved, awaiting super admin
    CLOSED,                  // super admin approves
    REJECTED                 // super admin sends back
}