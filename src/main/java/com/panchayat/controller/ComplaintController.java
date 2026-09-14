package com.panchayat.controller;

import com.panchayat.model.User;
import com.panchayat.service.ComplaintService;
import com.panchayat.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final UserService userService;

    public ComplaintController(ComplaintService complaintService, UserService userService) {
        this.complaintService = complaintService;
        this.userService = userService;
    }

    /** Resident's complaint page: the "one button" voice recorder + history list. */
    @GetMapping
    public String myComplaints(Authentication authentication, Model model) {
        User resident = userService.findByEmail(authentication.getName());
        model.addAttribute("complaints", complaintService.findForResident(resident));
        return "complaints";
    }

    /**
     * Receives the recorded audio blob from the browser (MediaRecorder API),
     * runs it through the AI pipeline (transcribe -> translate -> classify),
     * and redirects back with the result.
     */
    @PostMapping("/voice")
    public String submitVoiceComplaint(@RequestParam("audio") MultipartFile audio,
                                        Authentication authentication,
                                        Model model) throws IOException {
        User resident = userService.findByEmail(authentication.getName());
        var complaint = complaintService.fileVoiceComplaint(resident, audio.getBytes(), audio.getOriginalFilename());
        model.addAttribute("justFiled", complaint);
        model.addAttribute("complaints", complaintService.findForResident(resident));
        return "complaints";
    }

    /** Fallback for residents who prefer to type instead of speaking. */
    @PostMapping("/text")
    public String submitTextComplaint(@RequestParam("text") String text, Authentication authentication) {
        User resident = userService.findByEmail(authentication.getName());
        complaintService.fileTextComplaint(resident, text);
        return "redirect:/complaints";
    }
}
