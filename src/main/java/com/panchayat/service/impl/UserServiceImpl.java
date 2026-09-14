package com.panchayat.service.impl;

import com.panchayat.model.Role;
import com.panchayat.model.User;
import com.panchayat.repository.UserRepository;
import com.panchayat.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String masterCode;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           @org.springframework.beans.factory.annotation.Value("${app.admin.master-code:PANCHAYAT2026}") String masterCode) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.masterCode = masterCode;
    }

    @Override
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        if (user.getRole() == null) {
            user.setRole(Role.RESIDENT);
        }
        user.setApproved(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User registerAdmin(User user, String committeeRole) {
        return registerAdmin(user, committeeRole, null);
    }

    @Override
    public User registerAdmin(User user, String committeeRole, String providedMasterCode) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        user.setRole(Role.ADMIN);
        user.setCommitteeRole(committeeRole != null && !committeeRole.isBlank() ? committeeRole : "General Committee Member");
        
        // Auto-approve if correct master passcode is supplied by Society Main Head / President
        boolean isValidMasterCode = providedMasterCode != null && !providedMasterCode.isBlank()
                && providedMasterCode.trim().equals(masterCode != null ? masterCode.trim() : "PANCHAYAT2026");
        user.setApproved(isValidMasterCode);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No user found with email: " + email));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No user found with ID: " + id));
    }

    @Override
    public java.util.List<User> findPendingAdmins() {
        return userRepository.findByRoleAndApprovedOrderByCreatedAtDesc(Role.ADMIN, false);
    }

    @Override
    public java.util.List<User> findApprovedAdmins() {
        return userRepository.findByRoleAndApprovedOrderByCreatedAtDesc(Role.ADMIN, true);
    }

    @Override
    public java.util.List<User> findAllResidents() {
        return userRepository.findByRoleOrderByCreatedAtDesc(Role.RESIDENT);
    }

    @Override
    public void approveAdmin(Long userId) {
        User user = findById(userId);
        user.setApproved(true);
        userRepository.save(user);
    }

    @Override
    public void rejectAdmin(Long userId) {
        User user = findById(userId);
        userRepository.delete(user);
    }
}
