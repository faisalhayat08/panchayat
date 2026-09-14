package com.panchayat.service;

import com.panchayat.model.User;

import java.util.List;

public interface UserService {
    User register(User user);
    User registerAdmin(User user, String committeeRole);
    User registerAdmin(User user, String committeeRole, String masterCode);
    User findByEmail(String email);
    User findById(Long id);
    List<User> findPendingAdmins();
    List<User> findApprovedAdmins();
    List<User> findAllResidents();
    void approveAdmin(Long userId);
    void rejectAdmin(Long userId);
}
