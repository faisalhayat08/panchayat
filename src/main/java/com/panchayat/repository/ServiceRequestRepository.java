package com.panchayat.repository;

import com.panchayat.model.ServiceRequest;
import com.panchayat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByResidentOrderByCreatedAtDesc(User resident);
    List<ServiceRequest> findAllByOrderByCreatedAtDesc();
}
