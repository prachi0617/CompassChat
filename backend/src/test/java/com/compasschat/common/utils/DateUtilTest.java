package com.compasschat.common.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @Test
    void shouldThrowAssertionError_whenInstantiatedViaReflection() throws Exception {
        var ctor = DateUtil.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        assertThrows(Exception.class, ctor::newInstance);
    }

    @Test
    void shouldReturnFormattedString_whenFormatWithNonNullDateTime() {
        LocalDateTime dt = LocalDateTime.of(2025, 6, 15, 9, 30);
        assertEquals("2025-06-15 09:30", DateUtil.format(dt));
    }

    @Test
    void shouldReturnEmptyString_whenFormatWithNull() {
        assertEquals("", DateUtil.format(null));
    }

    @Test
    void shouldReturnNonNullDateTime_whenNowCalled() {
        assertNotNull(DateUtil.now());
    }

    @Test
    void shouldReturnMidnight_whenStartOfDay() {
        LocalDate date = LocalDate.of(2025, 3, 10);
        LocalDateTime result = DateUtil.startOfDay(date);
        assertEquals(0, result.getHour());
        assertEquals(0, result.getMinute());
        assertEquals(0, result.getSecond());
    }

    @Test
    void shouldReturnEndOfDay_whenEndOfDay() {
        LocalDate date = LocalDate.of(2025, 3, 10);
        LocalDateTime result = DateUtil.endOfDay(date);
        assertEquals(23, result.getHour());
        assertEquals(59, result.getMinute());
    }

    @Test
    void shouldReturnDaysBetween_whenGivenTwoDates() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 11, 0, 0);
        assertEquals(10L, DateUtil.daysBetween(start, end));
    }

    @Test
    void shouldReturnTrue_whenWithinLastNDays() {
        LocalDateTime recent = DateUtil.now().minusDays(2);
        assertTrue(DateUtil.isWithinLast(recent, 7));
    }

    @Test
    void shouldReturnFalse_whenNotWithinLastNDays() {
        LocalDateTime old = DateUtil.now().minusDays(30);
        assertFalse(DateUtil.isWithinLast(old, 7));
    }

    @Test
    void shouldReturnFalse_whenIsWithinLastWithNullDate() {
        assertFalse(DateUtil.isWithinLast(null, 7));
    }

    @Test
    void shouldReturnJustNow_whenTimeAgoWithVeryRecentDate() {
        LocalDateTime justNow = DateUtil.now().minusSeconds(10);
        assertEquals("just now", DateUtil.timeAgo(justNow));
    }

    @Test
    void shouldReturnMinutesAgo_whenTimeAgoWithFewMinutesAgo() {
        LocalDateTime recent = DateUtil.now().minusMinutes(5);
        String result = DateUtil.timeAgo(recent);
        assertTrue(result.endsWith("m ago"), "Expected minutes format but got: " + result);
    }

    @Test
    void shouldReturnHoursAgo_whenTimeAgoWithSeveralHoursAgo() {
        LocalDateTime hoursAgo = DateUtil.now().minusHours(3);
        String result = DateUtil.timeAgo(hoursAgo);
        assertTrue(result.endsWith("h ago"), "Expected hours format but got: " + result);
    }

    @Test
    void shouldReturnDaysAgo_whenTimeAgoWithFewDaysAgo() {
        LocalDateTime daysAgo = DateUtil.now().minusDays(3);
        String result = DateUtil.timeAgo(daysAgo);
        assertTrue(result.endsWith("d ago"), "Expected days format but got: " + result);
    }

    @Test
    void shouldReturnFormattedDate_whenTimeAgoWithOldDate() {
        LocalDateTime old = DateUtil.now().minusDays(30);
        String result = DateUtil.timeAgo(old);
        // Falls back to format(when) for old dates — should look like "yyyy-MM-dd HH:mm"
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}"),
                "Expected date format but got: " + result);
    }

    @Test
    void shouldReturnEmptyString_whenTimeAgoWithNull() {
        assertEquals("", DateUtil.timeAgo(null));
    }
}
