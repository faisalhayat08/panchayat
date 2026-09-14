package com.panchayat.repository;

import com.panchayat.model.Complaint;
import com.panchayat.model.ComplaintStatus;
import com.panchayat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByResidentOrderByCreatedAtDesc(User resident);
    List<Complaint> findAllByOrderByCreatedAtDesc();
    List<Complaint> findByStatusOrderByCreatedAtDesc(ComplaintStatus status);
}
