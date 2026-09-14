package com.panchayat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** A direct service booking, e.g. "I need a plumber tomorrow morning". */
@Entity
@Table(name = "service_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    private User resident;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Column(length = 1000)
    private String description;

    private LocalDate preferredDate;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    /** Assigned committee member or service technician */
    private String assignedTo;

    @Column(length = 1000)
    private String adminRemarks;

    private LocalDateTime createdAt = LocalDateTime.now();
}
