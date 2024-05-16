package com.example.strider;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EditJourneyTest {
    @Test
    public void testValidateEditJourneyInput_ValidInput() {
        assertTrue(EditJourney.validateEditJourneyInput("Title", "Comment",  "3"));
    }

    @Test
    public void testValidateEditJourneyInput_EmptyTitle() {
        assertFalse(EditJourney.validateEditJourneyInput("", "Comment", "3"));
    }

    @Test
    public void testValidateEditJourneyInput_InvalidRating_LowerBound() {
        assertFalse(EditJourney.validateEditJourneyInput("Title", "Comment", "-1"));
    }

    @Test
    public void testValidateEditJourneyInput_InvalidRating_UpperBound() {
        assertFalse(EditJourney.validateEditJourneyInput("Title", "Comment", "6"));
    }

    @Test
    public void testValidateEditJourneyInput_NonIntegerRating() {
        assertFalse(EditJourney.validateEditJourneyInput("Title", "Comment", "Rating"));
    }
}
