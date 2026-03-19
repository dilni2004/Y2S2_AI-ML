package com.sfs.educonnect.repository;

import com.sfs.educonnect.entity.Ticket;
import com.sfs.educonnect.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // Student: find own tickets
    List<Ticket> findByStudentIdNumber(String studentIdNumber);

    // Department admin: find tickets by department (and optionally not pending approval?)
    List<Ticket> findByDepartmentIdAndPendingApprovalFalse(Long departmentId);

    // Super admin: all tickets pending approval
    List<Ticket> findByPendingApprovalTrue();

    // Super admin: all tickets (optionally with department filter)
    List<Ticket> findAllByOrderByCreatedAtDesc();

    // For department reassign: find by id
    Optional<Ticket> findById(Long id);
}