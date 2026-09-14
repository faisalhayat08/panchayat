package com.panchayat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A voice complaint raised by a resident.
 * Flow: resident presses the record button -> audio saved -> transcribed ->
 * translated to English -> auto-classified into a category -> lands in the
 * Admin Inbox as a new Complaint row.
 */
@Entity
@Table(name = "complaints")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    private User resident;

    /** Relative path to the saved audio file, if voice was used. */
    private String audioFilePath;

    /** Raw speech-to-text output, in whatever language the resident spoke. */
    @Column(length = 3000)
    private String transcribedText;

    /** Detected spoken language, e.g. "Hindi", "English". */
    private String detectedLanguage;

    /** English translation used internally for classification & the admin inbox. */
    @Column(length = 3000)
    private String translatedText;

    @Enumerated(EnumType.STRING)
    private ComplaintCategory category = ComplaintCategory.OTHER;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status = ComplaintStatus.NEW;

    @Column(length = 2000)
    private String adminRemarks;

    /** Assigned committee member or staff for handling the complaint */
    private String assignedTo;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime resolvedAt;
}
