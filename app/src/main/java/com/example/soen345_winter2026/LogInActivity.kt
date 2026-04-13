package com.example.soen345_winter2026

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.soen345_winter2026.database.RegistrationDB
import com.example.soen345_winter2026.databinding.RegistrationBinding
import com.example.soen345_winter2026.events.EventListActivity

class LogInActivity : AppCompatActivity() {

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

        binding.tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
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
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AdminPageActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "You are not an admin!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Login failed: $error", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showForgotPasswordDialog() {
        val emailInput = android.widget.EditText(this).apply {
            hint = "Enter your email"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        AlertDialog.Builder(this)
            .setTitle("Reset Password")
            .setMessage("We'll send a reset link to your email.")
            .setView(emailInput)
            .setPositiveButton("Send") { _, _ ->
                val email = emailInput.text.toString().trim()
                if (email.isEmpty()) {
                    Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show()
                } else {
                    registrationDB.sendPasswordReset(email) { success, error ->
                        if (success) {
                            Toast.makeText(this, "Reset email sent. Check your inbox.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Failed: $error", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
