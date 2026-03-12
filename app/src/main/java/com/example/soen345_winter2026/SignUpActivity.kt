package com.example.soen345_winter2026

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.soen345_winter2026.databinding.SignUpBinding
class SignUpActivity: AppCompatActivity() {

    private lateinit var binding: SignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inflate the layout using the correct binding class
        binding = SignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Setup the Register Button
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                // Here you would typically save to a database
            }
        }

        // 3. Setup the link back to Login
        binding.tvLogin.setOnClickListener {
            finish() // Destroys this activity and goes back to MainActivity
        }
    }

}