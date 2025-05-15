package com.example.dashboardsmarthome

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.dashboardsmarthome.databinding.BottomNavFrameBinding
import com.google.android.material.appbar.MaterialToolbar

class BottomNavFrameActivity : AppCompatActivity() {

    private lateinit var binding: BottomNavFrameBinding
    private lateinit var navController: NavController
    private lateinit var homeText: TextView
    private lateinit var homeIcon: AppCompatImageView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = BottomNavFrameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = findViewById(R.id.topNavbar)
        setSupportActionBar(toolbar)

        homeText = findViewById(R.id.home_text)
        homeIcon = findViewById(R.id.home_icon)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_bottom_nav_frame) as NavHostFragment
        navController = navHostFragment.navController

        val navView = binding.navView
        navView.let {
            androidx.navigation.ui.NavigationUI.setupWithNavController(it, navController)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> {
                    homeText.text = "Home"
                    homeIcon.setImageResource(R.drawable.homeicon)
                    toolbar.visibility = View.VISIBLE
                }
                R.id.navigation_virtual -> {
                    homeText.text = "Virtual"
                    homeIcon.setImageResource(R.drawable._dcubeicon)
                    toolbar.visibility = View.VISIBLE
                }
                R.id.navigation_menu -> {
                    homeText.text = "AVARA"
                    homeIcon.setImageDrawable(null)
                    toolbar.visibility = View.VISIBLE
                }
                else -> {
                    homeText.text = ""
                    homeIcon.setImageDrawable(null)
                    toolbar.visibility = View.GONE
                }
            }
        }

    }
}