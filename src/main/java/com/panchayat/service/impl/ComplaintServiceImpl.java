package com.panchayat.service.impl;

import com.panchayat.model.Complaint;
import com.panchayat.model.ComplaintStatus;
import com.panchayat.model.User;
import com.panchayat.repository.ComplaintRepository;
import com.panchayat.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Orchestrates the full "one button" voice-complaint pipeline:
 * save audio -> AI transcribe -> AI translate -> AI classify -> persist ->
 * notify admin inbox.
 */
@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final VoiceTranscriptionService transcriptionService;
    private final TranslationService translationService;
    private final ComplaintClassifierService classifierService;
    private final NotificationService notificationService;

    @Value("${app.upload.voice-dir}")
    private String voiceUploadDir;

    public ComplaintServiceImpl(ComplaintRepository complaintRepository,
                                 VoiceTranscriptionService transcriptionService,
                                 TranslationService translationService,
                                 ComplaintClassifierService classifierService,
                                 NotificationService notificationService) {
        this.complaintRepository = complaintRepository;
        this.transcriptionService = transcriptionService;
        this.translationService = translationService;
        this.classifierService = classifierService;
        this.notificationService = notificationService;
    }

    @Override
    public Complaint fileVoiceComplaint(User resident, byte[] audioBytes, String originalFilename) {
        return fileVoiceComplaint(resident, audioBytes, originalFilename, null, null);
    }

    @Override
    public Complaint fileVoiceComplaint(User resident, byte[] audioBytes, String originalFilename,
                                        String clientTranscript, String clientLanguage) {
        String savedPath = saveAudioFile(audioBytes, originalFilename);

        String text;
        String lang;
        if (clientTranscript != null && !clientTranscript.trim().isEmpty()) {
            text = clientTranscript.trim();
            lang = (clientLanguage != null && !clientLanguage.trim().isEmpty()) ? clientLanguage.trim() : "Hindi / Hinglish";
        } else {
            VoiceTranscriptionService.TranscriptionResult transcription = transcriptionService.transcribe(savedPath);
            text = transcription.text();
            lang = transcription.detectedLanguage();
        }

        String translated = translationService.translateToEnglish(text, lang);

        Complaint complaint = new Complaint();
        complaint.setResident(resident);
        complaint.setAudioFilePath(savedPath);
        complaint.setTranscribedText(text);
        complaint.setDetectedLanguage(lang);
        complaint.setTranslatedText(translated);
        // Classify using both translated text and original text for maximum keyword matching accuracy
        complaint.setCategory(classifierService.classify(translated + " " + text));
        complaint.setStatus(ComplaintStatus.NEW);
        complaint.setCreatedAt(LocalDateTime.now());

        Complaint saved = complaintRepository.save(complaint);
        notificationService.notifyNewComplaint(saved);
        return saved;
    }

    @Override
    public Complaint fileTextComplaint(User resident, String text) {
        String translated = translationService.translateToEnglish(text, "English");

        Complaint complaint = new Complaint();
        complaint.setResident(resident);
        complaint.setTranscribedText(text);
        complaint.setDetectedLanguage("English");
        complaint.setTranslatedText(translated);
        complaint.setCategory(classifierService.classify(translated));
        complaint.setStatus(ComplaintStatus.NEW);
        complaint.setCreatedAt(LocalDateTime.now());

        Complaint saved = complaintRepository.save(complaint);
        notificationService.notifyNewComplaint(saved);
        return saved;
    }

    @Override
    public List<Complaint> findForResident(User resident) {
        return complaintRepository.findByResidentOrderByCreatedAtDesc(resident);
    }

    @Override
    public List<Complaint> findAll() {
        return complaintRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public Complaint updateStatus(Long complaintId, ComplaintStatus status, String remarks) {
        return updateStatus(complaintId, status, remarks, null);
    }

    @Override
    public Complaint updateStatus(Long complaintId, ComplaintStatus status, String remarks, String assignedTo) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found: " + complaintId));
        complaint.setStatus(status);
        if (remarks != null) {
            complaint.setAdminRemarks(remarks);
        }
        if (assignedTo != null && !assignedTo.isBlank()) {
            complaint.setAssignedTo(assignedTo);
        }
        if (status == ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(LocalDateTime.now());
        }
        return complaintRepository.save(complaint);
    }

    private String saveAudioFile(byte[] audioBytes, String originalFilename) {
        try {
            Path dir = Paths.get(voiceUploadDir);
            Files.createDirectories(dir);
            String ext = ".webm";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now());
            String filename = timestamp + "-" + UUID.randomUUID().toString().substring(0, 8) + ext;
            Path filePath = dir.resolve(filename);
            Files.write(filePath, audioBytes);
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Could not save voice recording", e);
        }
    }
}
