package com.example.soen345_winter2026

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.soen345_winter2026.database.RegistrationDB
import com.example.soen345_winter2026.databinding.SignUpBinding

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: SignUpBinding
    lateinit var registrationDB: RegistrationDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Initialize ONLY if test didn’t inject mock
        if (!::registrationDB.isInitialized) {
            registrationDB = RegistrationDB()
        }

        binding.btnSignUp.setOnClickListener {
            val fullName = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                registrationDB.signUp(email, password, fullName) { success, error ->

                    if (success) {
                        Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }
}