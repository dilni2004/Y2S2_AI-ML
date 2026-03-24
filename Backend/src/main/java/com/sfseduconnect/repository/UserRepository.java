package com.sfseduconnect.repository;

import com.sfseduconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    static Optional<User> findByUsername(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUsername'");
    }

    Optional<User> findByStudentIdNumber(String studentIdNumber);
}