package com.panchayat.controller;

import com.panchayat.model.User;
import com.panchayat.repository.NoticeRepository;
import com.panchayat.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserService userService;
    private final NoticeRepository noticeRepository;

    public HomeController(UserService userService, NoticeRepository noticeRepository) {
        this.userService = userService;
        this.noticeRepository = noticeRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("recentNotices", noticeRepository.findAllByOrderByPostedAtDesc()
                .stream().limit(3).toList());
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = userService.findByEmail(authentication.getName());
        if (user.getRole().name().equals("ADMIN")) {
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("user", user);
        return "resident-dashboard";
    }
}
