package com.example.soen345_winter2026

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.soen345_winter2026.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ibtnBack.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener { saveProfile() }
        binding.btnLogout.setOnClickListener { logout() }

        loadProfile()
    }

    private fun loadProfile() {
        val user = auth.currentUser ?: return
        binding.tvEmail.text = user.email

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("fullName") ?: ""
                binding.etFullName.setText(name)
                binding.tvAvatarInitial.text = if (name.isNotBlank()) name[0].uppercase() else "?"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load profile.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProfile() {
        val user = auth.currentUser ?: return
        val newName = binding.etFullName.text.toString().trim()

        if (newName.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(user.uid)
            .update("fullName", newName)
            .addOnSuccessListener {
                binding.tvAvatarInitial.text = newName[0].uppercase()
                Toast.makeText(this, "Profile updated.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent(this, LogInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
