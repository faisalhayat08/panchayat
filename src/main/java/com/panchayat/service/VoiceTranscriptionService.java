package com.panchayat.service;

/**
 * Converts a recorded voice complaint into text (speech-to-text / "AI analysis").
 *
 * A mock, offline implementation is provided by default (MockAiServiceImpl)
 * so the project runs and can be demoed without any paid API key.
 *
 * To plug in real AI: implement this interface calling a provider such as
 * OpenAI Whisper, Google Cloud Speech-to-Text, or AssemblyAI, and mark that
 * bean @Primary (see README.md "Connecting real AI" section).
 */
public interface VoiceTranscriptionService {

    /**
     * @param audioFilePath absolute/relative path to the saved audio file
     * @return the transcription result (text + detected language)
     */
    TranscriptionResult transcribe(String audioFilePath);

    record TranscriptionResult(String text, String detectedLanguage) {}
}
