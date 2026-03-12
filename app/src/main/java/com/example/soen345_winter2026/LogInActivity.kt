package com.example.soen345_winter2026

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.soen345_winter2026.database.RegistrationDB
import com.example.soen345_winter2026.databinding.RegistrationBinding

class LogInActivity : ComponentActivity() {

    private lateinit var binding: RegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val registrationDB = RegistrationDB();

        binding.btnSignUp.setOnClickListener {
            // Replace 'LoginActivity' with the name of your target Activity class
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()

                registrationDB.logIn(email, password) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        // Navigate to next screen
                        val intent = Intent(this, MainPageActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Login failed: $error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}