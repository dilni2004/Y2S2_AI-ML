package com.sfseduconnect.repository;

import com.sfseduconnect.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    // Find department by name (exact match)
    Optional<Department> findByName(String name);
    
    // Find departments by name containing (case-insensitive)
    List<Department> findByNameContainingIgnoreCase(String name);
    
    // Custom query to get department with its users (eager loading)
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.users WHERE d.id = :id")
    Optional<Department> findByIdWithUsers(@Param("id") Long id);
    
    // Get all departments with count of tickets (for dashboard)
    @Query("SELECT d, COUNT(t) FROM Department d LEFT JOIN Ticket t ON t.department = d GROUP BY d")
    List<Object[]> findAllWithTicketCount();
    
    // Check if department name already exists (for validation)
    boolean existsByNameIgnoreCase(String name);
    
    // Find departments that have no tickets assigned
    @Query("SELECT d FROM Department d WHERE NOT EXISTS (SELECT t FROM Ticket t WHERE t.department = d)")
    List<Department> findDepartmentsWithNoTickets();
}