package com.panchayat.controller;

import com.panchayat.model.User;
import com.panchayat.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.register(user);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
        model.addAttribute("registered", true);
        return "login";
    }

    @GetMapping("/register-admin")
    public String registerAdminForm(Model model) {
        model.addAttribute("user", new User());
        return "register-admin";
    }

    @PostMapping("/register-admin")
    public String registerAdmin(@Valid @ModelAttribute("user") User user, BindingResult result,
                                @org.springframework.web.bind.annotation.RequestParam(value = "masterCode", required = false) String masterCode,
                                Model model) {
        if (result.hasFieldErrors("fullName") || result.hasFieldErrors("email") || result.hasFieldErrors("password")) {
            return "register-admin";
        }
        try {
            User saved = userService.registerAdmin(user, user.getCommitteeRole(), masterCode);
            if (saved.isApproved()) {
                model.addAttribute("adminApproved", true);
            } else {
                model.addAttribute("adminRegistered", true);
            }
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register-admin";
        }
        return "login";
    }
}
