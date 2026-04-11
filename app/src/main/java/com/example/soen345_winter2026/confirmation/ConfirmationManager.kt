package com.example.soen345_winter2026.confirmation

import android.content.Context
import android.util.Log
import com.example.soen345_winter2026.reservation.Reservation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object ConfirmationManager {

    private const val TAG = "ConfirmationManager"

    var emailService: EmailService = EmailNotify

    fun notify(
        context: Context,
        reservation: Reservation,
        callback: (Boolean, String?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.w(TAG, "User not logged in — skipping confirmation")
            callback(false, "User not logged in")
            return
        }

        Log.d(TAG, "Fetching user data for userId=$userId")
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document == null || !document.exists()) {
                    Log.w(TAG, "User document not found for userId=$userId")
                    callback(false, "User data not found")
                    return@addOnSuccessListener
                }

                val email = document.getString("email") ?: ""
                val phone = document.getString("phone") ?: ""

                val hasRealEmail = email.isNotEmpty() && !email.contains("@phone.com")
                val hasPhone = phone.isNotEmpty()

                Log.d(TAG, "User contact info — email=$email (valid=$hasRealEmail), phone=$phone (valid=$hasPhone)")

                if (!hasRealEmail && !hasPhone) {
                    Log.w(TAG, "No valid contact info on file")
                    callback(false, "No valid contact information on file")
                    return@addOnSuccessListener
                }

                val message = ConfirmationMessage.fromReservation(reservation, email, phone)

                when {
                    hasRealEmail && hasPhone -> {
                        emailService.send(message) { emailSuccess, emailError ->
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
                    hasRealEmail -> {
                        emailService.send(message) { success, error ->
                            callback(success, error)
                        }
                    }
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