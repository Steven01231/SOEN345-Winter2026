package com.example.soen345_winter2026.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.soen345_winter2026.utils.Validator

class RegistrationDB(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // change phone to fake email to pass firebase auth without needing phone
    private fun phoneToEmail(phone: String): String {
        return "$phone@phone.com"
    }

    private fun resolveAuthEmail(input: String): Pair<String?, String?> {
        return when {
            Validator.isValidEmail(input) -> Pair(input, null)
            Validator.isValidPhoneNumber(input) -> Pair(phoneToEmail(input), null)
            else -> Pair(null, "Invalid email or phone number")
        }
    }

    fun signUp(
        email: String,
        phone: String,
        password: String,
        fullName: String,
        isAdmin: Boolean,
        callback: (Boolean, String?) -> Unit
    ) {

        val hasEmail = Validator.isValidEmail(email)
        val hasPhone = Validator.isValidPhoneNumber(phone)

        if (!hasEmail && !hasPhone) {
            callback(false, "Please provide a valid email or phone number")
            return
        }

        val authEmail = if (hasEmail) email else phoneToEmail(phone)

        auth.createUserWithEmailAndPassword(authEmail, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val userId = auth.currentUser!!.uid

                    val user = hashMapOf(
                        "fullName" to fullName,
                        "email" to authEmail,
                        "isAdmin" to isAdmin,
                        "phone" to (if (hasPhone) phone else ""),
                    )

                    db.collection("users")
                        .document(userId)
                        .set(user)
                        .addOnSuccessListener {
                            callback(true, null)
                        }
                        .addOnFailureListener {
                            callback(false, it.message)
                        }

                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun logIn(
        input: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {

        val (email, error) = resolveAuthEmail(input)
        if (email == null) {
            callback(false, error)
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun adminLogIn(
        input: String,
        password: String,
        callback: (Boolean, Boolean, String?) -> Unit) {

        val (email, error) = resolveAuthEmail(input)
        if (email == null) {
            callback(false, false, error)
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        db.collection("users").document(userId).get()
                            .addOnSuccessListener { document ->
                                if (document != null && document.exists()) {
                                    val isAdmin = document.getBoolean("isAdmin") ?: false
                                    callback(true, isAdmin, null)
                                } else {
                                    callback(false, false, "User data not found")
                                }
                            }
                            .addOnFailureListener { e ->
                                callback(false, false, e.message)
                            }
                    }
                } else {
                    callback(false, false, task.exception?.message ?: "Login failed")
                }
            }
    }
}