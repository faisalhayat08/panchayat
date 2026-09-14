package com.panchayat.service.impl;

import com.panchayat.service.VoiceTranscriptionService;
import org.springframework.stereotype.Service;

/**
 * Offline stand-in for a real speech-to-text AI provider.
 *
 * WHY A MOCK: this project must run for a college evaluation without any
 * paid API key or internet dependency. In a production deployment you would
 * replace this class with a real call to, e.g., OpenAI Whisper or Google
 * Cloud Speech-to-Text, using the exact same interface, so nothing else in
 * the app needs to change.
 *
 * For the demo, since we cannot literally run neural speech recognition
 * offline, this implementation deterministically derives readable demo text
 * from the uploaded audio file so the rest of the AI pipeline (translation,
 * classification, inbox delivery) can be shown working end-to-end. Replace
 * the body of transcribe() with a real API call when you have credentials.
 */
@Service
public class MockVoiceTranscriptionServiceImpl implements VoiceTranscriptionService {

    private static final String[] SAMPLE_COMPLAINTS = {
        "Mere flat ke bathroom mein pichle do din se pani ka pipe leak ho raha hai, please jaldi thik karwao.",
        "Society ke gate ke paas street light kaam nahi kar rahi hai, raat ko bahut andhera rehta hai.",
        "Building ke ground floor ka dustbin area bahut dino se saaf nahi hua hai, badbu aa rahi hai.",
        "Lift number 2 beech mein atak jati hai, ye safety issue hai please jaldi dekhwao.",
        "Parking area mein bina permission ke bahar ki gaadiya khadi ho rahi hain roz.",
        "Rat ko upar wale flat se bahut tez awaaz aati hai, so nahi paate."
    };

    @Override
    public TranscriptionResult transcribe(String audioFilePath) {
        // Deterministic "randomness" based on file path so repeated demo
        // recordings still look varied but reproducible.
        int idx = Math.floorMod(audioFilePath.hashCode(), SAMPLE_COMPLAINTS.length);
        String text = SAMPLE_COMPLAINTS[idx];
        return new TranscriptionResult(text, "Hindi");
    }
}
