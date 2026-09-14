package com.panchayat.repository;

import com.panchayat.model.SocietyProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SocietyProjectRepository extends JpaRepository<SocietyProject, Long> {
    List<SocietyProject> findAllByOrderByStartDateDesc();
}
