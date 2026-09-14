package com.panchayat.controller;

import com.panchayat.model.ServiceType;
import com.panchayat.model.User;
import com.panchayat.service.ServiceRequestService;
import com.panchayat.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/services")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;
    private final UserService userService;

    public ServiceRequestController(ServiceRequestService serviceRequestService, UserService userService) {
        this.serviceRequestService = serviceRequestService;
        this.userService = userService;
    }

    @GetMapping
    public String myRequests(Authentication authentication, Model model) {
        User resident = userService.findByEmail(authentication.getName());
        model.addAttribute("requests", serviceRequestService.findForResident(resident));
        model.addAttribute("serviceTypes", ServiceType.values());
        return "services";
    }

    @PostMapping
    public String create(@RequestParam ServiceType serviceType,
                          @RequestParam String description,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate preferredDate,
                          Authentication authentication) {
        User resident = userService.findByEmail(authentication.getName());
        serviceRequestService.create(resident, serviceType, description, preferredDate);
        return "redirect:/services";
    }
}
