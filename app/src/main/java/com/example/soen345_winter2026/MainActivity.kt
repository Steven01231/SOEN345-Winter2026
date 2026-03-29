package com.example.soen345_winter2026

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.soen345_winter2026.ui.theme.SOEN345Winter2026Theme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.soen345_winter2026.databinding.RegistrationBinding

class MainActivity : ComponentActivity() {

    private lateinit var binding: RegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val currentUser = FirebaseAuth.getInstance().currentUser
        val intent = if (currentUser != null) {
            Intent(this, com.example.soen345_winter2026.events.EventListActivity::class.java)
        } else {
            Intent(this, LogInActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    private fun testFirebaseConnection() {
        val auth = FirebaseAuth.getInstance()

        Log.d(TAG, "Starting Firebase anonymous sign-in...")

        auth.signInAnonymously()
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid
                Log.d(TAG, "Anonymous sign-in successful. UID: $uid")

                // Write test document to Firestore
                if (uid != null) {
                    writeTestDocument(uid)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Anonymous sign-in failed", exception)
            }
    }

    private fun writeTestDocument(uid: String) {
        val db = FirebaseFirestore.getInstance()
        val testData = hashMapOf(
            "message" to "Firebase connection successful!",
            "timestamp" to System.currentTimeMillis()
        )

        Log.d(TAG, "Writing test document to /users/$uid/testData")

        db.collection("users")
            .document(uid)
            .collection("testData")
            .add(testData)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Test document written successfully. Document ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to write test document", exception)
            }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
