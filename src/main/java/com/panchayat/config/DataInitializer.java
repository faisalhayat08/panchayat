package com.panchayat.config;

import com.panchayat.model.*;
import com.panchayat.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Seeds the H2 in-memory database with realistic demo data every time the
 * app starts, so a college evaluator sees a fully populated platform
 * immediately without any manual setup.
 *
 * Demo logins printed to the console on startup:
 *   Admin:    admin@panchayat.local    / admin123
 *   Resident: ravi.sharma@example.com  / resident123
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final EventRepository eventRepository;
    private final SocietyProjectRepository projectRepository;
    private final FacilityTimingRepository facilityTimingRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, NoticeRepository noticeRepository,
                            EventRepository eventRepository, SocietyProjectRepository projectRepository,
                            FacilityTimingRepository facilityTimingRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.noticeRepository = noticeRepository;
        this.eventRepository = eventRepository;
        this.projectRepository = projectRepository;
        this.facilityTimingRepository = facilityTimingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setFullName("Society Admin");
            admin.setEmail("admin@panchayat.local");
            admin.setPhone("9999999999");
            admin.setFlatNumber("Office");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setCommitteeRole("General Admin / Secretary");
            admin.setApproved(true);
            userRepository.save(admin);

            User resident = new User();
            resident.setFullName("Ravi Sharma");
            resident.setEmail("ravi.sharma@example.com");
            resident.setPhone("9876543210");
            resident.setFlatNumber("A-204");
            resident.setPassword(passwordEncoder.encode("resident123"));
            resident.setRole(Role.RESIDENT);
            resident.setApproved(true);
            userRepository.save(resident);
        }

        if (noticeRepository.count() == 0) {
            Notice n1 = new Notice();
            n1.setTitle("Society Rule Book - Key Guidelines");
            n1.setContent("1) Visitors must register at the security gate.\n" +
                    "2) Loud music/parties are not allowed after 10:00 PM.\n" +
                    "3) Two-wheeler and four-wheeler parking is only in allotted slots.\n" +
                    "4) Maintenance charges must be paid by the 10th of every month.\n" +
                    "5) Pets must be leashed in common areas.\n" +
                    "6) Any renovation work must be intimated to the office in advance.");
            n1.setPostedBy("Society Committee");
            n1.setImportant(true);
            noticeRepository.save(n1);

            Notice n2 = new Notice();
            n2.setTitle("Water Supply Maintenance - This Sunday");
            n2.setContent("Water supply will be interrupted from 10 AM to 1 PM this Sunday for overhead tank cleaning.");
            n2.setPostedBy("Society Committee");
            n2.setImportant(true);
            noticeRepository.save(n2);

            Notice n3 = new Notice();
            n3.setTitle("Annual General Meeting (AGM) Announcement");
            n3.setContent("The AGM will be held in the clubhouse. All flat owners are requested to attend.");
            n3.setPostedBy("Society Secretary");
            noticeRepository.save(n3);
        }

        if (eventRepository.count() == 0) {
            Event e1 = new Event();
            e1.setTitle("Independence Day Celebration");
            e1.setDescription("Flag hoisting followed by cultural programs and snacks in the community hall.");
            e1.setEventDateTime(LocalDateTime.of(2026, 8, 15, 8, 0));
            e1.setLocation("Community Hall");
            e1.setOrganizer("Cultural Committee");
            eventRepository.save(e1);

            Event e2 = new Event();
            e2.setTitle("Diwali Mela");
            e2.setDescription("Stalls, rangoli competition, and fireworks display for all residents.");
            e2.setEventDateTime(LocalDateTime.of(2026, 11, 8, 17, 0));
            e2.setLocation("Society Garden");
            e2.setOrganizer("Cultural Committee");
            eventRepository.save(e2);

            Event e3 = new Event();
            e3.setTitle("Free Health Checkup Camp");
            e3.setDescription("General health checkup camp in partnership with a local hospital.");
            e3.setEventDateTime(LocalDateTime.of(2026, 9, 20, 9, 0));
            e3.setLocation("Clubhouse");
            e3.setOrganizer("Welfare Committee");
            eventRepository.save(e3);
        }

        if (projectRepository.count() == 0) {
            SocietyProject p1 = new SocietyProject();
            p1.setTitle("New Clubhouse Construction");
            p1.setDescription("A new 2-storey clubhouse with an indoor games room and banquet hall.");
            p1.setStatus(ProjectStatus.ONGOING);
            p1.setStartDate(LocalDate.of(2026, 3, 1));
            p1.setExpectedCompletionDate(LocalDate.of(2026, 12, 31));
            projectRepository.save(p1);

            SocietyProject p2 = new SocietyProject();
            p2.setTitle("Solar Panel Installation");
            p2.setDescription("Rooftop solar panels for common-area electricity to cut society maintenance costs.");
            p2.setStatus(ProjectStatus.UPCOMING);
            p2.setStartDate(LocalDate.of(2026, 12, 1));
            projectRepository.save(p2);

            SocietyProject p3 = new SocietyProject();
            p3.setTitle("Rainwater Harvesting System");
            p3.setDescription("Installed rainwater harvesting pits across the society premises.");
            p3.setStatus(ProjectStatus.COMPLETED);
            p3.setStartDate(LocalDate.of(2025, 6, 1));
            p3.setExpectedCompletionDate(LocalDate.of(2025, 9, 1));
            projectRepository.save(p3);
        }

        if (facilityTimingRepository.count() == 0) {
            FacilityTiming gym = new FacilityTiming();
            gym.setFacilityName("Gym");
            gym.setTimings("6:00 AM - 10:00 AM, 5:00 PM - 9:00 PM");
            gym.setNotes("Closed on Mondays for maintenance and cleaning.");
            facilityTimingRepository.save(gym);

            FacilityTiming pool = new FacilityTiming();
            pool.setFacilityName("Swimming Pool");
            pool.setTimings("6:00 AM - 9:00 AM, 4:00 PM - 8:00 PM");
            pool.setNotes("Children under 12 must be accompanied by an adult.");
            facilityTimingRepository.save(pool);

            FacilityTiming club = new FacilityTiming();
            club.setFacilityName("Clubhouse");
            club.setTimings("9:00 AM - 10:00 PM");
            club.setNotes("Advance booking required for private events, contact the office.");
            facilityTimingRepository.save(club);

            FacilityTiming garden = new FacilityTiming();
            garden.setFacilityName("Garden / Walking Track");
            garden.setTimings("5:00 AM - 10:00 PM");
            garden.setNotes("Open all days.");
            facilityTimingRepository.save(garden);
        }

        System.out.println("=============================================================");
        System.out.println(" Digital Panchayat is ready!  Visit: http://localhost:8080");
        System.out.println(" Demo ADMIN login:    admin@panchayat.local / admin123");
        System.out.println(" Demo RESIDENT login: ravi.sharma@example.com / resident123");
        System.out.println("=============================================================");
    }
}
