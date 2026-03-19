package com.sfseduconnect.service;

import com.sfseduconnect.dto.*;
import com.sfseduconnect.entity.*;
import com.sfseduconnect.repository.TicketRepository;
import com.sfseduconnect.repository.CommentRepository;
import com.sfseduconnect.repository.AttachmentRepository;
import com.sfseduconnect.repository.DepartmentRepository;
import com.sfseduconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final AttachmentRepository attachmentRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    // Create ticket (student)
    @Transactional
    public TicketResponseDTO createTicket(TicketCreateDTO dto, Long studentUserId) {
        User student = userRepository.findById(studentUserId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Department dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Ticket ticket = new Ticket();
        ticket.setStudentIdNumber(dto.getStudentIdNumber());
        ticket.setStudentName(dto.getStudentName());
        ticket.setStudentPhone(dto.getStudentPhone());
        ticket.setStudentEmail(dto.getStudentEmail());
        ticket.setInquiryType(dto.getInquiryType());
        ticket.setDescription(dto.getDescription());
        ticket.setDepartment(dept);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPendingApproval(false);

        ticket = ticketRepository.save(ticket);
        return mapToDTO(ticket);
    }

    // Student: view own tickets
    public List<TicketResponseDTO> getStudentTickets(String studentIdNumber) {
        return ticketRepository.findByStudentIdNumber(studentIdNumber)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Student: update ticket with attachments (only when status is AWAITING_INFO)
    @Transactional
    public TicketResponseDTO addAttachments(Long ticketId, List<MultipartFile> files, Long studentUserId) throws IOException {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // security: ensure ticket belongs to this student
        if (!ticket.getStudentIdNumber().equals(userRepository.findById(studentUserId).get().getStudentIdNumber())) {
            throw new RuntimeException("Access denied");
        }

        // only allowed if status is AWAITING_INFO
        if (ticket.getStatus() != TicketStatus.AWAITING_INFO) {
            throw new RuntimeException("Cannot add attachments at current status");
        }

        // save files and create attachment records
        for (MultipartFile file : files) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            Attachment attachment = new Attachment();
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFilePath(path.toString());
            attachment.setFileSize(file.getSize());
            attachment.setTicket(ticket);
            attachmentRepository.save(attachment);
        }

        // Optionally change status back to OPEN or IN_PROGRESS? We'll leave as is, admin may change.
        return mapToDTO(ticket);
    }

    // Student: delete ticket (only if status OPEN or AWAITING_INFO)
    @Transactional
    public void deleteTicket(Long ticketId, Long studentUserId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (!ticket.getStudentIdNumber().equals(userRepository.findById(studentUserId).get().getStudentIdNumber())) {
            throw new RuntimeException("Access denied");
        }

        if (ticket.getStatus() != TicketStatus.OPEN && ticket.getStatus() != TicketStatus.AWAITING_INFO) {
            throw new RuntimeException("Cannot delete ticket at current status");
        }

        ticketRepository.delete(ticket);
    }

    // Admin: get tickets for department (department admin)
    public List<TicketResponseDTO> getDepartmentTickets(Long departmentId) {
        return ticketRepository.findByDepartmentIdAndPendingApprovalFalse(departmentId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Super admin: get all tickets pending approval
    public List<TicketResponseDTO> getPendingApprovalTickets() {
        return ticketRepository.findByPendingApprovalTrue()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Super admin: get all tickets
    public List<TicketResponseDTO> getAllTickets() {
        return ticketRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Admin actions (department admin & super admin)
    @Transactional
    public TicketResponseDTO performAdminAction(AdminActionDTO action, Long adminUserId) {
        Ticket ticket = ticketRepository.findById(action.getTicketId())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // permission checks based on role
        boolean isSuperAdmin = "SUPER_ADMIN".equals(admin.getRole());

        // Department admin: ensure they belong to the ticket's department unless they are reassigning
        if (!isSuperAdmin && !ticket.getDepartment().getId().equals(admin.getDepartment().getId())) {
            // if action is REASSIGN, they might be moving it away, still allowed?
            if (!"REASSIGN".equals(action.getAction())) {
                throw new RuntimeException("You can only act on tickets from your own department");
            }
        }

        // Process action
        switch (action.getAction()) {
            case "UPDATE_STATUS":
                ticket.setStatus(TicketStatus.valueOf(action.getStatus()));
                break;
            case "ADD_COMMENT":
                Comment comment = new Comment();
                comment.setContent(action.getComment());
                comment.setInternal(action.isInternal());
                comment.setAuthor(admin);
                comment.setTicket(ticket);
                commentRepository.save(comment);
                break;
            case "REASSIGN":
                Department newDept = departmentRepository.findById(action.getNewDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found"));
                ticket.setDepartment(newDept);
                // Optionally reset status or keep as is
                break;
            case "SUBMIT_APPROVAL":
                // department admin marks as resolved and submits to super admin
                ticket.setPendingApproval(true);
                ticket.setStatus(TicketStatus.RESOLVED_PENDING_APPROVAL);
                // Optionally add a comment
                if (action.getComment() != null && !action.getComment().isEmpty()) {
                    Comment resolutionComment = new Comment();
                    resolutionComment.setContent(action.getComment());
                    resolutionComment.setInternal(false);
                    resolutionComment.setAuthor(admin);
                    resolutionComment.setTicket(ticket);
                    commentRepository.save(resolutionComment);
                }
                break;
            case "APPROVE":
                // super admin approves
                if (!isSuperAdmin) throw new RuntimeException("Only super admin can approve");
                ticket.setPendingApproval(false);
                ticket.setStatus(TicketStatus.CLOSED);
                // optional comment
                break;
            case "SEND_BACK":
                if (!isSuperAdmin) throw new RuntimeException("Only super admin can send back");
                ticket.setPendingApproval(false);
                ticket.setStatus(TicketStatus.IN_PROGRESS); // or whatever
                // optional comment
                break;
            default:
                throw new RuntimeException("Unknown action");
        }

        ticket = ticketRepository.save(ticket);
        return mapToDTO(ticket);
    }

    // Helper: map Ticket to TicketResponseDTO
    private TicketResponseDTO mapToDTO(Ticket ticket) {
        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setId(ticket.getId());
        dto.setStudentIdNumber(ticket.getStudentIdNumber());
        dto.setStudentName(ticket.getStudentName());
        dto.setStudentPhone(ticket.getStudentPhone());
        dto.setStudentEmail(ticket.getStudentEmail());
        dto.setInquiryType(ticket.getInquiryType());
        dto.setDepartmentName(ticket.getDepartment().getName());
        dto.setDepartmentId(ticket.getDepartment().getId());
        dto.setDescription(ticket.getDescription());
        dto.setStatus(ticket.getStatus());
        dto.setPendingApproval(ticket.isPendingApproval());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());

        // Map comments
        List<CommentDTO> commentDTOs = ticket.getComments().stream()
                .map(c -> {
                    CommentDTO cdto = new CommentDTO();
                    cdto.setId(c.getId());
                    cdto.setContent(c.getContent());
                    cdto.setInternal(c.isInternal());
                    cdto.setAuthorName(c.getAuthor().getFullName());
                    cdto.setCreatedAt(c.getCreatedAt());
                    return cdto;
                })
                .collect(Collectors.toList());
        dto.setComments(commentDTOs);

        // Map attachments (simplified)
        List<AttachmentDTO> attachmentDTOs = ticket.getAttachments().stream()
                .map(a -> {
                    AttachmentDTO adto = new AttachmentDTO();
                    adto.setId(a.getId());
                    adto.setFileName(a.getFileName());
                    adto.setFileSize(a.getFileSize());
                    return adto;
                })
                .collect(Collectors.toList());
        dto.setAttachments(attachmentDTOs);

        return dto;
    }

    private AttachmentDTO mapToDTO(Attachment attachment) {
    AttachmentDTO dto = new AttachmentDTO();
    dto.setId(attachment.getId());
    dto.setFileName(attachment.getFileName());
    dto.setFilePath(attachment.getFilePath());
    dto.setFileSize(attachment.getFileSize());
    if (attachment.getUploadedAt() != null) {
        dto.setUploadedAt(attachment.getUploadedAt().toString());
    }
    if (attachment.getUploadedBy() != null) {
        dto.setUploadedBy(attachment.getUploadedBy().getFullName());
    }
    return dto;
}
}