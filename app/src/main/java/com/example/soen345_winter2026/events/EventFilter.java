package com.example.soen345_winter2026.events;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure Java utility for filtering events by search query, category, date, and location.
 * Kept separate from Android/Firestore to be fully unit-testable.
 */
public class EventFilter {

    public static List<Event> filter(
            List<Event> events,
            String query,
            String category,
            String date,
            String location) {

        List<Event> result = new ArrayList<>();
        for (Event event : events) {
            if (matches(event, query, category, date, location)) {
                result.add(event);
            }
        }
        return result;
    }

    private static boolean matches(Event event, String query, String category,
                                   String date, String location) {
        return matchesQuery(event, query)
                && matchesCategory(event, category)
                && matchesDate(event, date)
                && matchesLocation(event, location);
    }

    private static boolean matchesQuery(Event event, String query) {
        if (query == null || query.trim().isEmpty()) return true;
        String q = query.toLowerCase();
        return event.getTitle().toLowerCase().contains(q)
                || event.getLocation().toLowerCase().contains(q);
    }

    private static boolean matchesCategory(Event event, String category) {
        if (category == null || category.isEmpty()) return true;
        return event.getCategory().equalsIgnoreCase(category);
    }

    private static boolean matchesDate(Event event, String date) {
        if (date == null || date.isEmpty()) return true;
        return event.getDate().toLowerCase().contains(date.toLowerCase());
    }

    private static boolean matchesLocation(Event event, String location) {
        if (location == null || location.isEmpty()) return true;
        return event.getLocation().toLowerCase().contains(location.toLowerCase());
    }
}
