package com.example.soen345_winter2026.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationDB(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun signUp(
        email: String,
        password: String,
        fullName: String,
        isAdmin: Boolean,
        callback: (Boolean, String?) -> Unit
    ) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val userId = auth.currentUser!!.uid

                    val user = hashMapOf(
                        "fullName" to fullName,
                        "email" to email,
                        "isAdmin" to isAdmin
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
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun adminLogIn(email: String, password: String, callback: (Boolean, Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Fetch the user document from Firestore
                        db.collection("users").document(userId).get()
                            .addOnSuccessListener { document ->
                                if (document != null && document.exists()) {
                                    val isAdmin = document.getBoolean("isAdmin") ?: false
                                    // Return success, the admin status, and no error
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