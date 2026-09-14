package com.panchayat.service;

import com.panchayat.model.Role;
import com.panchayat.model.User;
import com.panchayat.repository.UserRepository;
import com.panchayat.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, "PANCHAYAT2026");
        lenient().when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
    }

    @Test
    void registerResidentSetsRoleAndApprovedTrue() {
        User resident = new User();
        resident.setFullName("John Doe");
        resident.setEmail("john@example.com");
        resident.setPassword("rawPassword");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.register(resident);

        assertEquals(Role.RESIDENT, saved.getRole());
        assertTrue(saved.isApproved());
        assertEquals("encodedPassword", saved.getPassword());
    }

    @Test
    void registerAdminWithoutMasterCodeSetsApprovedFalse() {
        User admin = new User();
        admin.setFullName("Jane Admin");
        admin.setEmail("jane@panchayat.local");
        admin.setPassword("rawPassword");

        when(userRepository.existsByEmail("jane@panchayat.local")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.registerAdmin(admin, "Treasurer", null);

        assertEquals(Role.ADMIN, saved.getRole());
        assertEquals("Treasurer", saved.getCommitteeRole());
        assertFalse(saved.isApproved());
        assertEquals("encodedPassword", saved.getPassword());
    }

    @Test
    void registerAdminWithValidMasterCodeSetsApprovedTrue() {
        User admin = new User();
        admin.setFullName("President Sharma");
        admin.setEmail("president@panchayat.local");
        admin.setPassword("rawPassword");

        when(userRepository.existsByEmail("president@panchayat.local")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.registerAdmin(admin, "Society President", "PANCHAYAT2026");

        assertEquals(Role.ADMIN, saved.getRole());
        assertEquals("Society President", saved.getCommitteeRole());
        assertTrue(saved.isApproved());
        assertEquals("encodedPassword", saved.getPassword());
    }

    @Test
    void approveAdminSetsApprovedTrue() {
        User pendingAdmin = new User();
        pendingAdmin.setId(10L);
        pendingAdmin.setEmail("pending@panchayat.local");
        pendingAdmin.setApproved(false);

        when(userRepository.findById(10L)).thenReturn(Optional.of(pendingAdmin));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.approveAdmin(10L);

        assertTrue(pendingAdmin.isApproved());
        verify(userRepository, times(1)).save(pendingAdmin);
    }

    @Test
    void registerThrowsExceptionWhenEmailAlreadyExists() {
        User user = new User();
        user.setEmail("existing@example.com");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.register(user));
        assertThrows(IllegalArgumentException.class, () -> userService.registerAdmin(user, "Secretary"));
    }
}
