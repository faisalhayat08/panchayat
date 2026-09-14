package com.panchayat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Timing info for a shared facility, e.g. Gym, Swimming Pool, Clubhouse. */
@Entity
@Table(name = "facility_timings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityTiming {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String facilityName;

    private String timings; // e.g. "6:00 AM - 10:00 AM, 5:00 PM - 9:00 PM"

    private String notes;   // e.g. "Closed on Mondays for maintenance"
}
