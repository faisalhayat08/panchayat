# Digital Panchayat — Society Management Platform

A Java (Spring Boot) web platform for a residential society. Built as an
internship project for a Java Developer role at a web solutions service
company.

## The problem it solves

A housing society usually juggles many disconnected things: WhatsApp
complaints, a physical register for plumber/cleaning requests, a notice
board that's easy to miss, and no single place to check gym timings, society
rules, ongoing construction projects, or upcoming events. **Digital
Panchayat** puts all of it behind one login.

## Headline feature — one-button voice complaints

A resident presses a single microphone button and speaks their problem in
whatever language they're comfortable in. The app then:

1. **Records** the audio in the browser (MediaRecorder API, no plugins).
2. **Transcribes** it to text ("AI analysis" step).
3. **Translates** it to English.
4. **Classifies** it into a category (Plumbing, Electrical, Cleaning,
   Security, Noise, Parking, Lift, Garden, Other) using keyword-based NLP.
5. **Delivers** it instantly to the Admin Inbox (in-app + optional email),
   with the original text, translation, category and timestamp all
   attached — no manual form filling.

The AI steps ship with a transparent, deterministic **mock implementation**
(`MockVoiceTranscriptionServiceImpl`, `MockTranslationServiceImpl`) so the
whole pipeline runs completely offline for a live evaluation/demo with zero
API keys. See **"Connecting real AI"** below to swap in a real provider.

## Other modules

- **Service requests** — book a plumber, electrician, cleaner, pest
  control, carpenter, painter or gym trainer directly.
- **Society info page** — history, facility timings (gym, pool, clubhouse,
  garden), the rule book, and a table of upcoming/ongoing/completed
  projects.
- **Notice board** — committee announcements, with an "important" flag.
- **Events** — festival celebrations, AGMs, health camps, etc.
- **Admin dashboard** — a unified inbox for every complaint and service
  request, with one-click status updates (New → Acknowledged → In
  Progress → Resolved), plus notice/event management.

## Tech stack

| Layer          | Technology                                   |
|----------------|-----------------------------------------------|
| Language       | Java 17                                        |
| Framework      | Spring Boot 3.2 (Web MVC, Data JPA, Security)  |
| Templating     | Thymeleaf + Spring Security Thymeleaf dialect  |
| Database       | H2 (in-memory, zero-setup demo) / MySQL-ready  |
| Build tool     | Maven                                          |
| Frontend       | HTML5, CSS3, vanilla JS (MediaRecorder API)    |
| Auth           | Spring Security (BCrypt, role-based: RESIDENT/ADMIN) |

## Project structure

```
src/main/java/com/panchayat/
  PanchayatApplication.java        entry point
  config/                          Spring Security + demo data seeding
  model/                           JPA entities & enums
  repository/                      Spring Data JPA repositories
  service/  + service/impl/        business logic, incl. the AI pipeline
  controller/                      MVC controllers (resident, admin, auth)
src/main/resources/
  templates/                       Thymeleaf views (+ templates/admin)
  static/css/style.css             design system
  static/js/voice-recorder.js      one-button recorder + AI-pipeline upload
  application.properties           config (DB, mail, AI provider switch)
src/test/java/...                  unit tests (complaint classifier)
```

## How to run

**Prerequisites:** JDK 17+ and Maven 3.8+ (or use your IDE's built-in
Maven support — IntelliJ IDEA / Eclipse / VS Code all work out of the box).

```bash
mvn spring-boot:run
```

Then open **http://localhost:8080**. The database (H2, in-memory) is
seeded automatically on startup with demo users, notices, events, projects
and facility timings — nothing to configure.

**Demo logins** (also printed to the console on startup):

| Role     | Email                         | Password      |
|----------|--------------------------------|---------------|
| Admin    | admin@panchayat.local          | admin123      |
| Resident | ravi.sharma@example.com        | resident123   |

The H2 console (for inspecting tables live) is available at
`/h2-console` with JDBC URL `jdbc:h2:mem:panchayatdb`, user `sa`, blank
password.

## Running the tests

```bash
mvn test
```

## Switching to MySQL

Open `application.properties`, comment out the H2 block, and uncomment the
MySQL block (update username/password). Add your schema name; Hibernate
will auto-create the tables (`spring.jpa.hibernate.ddl-auto=update`).

## Connecting real AI (optional, for production use)

This project is intentionally built so the AI layer is a clean,
swappable interface — nothing else in the codebase needs to change:

- `service/VoiceTranscriptionService.java` — implement against e.g.
  OpenAI Whisper or Google Cloud Speech-to-Text and call it from a new
  `@Service` (e.g. `WhisperTranscriptionServiceImpl`), then mark it
  `@Primary` so Spring picks it over the mock.
- `service/TranslationService.java` — same pattern, e.g. Google Cloud
  Translation or Azure Translator.
- `service/ComplaintClassifierService.java` — swap the keyword matcher
  for a trained ML model, or call an LLM classification prompt.

Fill in `app.ai.api-key` and `app.ai.provider` in `application.properties`
once you wire up a real provider.

## Notes for your internship evaluation / viva

- **Layered architecture**: Controller → Service (interface + impl) →
  Repository → Entity, which is the standard enterprise Java pattern —
  good talking point for "why did you structure it this way?"
- **Security**: passwords are BCrypt-hashed, role-based access control
  restricts `/admin/**` to the ADMIN role, CSRF protection is on
  everywhere (including the JS-based voice upload, which manually attaches
  the CSRF token since `fetch()` bypasses Thymeleaf's automatic form
  injection — a good detail to mention if asked).
- **Why a mock AI layer**: lets you demo the complete pipeline live,
  offline, deterministically, without depending on a paid API during
  evaluation — while the code is structured so a real provider is a
  drop-in replacement.
- **Async notifications**: admin email alerts are sent with `@Async` so
  filing a complaint never has to wait on SMTP.
