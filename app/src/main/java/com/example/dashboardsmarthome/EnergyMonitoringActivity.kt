package com.example.dashboardsmarthome

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dashboardsmarthome.databinding.ActivityEnergyMonitoringBinding
import com.google.firebase.database.*
import android.os.Handler
import android.os.Looper
import com.google.android.material.appbar.MaterialToolbar
import kotlin.random.Random

class EnergyMonitoringActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnergyMonitoringBinding
    private lateinit var database: DatabaseReference
    private lateinit var postListener: ValueEventListener

    private val handler = Handler(Looper.getMainLooper())
    private val dummyUpdateRunnable = object : Runnable {
        override fun run() {
            updateDummyData()
            handler.postDelayed(this, 10_000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEnergyMonitoringBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("Power")

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleBold)

        binding.topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, BottomNavFrameActivity::class.java)
            startActivity(intent)
            finish()
        }

        showDummyData()

    }

    override fun onStart() {
        super.onStart()

        postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val powerData = snapshot.getValue(PowerData::class.java)

                powerData?.let { data ->
                    if (data.current.isNullOrEmpty() || data.energy.isNullOrEmpty() ||
                        data.frequency.isNullOrEmpty() || data.powerFactor.isNullOrEmpty() ||
                        data.power.isNullOrEmpty() || data.voltage.isNullOrEmpty()) {
                        showDummyData()
                    } else {
                        binding.tvCurrentUsage.text = "${data.current} A"
                        binding.tvEnergyUsage.text = "${data.energy} Wh"
                        binding.tvFrequencyUsage.text = "${data.frequency} Hz"
                        binding.tvPowerFactorUsage.text = data.powerFactor
                        binding.tvPowerUsage.text = "${data.power} W"
                        binding.tvVoltageUsage.text = "${data.voltage} V"
                    }
                } ?: showDummyData()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EnergyMonitoringActivity, "Gagal terhubung ke Firebase, tampilkan data dummy.", Toast.LENGTH_SHORT).show()
                showDummyData()
            }
        }

        database.addValueEventListener(postListener)
    }

    override fun onStop() {
        super.onStop()
        database.removeEventListener(postListener)
        handler.removeCallbacks(dummyUpdateRunnable)
    }


    private fun updateDummyData() {
        val current = String.format("%.2f", Random.nextDouble(0.5, 2.0))
        val energy = String.format("%.1f", Random.nextDouble(100.0, 150.0))
        val frequency = String.format("%.1f", Random.nextDouble(49.5, 50.5))
        val powerFactor = String.format("%.2f", Random.nextDouble(0.90, 1.0))
        val power = String.format("%.1f", Random.nextDouble(200.0, 300.0))
        val voltage = String.format("%.1f", Random.nextDouble(210.0, 230.0))

        binding.tvCurrentUsage.text = "$current A"
        binding.tvEnergyUsage.text = "$energy Wh"
        binding.tvFrequencyUsage.text = "$frequency Hz"
        binding.tvPowerFactorUsage.text = powerFactor
        binding.tvPowerUsage.text = "$power W"
        binding.tvVoltageUsage.text = "$voltage V"
    }

    private fun showDummyData() {
        handler.post(dummyUpdateRunnable)
    }


    data class PowerData(
        val current: String? = null,
        val energy: String? = null,
        val frequency: String? = null,
        val powerFactor: String? = null,
        val power: String? = null,
        val voltage: String? = null
    )
}
