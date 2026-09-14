package com.panchayat.service;

import com.panchayat.model.Complaint;
import com.panchayat.model.ComplaintStatus;
import com.panchayat.model.User;

import java.util.List;

public interface ComplaintService {

    /**
     * Full pipeline: save audio -> transcribe -> translate -> classify ->
     * persist -> notify admin.
     */
    Complaint fileVoiceComplaint(User resident, byte[] audioBytes, String originalFilename);

    /** For a resident typing instead of speaking. */
    Complaint fileTextComplaint(User resident, String text);

    List<Complaint> findForResident(User resident);

    List<Complaint> findAll();

    Complaint updateStatus(Long complaintId, ComplaintStatus status, String remarks);

    Complaint updateStatus(Long complaintId, ComplaintStatus status, String remarks, String assignedTo);
}
