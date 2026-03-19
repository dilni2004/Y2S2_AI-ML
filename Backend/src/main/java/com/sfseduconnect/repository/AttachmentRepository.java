package com.sfseduconnect.repository;

import com.sfseduconnect.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    
    // Find all attachments for a specific ticket
    List<Attachment> findByTicketId(Long ticketId);
    
    // Find attachments by file name (for search functionality)
    List<Attachment> findByFileNameContainingIgnoreCase(String fileName);
    
    // Delete all attachments for a ticket (used when deleting a ticket)
    void deleteByTicketId(Long ticketId);
    
    // Count attachments per ticket (for dashboard metrics)
    Long countByTicketId(Long ticketId);
}