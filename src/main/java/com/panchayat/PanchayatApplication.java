package com.panchayat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Digital Panchayat - Society Management Platform
 * -------------------------------------------------
 * A single web platform for a residential society where residents can:
 *  - Press one button to record a voice complaint. The app transcribes it,
 *    auto-translates it, auto-classifies the category (plumber/electrician/
 *    cleaning/security/other), and drops it straight into the Admin Inbox.
 *  - Raise direct service requests (plumber, cleaning, electrician, etc.)
 *  - View society rules, history, ongoing projects and gym/facility timings.
 *  - View notices and upcoming events.
 *
 * Entry point for the Spring Boot application.
 */
@SpringBootApplication
@EnableAsync
public class PanchayatApplication {
    public static void main(String[] args) {
        SpringApplication.run(PanchayatApplication.class, args);
    }
}







