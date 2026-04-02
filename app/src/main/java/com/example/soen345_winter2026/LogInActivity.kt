package com.example.soen345_winter2026

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.soen345_winter2026.database.RegistrationDB
import com.example.soen345_winter2026.databinding.RegistrationBinding
import com.example.soen345_winter2026.events.EventListActivity

class LogInActivity : ComponentActivity() {

    lateinit var binding: RegistrationBinding
    lateinit var registrationDB: RegistrationDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeDependencies()
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun initializeDependencies() {
        // Initialize ONLY if test didn't inject mock
        if (!::registrationDB.isInitialized) {
            registrationDB = RegistrationDB()
        }

        binding = RegistrationBinding.inflate(layoutInflater)
    }

    private fun setupClickListeners() {
        binding.btnSignUp.setOnClickListener {
            handleSignUp()
        }

        binding.btnLogin.setOnClickListener {
            handleUserLogin()
        }

        binding.adminBtnLogin.setOnClickListener {
            handleAdminLogin()
        }
    }

    private fun handleSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun handleUserLogin() {
        val email = binding.etEmailPhone.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        registrationDB.logIn(email, password) { success, error ->
            if (success) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                // Navigate to event list after login
                val intent = Intent(this, EventListActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Login failed: $error", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleAdminLogin() {
        val email = binding.etEmailPhone.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        registrationDB.adminLogIn(email, password) { success, isAdmin, error ->
            if (success) {
                if (isAdmin) {
                    // Navigate to Admin specific activity
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AdminPageActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Navigate to regular User activity
                    Toast.makeText(this, "You are not an admin!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Login failed: $error", Toast.LENGTH_LONG).show()
            }
        }
    }
}