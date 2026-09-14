package com.panchayat.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panchayat.service.TranslationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Intelligent translator supporting:
 * 1. Fast exact-phrase dictionary lookup (offline demo compatibility)
 * 2. Real-time online translation via Google Translate (for actual spoken Hindi/Hinglish/regional languages)
 * 3. Graceful offline fallback to original text if internet is unreachable
 */
@Service
public class MockTranslationServiceImpl implements TranslationService {

    private static final Logger log = LoggerFactory.getLogger(MockTranslationServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private static final Map<String, String> PHRASE_MAP = new LinkedHashMap<>();
    static {
        PHRASE_MAP.put("Mere flat ke bathroom mein pichle do din se pani ka pipe leak ho raha hai, please jaldi thik karwao.",
                "The bathroom pipe in my flat has been leaking water for the last two days, please get it fixed urgently.");
        PHRASE_MAP.put("Society ke gate ke paas street light kaam nahi kar rahi hai, raat ko bahut andhera rehta hai.",
                "The street light near the society gate is not working, it stays very dark at night.");
        PHRASE_MAP.put("Building ke ground floor ka dustbin area bahut dino se saaf nahi hua hai, badbu aa rahi hai.",
                "The dustbin area on the ground floor of the building has not been cleaned for many days and is smelling bad.");
        PHRASE_MAP.put("Lift number 2 beech mein atak jati hai, ye safety issue hai please jaldi dekhwao.",
                "Lift number 2 gets stuck in the middle, this is a safety issue, please get it checked urgently.");
        PHRASE_MAP.put("Parking area mein bina permission ke bahar ki gaadiya khadi ho rahi hain roz.",
                "Outside vehicles are parking in the parking area without permission every day.");
        PHRASE_MAP.put("Rat ko upar wale flat se bahut tez awaaz aati hai, so nahi paate.",
                "There is very loud noise from the flat upstairs at night, we are not able to sleep.");
        PHRASE_MAP.put("Pani nahi aa raha", "Water is not coming.");
        PHRASE_MAP.put("Bijli chali gayi", "Electricity has gone out.");
        PHRASE_MAP.put("Kachra saaf nahi hua", "Garbage has not been cleaned.");
        PHRASE_MAP.put("Lift band hai", "The elevator is out of service.");
    }

    @Override
    public String translateToEnglish(String text, String sourceLanguage) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String trimmed = text.trim();

        // 1. Direct dictionary check
        if (PHRASE_MAP.containsKey(trimmed)) {
            return PHRASE_MAP.get(trimmed);
        }

        // If source language is purely English, return as-is
        if ("English".equalsIgnoreCase(sourceLanguage)) {
            return trimmed;
        }

        // 2. Real-time dynamic translation
        try {
            String encoded = URLEncoder.encode(trimmed, StandardCharsets.UTF_8);
            String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=en&dt=t&q=" + encoded;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(4))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 && response.body() != null) {
                JsonNode root = objectMapper.readTree(response.body());
                if (root.isArray() && root.size() > 0 && root.get(0).isArray()) {
                    StringBuilder sb = new StringBuilder();
                    for (JsonNode item : root.get(0)) {
                        if (item.isArray() && item.size() > 0 && !item.get(0).isNull()) {
                            sb.append(item.get(0).asText());
                        }
                    }
                    String translated = sb.toString().trim();
                    if (!translated.isEmpty()) {
                        return translated;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Dynamic translation failed or timed out: {}. Using original text.", e.getMessage());
        }

        // 3. Fallback: return original text
        return trimmed;
    }
}
