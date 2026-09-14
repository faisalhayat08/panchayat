package com.panchayat.service;

import com.panchayat.model.ComplaintCategory;

/**
 * "AI analysis" step: reads the translated complaint text and decides which
 * category / department it belongs to (plumbing, electrical, security...).
 * Implemented with transparent keyword-based NLP so it is explainable and
 * runs instantly with no external dependency; can be swapped for a real
 * ML/LLM classifier later without touching any caller.
 */
public interface ComplaintClassifierService {
    ComplaintCategory classify(String translatedText);
}
