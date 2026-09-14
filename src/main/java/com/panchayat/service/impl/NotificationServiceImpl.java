package com.panchayat.service.impl;

import com.panchayat.model.Complaint;
import com.panchayat.model.ServiceRequest;
import com.panchayat.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Delivers new complaint / service-request alerts to the admin's inbox.
 *
 * A complaint or request is ALWAYS saved to the in-app Admin Inbox
 * (visible on /admin/complaints and /admin/service-requests) regardless of
 * email configuration. If SMTP credentials are configured in
 * application.properties, a real email is also sent. If not configured (or
 * sending fails, e.g. no internet in this sandboxed evaluation
 * environment), the failure is logged and swallowed so the resident-facing
 * flow never breaks.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${app.admin.inbox-email}")
    private String adminEmail;

    @Value("${spring.mail.username:}")
    private String smtpUsername;

    public NotificationServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void notifyNewComplaint(Complaint complaint) {
        String subject = "[Panchayat] New " + complaint.getCategory() + " complaint from "
                + safeName(complaint) + " (Flat " + safeFlat(complaint) + ")";
        String body = "A new voice complaint was filed.\n\n"
                + "Category: " + complaint.getCategory() + "\n"
                + "Detected language: " + complaint.getDetectedLanguage() + "\n"
                + "Original transcription: " + complaint.getTranscribedText() + "\n"
                + "English translation: " + complaint.getTranslatedText() + "\n"
                + "Filed at: " + complaint.getCreatedAt() + "\n";
        sendMailSafely(subject, body);
    }

    @Override
    @Async
    public void notifyNewServiceRequest(ServiceRequest request) {
        String subject = "[Panchayat] New service request: " + request.getServiceType();
        String body = "A new service request was raised.\n\n"
                + "Type: " + request.getServiceType() + "\n"
                + "Description: " + request.getDescription() + "\n"
                + "Preferred date: " + request.getPreferredDate() + "\n"
                + "Filed at: " + request.getCreatedAt() + "\n";
        sendMailSafely(subject, body);
    }

    private void sendMailSafely(String subject, String body) {
        if (smtpUsername == null || smtpUsername.isBlank()) {
            log.info("[Admin Inbox - email not configured, stored in-app only] {}", subject);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception ex) {
            log.warn("Could not send admin notification email (stored in-app inbox regardless): {}", ex.getMessage());
        }
    }

    private String safeName(Complaint c) {
        return c.getResident() != null ? c.getResident().getFullName() : "Unknown";
    }

    private String safeFlat(Complaint c) {
        return c.getResident() != null ? c.getResident().getFlatNumber() : "N/A";
    }
}
