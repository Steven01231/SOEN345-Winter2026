package com.example.soen345_winter2026.confirmation

import android.content.Context
import com.example.soen345_winter2026.reservation.Reservation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object ConfirmationManager {

    fun notify(
        context: Context,
        reservation: Reservation,
        callback: (Boolean, String?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if (userId == null) {
            callback(false, "User not logged in")
            return
        }

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document == null || !document.exists()) {
                    callback(false, "User data not found")
                    return@addOnSuccessListener
                }

                val email = document.getString("email") ?: ""
                val phone = document.getString("phone") ?: ""

                val hasRealEmail = email.isNotEmpty() && !email.contains("@phone.com")
                val hasPhone = phone.isNotEmpty()

                if (!hasRealEmail && !hasPhone) {
                    callback(false, "No valid contact information on file")
                    return@addOnSuccessListener
                }

                val message = ConfirmationMessage.fromReservation(reservation, email, phone)

                when {
                    // has both = send both
                    hasRealEmail && hasPhone -> {
                        EmailNotify.send(message) { emailSuccess, emailError ->
                            SmsNotify.send(context, message) { smsSuccess, smsError ->
                                when {
                                    emailSuccess && smsSuccess ->
                                        callback(true, null)
                                    emailSuccess ->
                                        callback(true, "Email sent but SMS failed: $smsError")
                                    smsSuccess ->
                                        callback(true, "SMS sent but email failed: $emailError")
                                    else ->
                                        callback(false, "Both failed — Email: $emailError, SMS: $smsError")
                                }
                            }
                        }
                    }
                    // email only
                    hasRealEmail -> {
                        EmailNotify.send(message) { success, error ->
                            callback(success, error)
                        }
                    }
                    // phone left
                    else -> {
                        SmsNotify.send(context, message) { success, error ->
                            callback(success, error)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }
}