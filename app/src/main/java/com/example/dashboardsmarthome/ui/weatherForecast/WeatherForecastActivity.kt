package com.example.dashboardsmarthome.ui.weatherForecast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.graphics.toColor
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dashboardsmarthome.BottomNavFrameActivity
import com.example.dashboardsmarthome.FireNotifActivity
import com.example.dashboardsmarthome.R
import com.example.dashboardsmarthome.databinding.ActivityFireNotifBinding
import com.example.dashboardsmarthome.databinding.ActivityWeatherForecastBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class WeatherForecastActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherForecastBinding
    private lateinit var viewModel: WeatherViewModel
    private lateinit var adapter: WeatherAdapter

    private lateinit var database: DatabaseReference
    private val channelId = "rain_alert_channel"
    private val notificationId = 103
    private var firebaseConnected = true
    private var lastDataReceivedTime: Long = System.currentTimeMillis()
    private var dummyAlarmTriggered = false

    private val handler = Handler(Looper.getMainLooper())
    private val dummyRunnable = object : Runnable {
        override fun run() {
            val timeSinceLastData = System.currentTimeMillis() - lastDataReceivedTime

            if (timeSinceLastData > 1_000 && !dummyAlarmTriggered) {
                showNotification("Peringatan Hujan", "Sensor lokal mendeteksi potensi hujan!")
                dummyAlarmTriggered = true
            }

            handler.postDelayed(this, 1_000)
        }
    }

    private val MAX_FORECAST_ITEMS_TO_DISPLAY = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        adapter = WeatherAdapter(emptyList())

        binding.recyclerViewForecast.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewForecast.adapter = adapter

        binding.topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, BottomNavFrameActivity::class.java)
            startActivity(intent)
            finish()
        }

        val adm4Code = intent.getStringExtra("adm4_code") ?: "34.04.07.2001"

        observeViewModel()
        viewModel.getWeatherForecast(adm4Code)

        database = FirebaseDatabase.getInstance().getReference("FireAlert")
        createNotificationChannel()

        database.child("detected").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val detected = snapshot.getValue(Boolean::class.java)
                lastDataReceivedTime = System.currentTimeMillis()
                dummyAlarmTriggered = false

                if (detected == true) {
                    showNotification("Peringatan Hujan!", "Sensor mendeteksi potensi Hujan!")
                }
            }


            override fun onCancelled(error: DatabaseError) {
                firebaseConnected = false
            }
        })

        handler.postDelayed(dummyRunnable, 1000)
    }

    private fun observeViewModel() {
        viewModel.weatherData.observe(this) { weatherResponse ->
            if (weatherResponse != null && weatherResponse.data.isNotEmpty()) {
                val weatherDataWrapper = weatherResponse.data.firstOrNull()

                if (weatherDataWrapper != null && weatherDataWrapper.cuaca.isNotEmpty()) {
                    val allFlattenedForecasts = weatherDataWrapper.cuaca.flatten()
                    Log.d("WeatherForecast", "Total item perkiraan cuaca (per 3 jam) tersedia: ${allFlattenedForecasts.size}")

                    val forecastsToDisplay = allFlattenedForecasts.take(MAX_FORECAST_ITEMS_TO_DISPLAY)
                    Log.d("WeatherForecast", "Jumlah item yang akan ditampilkan (setelah take): ${forecastsToDisplay.size}")

                    if (forecastsToDisplay.isNotEmpty()) {
                        adapter.updateData(forecastsToDisplay)
                        binding.recyclerViewForecast.visibility = View.VISIBLE
                        supportActionBar?.title = "${weatherResponse.lokasi.desa}, ${weatherResponse.lokasi.kotkab}"
                        binding.textEmptyMessage.visibility = View.GONE
                    } else {
                        showEmptyMessage("Tidak ada data cuaca yang relevan tersedia.")
                    }
                } else {
                    showEmptyMessage("Data perkiraan cuaca tidak ditemukan dalam respons.")
                }
            } else {
                showEmptyMessage("Gagal memuat data cuaca atau respons kosong.")
            }
        }
    }

    private fun showEmptyMessage(message: String) {
        binding.recyclerViewForecast.visibility = View.GONE
        binding.textEmptyMessage.visibility = View.VISIBLE
        binding.textEmptyMessage.text = message
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(dummyRunnable)
    }

    private fun showNotification(title: String, message: String) {
        val intent = Intent(this, WeatherForecastActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val fullScreenIntent = Intent(this, WeatherForecastActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.rain)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVibrate(longArrayOf(0, 500, 1000))
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Rain Alert Channel"
            val description = "Channel untuk notifikasi hujan"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                this.description = description
            }

            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(0, 500, 1000)
            channel.setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
            )

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}