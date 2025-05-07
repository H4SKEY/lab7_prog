package org.example.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static String toString(LocalDateTime dateTime) {
        return dateTime != null ? FORMATTER.format(dateTime) : null;
    }

    public static LocalDateTime fromString(String dateTimeStr) {
        return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr, FORMATTER) : null;
    }
}