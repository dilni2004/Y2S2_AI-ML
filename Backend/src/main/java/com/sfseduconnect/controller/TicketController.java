package com.sfseduconnect.controller;

import com.sfseduconnect.dto.*;
import com.sfseduconnect.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // Student creates ticket
    @PostMapping
    public ResponseEntity<TicketResponseDTO> createTicket(@RequestBody TicketCreateDTO dto,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getCurrentUserId(userDetails); // you need to implement extraction
        return ResponseEntity.ok(ticketService.createTicket(dto, userId));
    }

    // Student: get my tickets
    @GetMapping("/my")
    public ResponseEntity<List<TicketResponseDTO>> getMyTickets(@AuthenticationPrincipal UserDetails userDetails) {
        String studentIdNumber = getCurrentUserStudentId(userDetails); // fetch from user details
        return ResponseEntity.ok(ticketService.getStudentTickets(studentIdNumber));
    }

    // Student: add attachments to ticket (update)
    @PostMapping("/{ticketId}/attachments")
    public ResponseEntity<TicketResponseDTO> addAttachments(@PathVariable Long ticketId,
                                                             @RequestParam("files") List<MultipartFile> files,
                                                             @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Long userId = getCurrentUserId(userDetails);
        return ResponseEntity.ok(ticketService.addAttachments(ticketId, files, userId));
    }

    // Student: delete ticket
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long ticketId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getCurrentUserId(userDetails);
        ticketService.deleteTicket(ticketId, userId);
        return ResponseEntity.noContent().build();
    }

    // Department admin: get tickets for their department
    @GetMapping("/department")
    public ResponseEntity<List<TicketResponseDTO>> getDepartmentTickets(@AuthenticationPrincipal UserDetails userDetails) {
        Long deptId = getCurrentUserDepartmentId(userDetails);
        return ResponseEntity.ok(ticketService.getDepartmentTickets(deptId));
    }

    // Super admin: get all tickets
    @GetMapping("/all")
    public ResponseEntity<List<TicketResponseDTO>> getAllTickets(@AuthenticationPrincipal UserDetails userDetails) {
        // ensure role is super admin – handled by security
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    // Super admin: get pending approval tickets
    @GetMapping("/pending-approval")
    public ResponseEntity<List<TicketResponseDTO>> getPendingApproval(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ticketService.getPendingApprovalTickets());
    }

    // Admin action endpoint (both dept admin and super admin)
    @PostMapping("/action")
    public ResponseEntity<TicketResponseDTO> performAction(@RequestBody AdminActionDTO action,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        Long adminId = getCurrentUserId(userDetails);
        return ResponseEntity.ok(ticketService.performAdminAction(action, adminId));
    }

    // Helper methods to extract info from UserDetails – implement according to your security setup
    private Long getCurrentUserId(UserDetails userDetails) {
        // fetch from your user service
        return 1L; // placeholder
    }

    private String getCurrentUserStudentId(UserDetails userDetails) {
        return "STU001"; // placeholder
    }

    private Long getCurrentUserDepartmentId(UserDetails userDetails) {
        return 1L; // placeholder
    }
}