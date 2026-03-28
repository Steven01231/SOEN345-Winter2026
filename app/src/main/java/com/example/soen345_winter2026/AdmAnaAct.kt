package com.example.soen345_winter2026

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.soen345_winter2026.databinding. AnalyticsAdminPageBinding

class AdminAnalyticsActivity : AppCompatActivity() {

    private lateinit var binding:  AnalyticsAdminPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  AnalyticsAdminPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}