package com.example.soen345_winter2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.soen345_winter2026.databinding.MainPageBinding
import com.example.soen345_winter2026.databinding.RegistrationBinding

class MainPageActivity: ComponentActivity() {

    private lateinit var binding: MainPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}