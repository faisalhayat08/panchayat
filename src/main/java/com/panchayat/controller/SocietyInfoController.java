package com.panchayat.controller;

import com.panchayat.repository.EventRepository;
import com.panchayat.repository.FacilityTimingRepository;
import com.panchayat.repository.NoticeRepository;
import com.panchayat.repository.SocietyProjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Public pages: society rules/history/projects, facility timings, notices, events. */
@Controller
public class SocietyInfoController {

    private final NoticeRepository noticeRepository;
    private final EventRepository eventRepository;
    private final SocietyProjectRepository projectRepository;
    private final FacilityTimingRepository facilityTimingRepository;

    public SocietyInfoController(NoticeRepository noticeRepository, EventRepository eventRepository,
                                  SocietyProjectRepository projectRepository,
                                  FacilityTimingRepository facilityTimingRepository) {
        this.noticeRepository = noticeRepository;
        this.eventRepository = eventRepository;
        this.projectRepository = projectRepository;
        this.facilityTimingRepository = facilityTimingRepository;
    }

    @GetMapping("/about-society")
    public String aboutSociety(Model model) {
        model.addAttribute("projects", projectRepository.findAllByOrderByStartDateDesc());
        model.addAttribute("facilities", facilityTimingRepository.findAll());
        return "about-society";
    }

    @GetMapping("/notices")
    public String notices(Model model) {
        model.addAttribute("notices", noticeRepository.findAllByOrderByPostedAtDesc());
        return "notices";
    }

    @GetMapping("/events")
    public String events(Model model) {
        model.addAttribute("events", eventRepository.findAllByOrderByEventDateTimeAsc());
        return "events";
    }
}
