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
//    private lateinit var database: DatabaseReference
//    private lateinit var postListener: ValueEventListener

//    private lateinit var homeViewModel: HomeViewModel

//    private val handler = Handler(Looper.getMainLooper())
//    private val dummyUpdateRunnable = object : Runnable {
//        override fun run() {
//            updateDummyData()
//            handler.postDelayed(this, 10_000)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEnergyMonitoringBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleBold)

        binding.topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, BottomNavFrameActivity::class.java)
            startActivity(intent)
            finish()
        }

//        showDummyData()

        val receivedPowerData: PowerData? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("power_data", PowerData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("power_data")
        }


        if (receivedPowerData != null) {
            binding.tvCurrentUsage.text = "${receivedPowerData.current ?: "--"} A"
            binding.tvEnergyUsage.text = "${receivedPowerData.energy ?: "--"} Wh"
            binding.tvFrequencyUsage.text = "${receivedPowerData.frequency ?: "--"} Hz"
            binding.tvPowerFactorUsage.text = receivedPowerData.powerFactor?.toString() ?: "--"
            binding.tvPowerUsage.text = "${receivedPowerData.power ?: "--"} W"
            binding.tvVoltageUsage.text = "${receivedPowerData.voltage ?: "--"} V"
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
//
//    override fun onStart() {
//        super.onStart()
//    }
//
//    override fun onStop() {
//        super.onStop()
////        handler.removeCallbacks(dummyUpdateRunnable)
//    }


//    private fun updateDummyData() {
//        val current = String.format("%.2f", Random.nextDouble(0.5, 2.0))
//        val energy = String.format("%.1f", Random.nextDouble(100.0, 150.0))
//        val frequency = String.format("%.1f", Random.nextDouble(49.5, 50.5))
//        val powerFactor = String.format("%.2f", Random.nextDouble(0.90, 1.0))
//        val power = String.format("%.1f", Random.nextDouble(200.0, 300.0))
//        val voltage = String.format("%.1f", Random.nextDouble(210.0, 230.0))
//
//        binding.tvCurrentUsage.text = "$current A"
//        binding.tvEnergyUsage.text = "$energy Wh"
//        binding.tvFrequencyUsage.text = "$frequency Hz"
//        binding.tvPowerFactorUsage.text = powerFactor
//        binding.tvPowerUsage.text = "$power W"
//        binding.tvVoltageUsage.text = "$voltage V"
//    }
//
//    private fun showDummyData() {
//        updateDummyData()
//    }
}
