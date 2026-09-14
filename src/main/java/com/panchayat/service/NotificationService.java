package com.panchayat.service;

import com.panchayat.model.Complaint;
import com.panchayat.model.ServiceRequest;

/** Sends new-complaint / new-service-request alerts to the Admin Inbox (email + in-app). */
public interface NotificationService {
    void notifyNewComplaint(Complaint complaint);
    void notifyNewServiceRequest(ServiceRequest request);
}
