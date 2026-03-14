package com.example.soen345_winter2026.events

object EventFilter {

    fun filter(
        events: List<Event>,
        query: String = "",
        category: String = "",
        date: String = "",
        location: String = ""
    ): List<Event> {
        return events.filter { event ->
            val matchesQuery = query.isBlank() ||
                event.title.contains(query, ignoreCase = true) ||
                event.location.contains(query, ignoreCase = true)

            val matchesCategory = category.isBlank() ||
                event.category.equals(category, ignoreCase = true)

            val matchesDate = date.isBlank() ||
                event.date.contains(date, ignoreCase = true)

            val matchesLocation = location.isBlank() ||
                event.location.contains(location, ignoreCase = true)

            matchesQuery && matchesCategory && matchesDate && matchesLocation
        }
    }
}
