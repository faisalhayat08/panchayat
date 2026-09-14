package com.panchayat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** An infrastructure/development project of the society, e.g. "New Clubhouse". */
@Entity
@Table(name = "society_projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocietyProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status = ProjectStatus.UPCOMING;

    private LocalDate startDate;

    private LocalDate expectedCompletionDate;
}
