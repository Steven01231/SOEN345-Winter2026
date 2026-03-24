package com.example.soen345_winter2026.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventFilterTest {

    private List<Event> events;

    @BeforeEach
    void setUp() {
        events = Arrays.asList(
                new Event("1", "Summer Music Festival", "Concert", "June 15, 2026", "Central Park", 150, "active", ""),
                new Event("2", "Avengers: Endgame", "Movie", "July 1, 2026", "Cinema City", 80, "active", ""),
                new Event("3", "NBA Finals", "Sports", "June 20, 2026", "Madison Square Garden", 0, "active", ""),
                new Event("4", "Paris Travel Tour", "Travel", "August 10, 2026", "Paris", 30, "active", ""),
                new Event("5", "Rock Concert Night", "Concert", "September 5, 2026", "Hollywood Bowl", 200, "active", "")
        );
    }

    // --- No filters ---

    @Test
    void filter_noFilters_returnsAllEvents() {
        List<Event> result = EventFilter.filter(events, "", "", "", "");
        assertEquals(5, result.size());
    }

    @Test
    void filter_nullFilters_returnsAllEvents() {
        List<Event> result = EventFilter.filter(events, null, null, null, null);
        assertEquals(5, result.size());
    }

    // --- Category filter ---

    @Test
    void filter_byCategoryMovie_returnsOnlyMovies() {
        List<Event> result = EventFilter.filter(events, "", "Movie", "", "");
        assertEquals(1, result.size());
        assertEquals("Avengers: Endgame", result.get(0).getTitle());
    }

    @Test
    void filter_byCategoryConcert_returnsOnlyConcerts() {
        List<Event> result = EventFilter.filter(events, "", "Concert", "", "");
        assertEquals(2, result.size());
    }

    @Test
    void filter_byCategorySports_returnsOnlySports() {
        List<Event> result = EventFilter.filter(events, "", "Sports", "", "");
        assertEquals(1, result.size());
        assertEquals("NBA Finals", result.get(0).getTitle());
    }

    @Test
    void filter_byCategoryTravel_returnsOnlyTravel() {
        List<Event> result = EventFilter.filter(events, "", "Travel", "", "");
        assertEquals(1, result.size());
        assertEquals("Paris Travel Tour", result.get(0).getTitle());
    }

    @Test
    void filter_byCategoryIsCaseInsensitive() {
        List<Event> result = EventFilter.filter(events, "", "concert", "", "");
        assertEquals(2, result.size());
    }

    @Test
    void filter_byNonExistentCategory_returnsEmpty() {
        List<Event> result = EventFilter.filter(events, "", "Theatre", "", "");
        assertTrue(result.isEmpty());
    }

    // --- Search query ---

    @Test
    void filter_byQueryMatchingTitle_returnsMatchingEvents() {
        List<Event> result = EventFilter.filter(events, "Music", "", "", "");
        assertEquals(1, result.size());
        assertEquals("Summer Music Festival", result.get(0).getTitle());
    }

    @Test
    void filter_byQueryMatchingLocation_returnsMatchingEvents() {
        List<Event> result = EventFilter.filter(events, "Park", "", "", "");
        assertEquals(1, result.size());
        assertEquals("Summer Music Festival", result.get(0).getTitle());
    }

    @Test
    void filter_byQueryIsCaseInsensitive() {
        List<Event> result = EventFilter.filter(events, "nba", "", "", "");
        assertEquals(1, result.size());
        assertEquals("NBA Finals", result.get(0).getTitle());
    }

    @Test
    void filter_byQueryWithNoMatch_returnsEmpty() {
        List<Event> result = EventFilter.filter(events, "xyznotfound", "", "", "");
        assertTrue(result.isEmpty());
    }

    @Test
    void filter_byQueryMatchesMultipleEvents() {
        List<Event> result = EventFilter.filter(events, "concert", "", "", "");
        // Matches "Rock Concert Night" (title) — but not Summer Music Festival or NBA
        assertEquals(1, result.size());
    }

    // --- Date filter ---

    @Test
    void filter_byDate_returnsMatchingEvents() {
        List<Event> result = EventFilter.filter(events, "", "", "June", "");
        assertEquals(2, result.size());
    }

    @Test
    void filter_byDateNoMatch_returnsEmpty() {
        List<Event> result = EventFilter.filter(events, "", "", "December", "");
        assertTrue(result.isEmpty());
    }

    // --- Location filter ---

    @Test
    void filter_byLocation_returnsMatchingEvents() {
        List<Event> result = EventFilter.filter(events, "", "", "", "Paris");
        assertEquals(1, result.size());
        assertEquals("Paris Travel Tour", result.get(0).getTitle());
    }

    @Test
    void filter_byLocationIsCaseInsensitive() {
        List<Event> result = EventFilter.filter(events, "", "", "", "paris");
        assertEquals(1, result.size());
    }

    @Test
    void filter_byLocationNoMatch_returnsEmpty() {
        List<Event> result = EventFilter.filter(events, "", "", "", "Tokyo");
        assertTrue(result.isEmpty());
    }

    // --- Combined filters ---

    @Test
    void filter_queryAndCategory_appliesBothFilters() {
        List<Event> result = EventFilter.filter(events, "Rock", "Concert", "", "");
        assertEquals(1, result.size());
        assertEquals("Rock Concert Night", result.get(0).getTitle());
    }

    @Test
    void filter_queryAndCategoryNoMatch_returnsEmpty() {
        List<Event> result = EventFilter.filter(events, "Avengers", "Concert", "", "");
        assertTrue(result.isEmpty());
    }

    @Test
    void filter_categoryAndDate_appliesBothFilters() {
        List<Event> result = EventFilter.filter(events, "", "Concert", "June", "");
        assertEquals(1, result.size());
        assertEquals("Summer Music Festival", result.get(0).getTitle());
    }

    // --- Sold out ---

    @Test
    void isSoldOut_whenZeroSeats_returnsTrue() {
        Event soldOut = new Event("x", "Test", "Sports", "Jan 1", "Venue", 0, "active", "");
        assertTrue(soldOut.isSoldOut());
    }

    @Test
    void isSoldOut_whenSeatsAvailable_returnsFalse() {
        Event available = new Event("x", "Test", "Sports", "Jan 1", "Venue", 50, "active", "");
        assertFalse(available.isSoldOut());
    }

    // --- Empty input list ---

    @Test
    void filter_emptyEventList_returnsEmpty() {
        List<Event> result = EventFilter.filter(List.of(), "Music", "Concert", "", "");
        assertTrue(result.isEmpty());
    }
}
