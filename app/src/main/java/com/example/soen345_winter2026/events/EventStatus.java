package com.example.soen345_winter2026.events;

import com.google.firebase.firestore.PropertyName;

public enum EventStatus {
	@PropertyName("active")
	ACTIVE,

	@PropertyName("past")
	PAST,

	@PropertyName("cancelled")
	CANCELLED
}