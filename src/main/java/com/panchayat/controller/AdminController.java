package com.panchayat.controller;

import com.panchayat.model.*;
import com.panchayat.repository.EventRepository;
import com.panchayat.repository.NoticeRepository;
import com.panchayat.service.ComplaintService;
import com.panchayat.service.ServiceRequestService;
import com.panchayat.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/** Admin/committee area: the unified inbox for complaints + service requests, admin approval, and content management. */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ComplaintService complaintService;
    private final ServiceRequestService serviceRequestService;
    private final NoticeRepository noticeRepository;
    private final EventRepository eventRepository;
    private final UserService userService;

    public AdminController(ComplaintService complaintService, ServiceRequestService serviceRequestService,
                            NoticeRepository noticeRepository, EventRepository eventRepository,
                            UserService userService) {
        this.complaintService = complaintService;
        this.serviceRequestService = serviceRequestService;
        this.noticeRepository = noticeRepository;
        this.eventRepository = eventRepository;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var complaints = complaintService.findAll();
        var requests = serviceRequestService.findAll();
        var residents = userService.findAllResidents();
        var approvedAdmins = userService.findApprovedAdmins();
        var pendingAdmins = userService.findPendingAdmins();

        model.addAttribute("complaints", complaints);
        model.addAttribute("requests", requests);
        model.addAttribute("residents", residents);
        model.addAttribute("approvedAdmins", approvedAdmins);
        model.addAttribute("pendingAdmins", pendingAdmins);

        // Complaint stats
        long newComplaints = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.NEW).count();
        long ackComplaints = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.ACKNOWLEDGED).count();
        long inProgComplaints = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.IN_PROGRESS).count();
        long resolvedComplaints = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.RESOLVED).count();
        long rejectedComplaints = complaints.stream().filter(c -> c.getStatus() == ComplaintStatus.REJECTED).count();

        // Service request stats
        long pendingRequests = requests.stream().filter(r -> r.getStatus() == RequestStatus.PENDING).count();
        long assignedRequests = requests.stream().filter(r -> r.getStatus() == RequestStatus.ASSIGNED).count();
        long completedRequests = requests.stream().filter(r -> r.getStatus() == RequestStatus.COMPLETED).count();
        long cancelledRequests = requests.stream().filter(r -> r.getStatus() == RequestStatus.CANCELLED).count();

        model.addAttribute("newComplaintCount", newComplaints);
        model.addAttribute("ackComplaintCount", ackComplaints);
        model.addAttribute("inProgComplaintCount", inProgComplaints);
        model.addAttribute("resolvedComplaintCount", resolvedComplaints);
        model.addAttribute("rejectedComplaintCount", rejectedComplaints);
        model.addAttribute("pendingRequestCount", pendingRequests);
        model.addAttribute("assignedRequestCount", assignedRequests);
        model.addAttribute("completedRequestCount", completedRequests);
        model.addAttribute("cancelledRequestCount", cancelledRequests);

        // Attention needed: open complaints and open requests
        var urgentComplaints = complaints.stream()
                .filter(c -> c.getStatus() == ComplaintStatus.NEW || c.getStatus() == ComplaintStatus.ACKNOWLEDGED || c.getStatus() == ComplaintStatus.IN_PROGRESS)
                .limit(5)
                .toList();
        var urgentRequests = requests.stream()
                .filter(r -> r.getStatus() == RequestStatus.PENDING || r.getStatus() == RequestStatus.ASSIGNED)
                .limit(5)
                .toList();

        model.addAttribute("urgentComplaints", urgentComplaints);
        model.addAttribute("urgentRequests", urgentRequests);

        return "admin/dashboard";
    }

    // --- Admin Management & Approval ---

    @GetMapping("/manage-admins")
    public String manageAdmins(Model model) {
        model.addAttribute("pendingAdmins", userService.findPendingAdmins());
        model.addAttribute("approvedAdmins", userService.findApprovedAdmins());
        return "admin/manage-admins";
    }

    @PostMapping("/manage-admins/{id}/approve")
    public String approveAdmin(@PathVariable Long id) {
        userService.approveAdmin(id);
        return "redirect:/admin/manage-admins?approved=true";
    }

    @PostMapping("/manage-admins/{id}/reject")
    public String rejectAdmin(@PathVariable Long id) {
        userService.rejectAdmin(id);
        return "redirect:/admin/manage-admins?rejected=true";
    }

    // --- Complaints Management ---

    @GetMapping("/complaints")
    public String complaints(Model model) {
        model.addAttribute("complaints", complaintService.findAll());
        model.addAttribute("statuses", ComplaintStatus.values());
        model.addAttribute("admins", userService.findApprovedAdmins());
        return "admin/complaints";
    }

    @PostMapping("/complaints/{id}/status")
    public String updateComplaintStatus(@PathVariable Long id,
                                         @RequestParam ComplaintStatus status,
                                         @RequestParam(required = false) String remarks,
                                         @RequestParam(required = false) String assignedTo) {
        complaintService.updateStatus(id, status, remarks, assignedTo);
        return "redirect:/admin/complaints";
    }

    // --- Service Requests Management ---

    @GetMapping("/service-requests")
    public String serviceRequests(Model model) {
        model.addAttribute("requests", serviceRequestService.findAll());
        model.addAttribute("statuses", RequestStatus.values());
        model.addAttribute("admins", userService.findApprovedAdmins());
        return "admin/service-requests";
    }

    @PostMapping("/service-requests/{id}/status")
    public String updateRequestStatus(@PathVariable Long id,
                                      @RequestParam RequestStatus status,
                                      @RequestParam(required = false) String remarks,
                                      @RequestParam(required = false) String assignedTo) {
        serviceRequestService.updateStatus(id, status, remarks, assignedTo);
        return "redirect:/admin/service-requests";
    }

    // --- Notices CRUD ---

    @GetMapping("/notices")
    public String manageNotices(Model model) {
        model.addAttribute("notices", noticeRepository.findAllByOrderByPostedAtDesc());
        model.addAttribute("notice", new Notice());
        return "admin/notices";
    }

    @PostMapping("/notices")
    public String postNotice(@RequestParam String title,
                             @RequestParam String content,
                             @RequestParam(required = false, defaultValue = "Society Committee") String postedBy,
                             @RequestParam(defaultValue = "false") boolean important) {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setImportant(important);
        notice.setPostedBy(postedBy != null && !postedBy.isBlank() ? postedBy : "Society Committee");
        noticeRepository.save(notice);
        return "redirect:/admin/notices";
    }

    @GetMapping("/notices/edit/{id}")
    public String editNoticeForm(@PathVariable Long id, Model model) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found: " + id));
        model.addAttribute("notice", notice);
        return "admin/edit-notice";
    }

    @PostMapping("/notices/edit/{id}")
    public String updateNotice(@PathVariable Long id,
                               @RequestParam String title,
                               @RequestParam String content,
                               @RequestParam(required = false) String postedBy,
                               @RequestParam(defaultValue = "false") boolean important) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found: " + id));
        notice.setTitle(title);
        notice.setContent(content);
        notice.setImportant(important);
        if (postedBy != null && !postedBy.isBlank()) {
            notice.setPostedBy(postedBy);
        }
        noticeRepository.save(notice);
        return "redirect:/admin/notices?updated=true";
    }

    @PostMapping("/notices/delete/{id}")
    public String deleteNotice(@PathVariable Long id) {
        noticeRepository.deleteById(id);
        return "redirect:/admin/notices?deleted=true";
    }

    // --- Events CRUD ---

    @GetMapping("/events")
    public String manageEvents(Model model) {
        model.addAttribute("events", eventRepository.findAllByOrderByEventDateTimeAsc());
        return "admin/events";
    }

    @PostMapping("/events")
    public String createEvent(@RequestParam String title,
                              @RequestParam String description,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventDateTime,
                              @RequestParam String location,
                              @RequestParam String organizer) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setEventDateTime(eventDateTime);
        event.setLocation(location);
        event.setOrganizer(organizer);
        eventRepository.save(event);
        return "redirect:/admin/events";
    }

    @GetMapping("/events/edit/{id}")
    public String editEventForm(@PathVariable Long id, Model model) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + id));
        model.addAttribute("event", event);
        return "admin/edit-event";
    }

    @PostMapping("/events/edit/{id}")
    public String updateEvent(@PathVariable Long id,
                              @RequestParam String title,
                              @RequestParam String description,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventDateTime,
                              @RequestParam String location,
                              @RequestParam String organizer) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + id));
        event.setTitle(title);
        event.setDescription(description);
        event.setEventDateTime(eventDateTime);
        event.setLocation(location);
        event.setOrganizer(organizer);
        eventRepository.save(event);
        return "redirect:/admin/events?updated=true";
    }

    @PostMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventRepository.deleteById(id);
        return "redirect:/admin/events?deleted=true";
    }
}
