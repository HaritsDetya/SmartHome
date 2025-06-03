package com.example.dashboardsmarthome

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dashboardsmarthome.databinding.ActivityEnergyMonitoringBinding
import com.example.dashboardsmarthome.dataAPI.PowerData
import com.google.android.material.appbar.MaterialToolbar

class EnergyMonitoringActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnergyMonitoringBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEnergyMonitoringBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleBold)

        binding.topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, BottomNavFrameActivity::class.java)
            startActivity(intent)
            finish()
        }

        val receivedPowerData: PowerData? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("power_data", PowerData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("power_data")
        }


        if (receivedPowerData != null) {
            binding.tvCurrentUsage.text = "${receivedPowerData.current?.let { String.format("%.2f", it) } ?: "--"} A"
            binding.tvEnergyUsage.text = "${receivedPowerData.energy?.let { String.format("%.5f", it) } ?: "--"} Wh"
            binding.tvFrequencyUsage.text = "${receivedPowerData.frequency?.let { String.format("%.1f", it) } ?: "--"} Hz"
            binding.tvPowerFactorUsage.text = receivedPowerData.pf?.let { String.format("%.2f", it) } ?: "--"
            binding.tvPowerUsage.text = "${receivedPowerData.power?.let { String.format("%.2f", it) } ?: "--"} W"
            binding.tvVoltageUsage.text = "${receivedPowerData.voltage?.let { String.format("%.2f", it) } ?: "--"} V"
        } else {
            Toast.makeText(this, "Tidak ada data energi yang diterima. Tampilkan data default.", Toast.LENGTH_SHORT).show()
            binding.tvCurrentUsage.text = "-- A"
            binding.tvEnergyUsage.text = "-- Wh"
            binding.tvFrequencyUsage.text = "-- Hz"
            binding.tvPowerFactorUsage.text = "--"
            binding.tvPowerUsage.text = "-- W"
            binding.tvVoltageUsage.text = "-- V"
        }
    }
}
