package com.panchayat.service.impl;

import com.panchayat.service.TranslationService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Offline rule-based translator used for the demo pipeline (see
 * MockVoiceTranscriptionServiceImpl for why a mock is used). It performs a
 * simple phrase-dictionary substitution from common Hinglish/Hindi
 * complaint phrases to English so the resulting text is realistic and
 * readable in the Admin Inbox. Swap for a real translation API in
 * production (Google Cloud Translation, Azure Translator, DeepL, etc.).
 */
@Service
public class MockTranslationServiceImpl implements TranslationService {

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
    }

    @Override
    public String translateToEnglish(String text, String sourceLanguage) {
        if (text == null) return "";
        if ("English".equalsIgnoreCase(sourceLanguage)) {
            return text;
        }
        return PHRASE_MAP.getOrDefault(text.trim(),
                // Fallback: return original text tagged, for any free-typed complaint
                // that doesn't match a known demo phrase.
                text);
    }
}
