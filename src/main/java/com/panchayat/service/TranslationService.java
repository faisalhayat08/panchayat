package com.panchayat.service;

/**
 * Translates transcribed complaint text into English so admins/staff can
 * read every complaint uniformly regardless of which language the resident
 * spoke in.
 *
 * A mock, offline implementation is provided by default. Swap in a real
 * provider (Google Cloud Translation, Azure Translator, DeepL, etc.) by
 * implementing this interface (see README.md).
 */
public interface TranslationService {
    String translateToEnglish(String text, String sourceLanguage);
}
