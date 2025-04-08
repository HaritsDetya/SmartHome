package com.example.dashboardsmarthome.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dashboardsmarthome.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}