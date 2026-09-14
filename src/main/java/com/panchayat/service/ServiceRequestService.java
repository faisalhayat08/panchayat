package com.panchayat.service;

import com.panchayat.model.ServiceRequest;
import com.panchayat.model.ServiceType;
import com.panchayat.model.User;
import com.panchayat.model.RequestStatus;

import java.time.LocalDate;
import java.util.List;

public interface ServiceRequestService {
    ServiceRequest create(User resident, ServiceType type, String description, LocalDate preferredDate);
    List<ServiceRequest> findForResident(User resident);
    List<ServiceRequest> findAll();
    ServiceRequest updateStatus(Long id, RequestStatus status);
    ServiceRequest updateStatus(Long id, RequestStatus status, String remarks, String assignedTo);
}
