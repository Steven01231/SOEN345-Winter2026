package com.example.soen345_winter2026.database

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationDB {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun signUp(email: String, password: String, fullName: String, callback: (Boolean, String?) -> Unit) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val userId = auth.currentUser!!.uid

                    val user = hashMapOf(
                        "fullName" to fullName,
                        "email" to email
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

    fun logIn(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        val auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful
                    callback(true, null)
                } else {
                    // Login failed, return the error message
                    callback(false, task.exception?.message)
                }
            }
    }


}