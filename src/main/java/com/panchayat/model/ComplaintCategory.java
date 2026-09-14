package com.panchayat.model;

/**
 * Category a complaint gets auto-classified into after voice
 * transcription. See service.impl.ComplaintClassifierServiceImpl for the
 * classification logic.
 */
public enum ComplaintCategory {
    PLUMBING,
    ELECTRICAL,
    CLEANING_SANITATION,
    SECURITY,
    NOISE,
    PARKING,
    LIFT_ELEVATOR,
    GARDEN_MAINTENANCE,
    OTHER
}
