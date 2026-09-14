package com.panchayat.repository;

import com.panchayat.model.Role;
import com.panchayat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findByRoleAndApprovedOrderByCreatedAtDesc(Role role, boolean approved);
    List<User> findByRoleOrderByCreatedAtDesc(Role role);

    long countByRole(Role role);
    long countByRoleAndApproved(Role role, boolean approved);
}
