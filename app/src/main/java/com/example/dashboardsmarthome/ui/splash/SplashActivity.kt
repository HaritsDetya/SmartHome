package com.example.dashboardsmarthome.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.dashboardsmarthome.BottomNavFrameActivity
import com.example.dashboardsmarthome.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // Make sure this layout exists

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, BottomNavFrameActivity::class.java))
            finish()
        }, 2000)
    }
}
