package com.panchayat.service.impl;

import com.panchayat.model.*;
import com.panchayat.repository.ServiceRequestRepository;
import com.panchayat.service.NotificationService;
import com.panchayat.service.ServiceRequestService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceRequestServiceImpl implements ServiceRequestService {

    private final ServiceRequestRepository repository;
    private final NotificationService notificationService;

    public ServiceRequestServiceImpl(ServiceRequestRepository repository, NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    @Override
    public ServiceRequest create(User resident, ServiceType type, String description, LocalDate preferredDate) {
        ServiceRequest request = new ServiceRequest();
        request.setResident(resident);
        request.setServiceType(type);
        request.setDescription(description);
        request.setPreferredDate(preferredDate);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        ServiceRequest saved = repository.save(request);
        notificationService.notifyNewServiceRequest(saved);
        return saved;
    }

    @Override
    public List<ServiceRequest> findForResident(User resident) {
        return repository.findByResidentOrderByCreatedAtDesc(resident);
    }

    @Override
    public List<ServiceRequest> findAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public ServiceRequest updateStatus(Long id, RequestStatus status) {
        return updateStatus(id, status, null, null);
    }

    @Override
    public ServiceRequest updateStatus(Long id, RequestStatus status, String remarks, String assignedTo) {
        ServiceRequest request = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service request not found: " + id));
        request.setStatus(status);
        if (remarks != null) {
            request.setAdminRemarks(remarks);
        }
        if (assignedTo != null && !assignedTo.isBlank()) {
            request.setAssignedTo(assignedTo);
        }
        return repository.save(request);
    }
}
