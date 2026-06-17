package com.compasschat.common.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class DateUtil {

    /** Stick to ISO display format across the whole app. */
    public static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Single source of truth for "what time zone are we in?" */
    public static final ZoneId APP_ZONE = ZoneId.of("UTC");

    private DateUtil() {
        // Never instantiated. If reflection tries, refuse.
        throw new AssertionError("DateUtil is a utility class; do not instantiate");
    }

    public static String format(LocalDateTime when) {
        return when == null ? "" : when.format(DISPLAY_FORMAT);
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(APP_ZONE);
    }

    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }

    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    public static boolean isWithinLast(LocalDateTime when, long days) {
        if (when == null) return false;
        return when.isAfter(now().minusDays(days));
    }

    public static String timeAgo(LocalDateTime when) {
        if (when == null) return "";
        Duration d = Duration.between(when, now());
        if (d.toMinutes() < 1)  return "just now";
        if (d.toMinutes() < 60) return d.toMinutes() + "m ago";
        if (d.toHours()   < 24) return d.toHours()   + "h ago";
        if (d.toDays()    < 7)  return d.toDays()    + "d ago";
        return format(when); // far enough back: show the date
    }
}
