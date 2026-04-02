package com.example.soen345_winter2026

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.soen345_winter2026.database.RegistrationDB
import com.example.soen345_winter2026.databinding.SignUpBinding
import com.example.soen345_winter2026.utils.Validator

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: SignUpBinding
    lateinit var registrationDB: RegistrationDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!::registrationDB.isInitialized) {
            registrationDB = RegistrationDB()
        }

        binding.btnSignUp.setOnClickListener {
            val fullName = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            val phone = binding.etPhone.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            val isAdmin = binding.swIsAdmin.isChecked

            if (fullName.isEmpty()) {
                Toast.makeText(this, "Full name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (email.isEmpty() && phone.isEmpty()) {
                Toast.makeText(this, "Please provide either email or phone", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (email.isNotEmpty() && !Validator.isValidEmail(email)) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (phone.isNotEmpty() && !Validator.isValidPhoneNumber(phone)) {
                Toast.makeText(this, "Please enter a valid phone number (digits only)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                registrationDB.signUp(email, phone, password, fullName, isAdmin) { success, error ->

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