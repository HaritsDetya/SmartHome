package com.example.dashboardsmarthome

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.enums.Anchor
import com.anychart.enums.MarkerType
import com.anychart.enums.TooltipPositionMode
import com.example.dashboardsmarthome.databinding.ActivityEnergyMonitoringBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EnergyMonitoringActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnergyMonitoringBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_energy_monitoring)
        enableEdgeToEdge()

        binding = ActivityEnergyMonitoringBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, BottomNavFrameActivity::class.java)
            startActivity(intent)
            finish()
        }

        databaseListener()

        val costChartView = findViewById<AnyChartView>(R.id.energy_cost_chart)
        val usageChartView = findViewById<AnyChartView>(R.id.energy_usage_chart)

        setupChart(costChartView, "Total Energy Cost per Month", getCostData())
        setupChart(usageChartView, "Total Energy Usage per Month", getUsageData())

    }

    private fun databaseListener() {
        database = FirebaseDatabase.getInstance().getReference("Power")
        val postListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val current = snapshot.child("current").value
                val energy = snapshot.child("energy").value
                val frequency = snapshot.child("frequency").value
                val powerFactor = snapshot.child("pf").value
                val power = snapshot.child("power").value
                val voltage = snapshot.child("voltage").value

                binding.tvCurrentUsage.text = "$current A"
                binding.tvEnergyUsage.text = "$energy Wh"
                binding.tvFrequencyUsage.text = "$frequency Hz"
                binding.tvPowerFactorUsage.text = "$powerFactor"
                binding.tvPowerUsage.text = "$power W"
                binding.tvVoltageUsage.text = "$voltage V"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EnergyMonitoringActivity, "Failed to read power data", Toast.LENGTH_SHORT).show()
            }
        }
        database.addValueEventListener(postListener)
    }

    private fun setupChart(chartView: AnyChartView, title: String, data: List<DataEntry>) {
        val cartesian = AnyChart.line()
        cartesian.animation(true)
        cartesian.padding(10.0, 20.0, 5.0, 20.0)
        cartesian.crosshair().enabled(true)
        cartesian.crosshair().yLabel(true)
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
        cartesian.title(title)
        cartesian.yAxis(0).title("Value")
        cartesian.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)

        val series = cartesian.line(data)
        series.name(title)
        series.hovered().markers().enabled(true)
        series.hovered().markers().type(MarkerType.CIRCLE).size(4.0)
        series.tooltip().position("right").anchor(Anchor.LEFT_CENTER).offsetX(5.0).offsetY(5.0)

        chartView.setChart(cartesian)
    }

    private fun getCostData(): List<DataEntry> {
        return listOf(
            ValueDataEntry("Jan", 120000),
            ValueDataEntry("Feb", 110000),
            ValueDataEntry("Mar", 135000),
            ValueDataEntry("Apr", 125000),
            ValueDataEntry("May", 140000),
            ValueDataEntry("Jun", 138000)
        )
    }

    private fun getUsageData(): List<DataEntry> {
        return listOf(
            ValueDataEntry("Jan", 400),
            ValueDataEntry("Feb", 390),
            ValueDataEntry("Mar", 430),
            ValueDataEntry("Apr", 410),
            ValueDataEntry("May", 450),
            ValueDataEntry("Jun", 445)
        )
    }

//    private fun readData() {
//        database = FirebaseDatabase.getInstance().getReference("Power")
//        database.child("voltage").get().addOnSuccessListener {
//            if(it.exists()) {
//                val voltage: Float = it.value.toString().toFloat()
//                Toast.makeText(this, "Successful Voltage Read", Toast.LENGTH_SHORT).show()
//                binding.tvEnergyUsage.text = voltage.toString()
//            } else {
//                Toast.makeText(this, "Power/voltage path does not exist", Toast.LENGTH_SHORT).show()
//            }
//        }.addOnFailureListener {
//            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
//        }
//    }

}
