package com.panchayat.service.impl;

import com.panchayat.model.ComplaintCategory;
import com.panchayat.service.ComplaintClassifierService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Keyword-based "AI analysis" classifier. Scans the (translated, English)
 * complaint text for category keywords and returns the best-matching
 * category. This keeps the classification transparent and instant; it can
 * later be swapped for a trained ML model or an LLM classification call
 * behind the exact same interface.
 */
@Service
public class ComplaintClassifierServiceImpl implements ComplaintClassifierService {

    private static final Map<ComplaintCategory, String[]> KEYWORDS = new LinkedHashMap<>();
    static {
        KEYWORDS.put(ComplaintCategory.PLUMBING, new String[]{"pipe", "leak", "water", "tap", "drainage", "bathroom", "toilet"});
        KEYWORDS.put(ComplaintCategory.ELECTRICAL, new String[]{"light", "electric", "wiring", "power", "switch", "fuse"});
        KEYWORDS.put(ComplaintCategory.CLEANING_SANITATION, new String[]{"dustbin", "garbage", "clean", "smell", "sanitation", "waste"});
        KEYWORDS.put(ComplaintCategory.SECURITY, new String[]{"security", "guard", "gate", "theft", "stranger", "unsafe"});
        KEYWORDS.put(ComplaintCategory.NOISE, new String[]{"noise", "loud", "sound", "music", "party"});
        KEYWORDS.put(ComplaintCategory.PARKING, new String[]{"parking", "vehicle", "car", "bike", "gaadi"});
        KEYWORDS.put(ComplaintCategory.LIFT_ELEVATOR, new String[]{"lift", "elevator", "stuck"});
        KEYWORDS.put(ComplaintCategory.GARDEN_MAINTENANCE, new String[]{"garden", "plant", "tree", "lawn"});
    }

    @Override
    public ComplaintCategory classify(String translatedText) {
        if (translatedText == null || translatedText.isBlank()) {
            return ComplaintCategory.OTHER;
        }
        String lower = translatedText.toLowerCase();

        ComplaintCategory bestCategory = ComplaintCategory.OTHER;
        int bestScore = 0;

        for (Map.Entry<ComplaintCategory, String[]> entry : KEYWORDS.entrySet()) {
            int score = 0;
            for (String keyword : entry.getValue()) {
                if (lower.contains(keyword)) {
                    score++;
                }
            }
            if (score > bestScore) {
                bestScore = score;
                bestCategory = entry.getKey();
            }
        }
        return bestCategory;
    }
}
