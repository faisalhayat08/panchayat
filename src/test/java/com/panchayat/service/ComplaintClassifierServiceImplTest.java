package com.panchayat.service;

import com.panchayat.model.ComplaintCategory;
import com.panchayat.service.impl.ComplaintClassifierServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Basic unit tests for the keyword-based AI classification step of the
 * complaint pipeline.
 */
class ComplaintClassifierServiceImplTest {

    private final ComplaintClassifierServiceImpl classifier = new ComplaintClassifierServiceImpl();

    @Test
    void classifiesPlumbingComplaint() {
        String text = "The bathroom pipe in my flat has been leaking water for two days.";
        assertEquals(ComplaintCategory.PLUMBING, classifier.classify(text));
    }

    @Test
    void classifiesElectricalComplaint() {
        String text = "The street light near the gate is not working at night.";
        assertEquals(ComplaintCategory.ELECTRICAL, classifier.classify(text));
    }

    @Test
    void classifiesLiftComplaint() {
        String text = "Lift number 2 gets stuck in the middle, this is a safety issue.";
        assertEquals(ComplaintCategory.LIFT_ELEVATOR, classifier.classify(text));
    }

    @Test
    void fallsBackToOtherWhenNoKeywordsMatch() {
        String text = "I just wanted to say the committee is doing a great job overall.";
        assertEquals(ComplaintCategory.OTHER, classifier.classify(text));
    }

    @Test
    void handlesNullOrBlankTextGracefully() {
        assertEquals(ComplaintCategory.OTHER, classifier.classify(null));
        assertEquals(ComplaintCategory.OTHER, classifier.classify("   "));
    }
}
