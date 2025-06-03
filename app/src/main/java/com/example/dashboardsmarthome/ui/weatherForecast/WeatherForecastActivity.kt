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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dashboardsmarthome.BottomNavFrameActivity
import com.example.dashboardsmarthome.EWSViewModel
import com.example.dashboardsmarthome.R
import com.example.dashboardsmarthome.dataAPI.NotificationHistoryManager
import com.example.dashboardsmarthome.databinding.ActivityWeatherForecastBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class WeatherForecastActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherForecastBinding
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var ewsViewModel: EWSViewModel
    private lateinit var adapter: WeatherAdapter

    private lateinit var database: DatabaseReference
    private val channelId = "weather_alert_channel"
    private val notificationId = 103

//    private var firebaseConnected = true
//    private var lastDataReceivedTime: Long = System.currentTimeMillis()
//    private var dummyAlarmTriggered = false
//    private var latestWeatherDescription: String? = null
//    private var lastNotifiedWeatherDescription: String? = null

    private val MAX_FORECAST_ITEMS_TO_DISPLAY = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()
        NotificationHistoryManager.init(applicationContext)

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        ewsViewModel = ViewModelProvider(this)[EWSViewModel::class.java]

        adapter = WeatherAdapter(emptyList())

        binding.recyclerViewForecast.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewForecast.adapter = adapter

        if (intent.getBooleanExtra("trigger_weather_notification", false)) {
            val weatherDesc = intent.getStringExtra("weather_description") ?: "Tidak diketahui"
            sendWeatherNotification(weatherDesc)
            finish()
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleBold)

        binding.topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, BottomNavFrameActivity::class.java)
            intent.putExtra("start_destination", "ews")
            startActivity(intent)
            finish()
        }

        val adm4Code = intent.getStringExtra("adm4_code") ?: "34.04.07.2001"

        weatherViewModel.weatherData.observe(this) { weatherResponse ->
            if (weatherResponse != null && weatherResponse.data.isNotEmpty()) {
                val weatherDataWrapper = weatherResponse.data.firstOrNull()

                if (weatherDataWrapper != null && weatherDataWrapper.cuaca.isNotEmpty()) {
                    val allFlattenedForecasts = weatherDataWrapper.cuaca.flatten()
                    val forecastsToDisplay = allFlattenedForecasts.take(MAX_FORECAST_ITEMS_TO_DISPLAY)

                    if (forecastsToDisplay.isNotEmpty()) {
                        adapter.updateData(forecastsToDisplay)
                        binding.recyclerViewForecast.visibility = View.VISIBLE
                        supportActionBar?.title = "${weatherResponse.lokasi.desa}, ${weatherResponse.lokasi.kotkab}"
                        binding.textEmptyMessage.visibility = View.GONE

                        // Trigger notifikasi cuaca melalui EWSViewModel
                        val latestWeatherDescription = forecastsToDisplay.first().weatherDesc
                        ewsViewModel.getWeatherForecastAndCheckForAlert(adm4Code) // Memanggil ViewModel untuk cek & picu notif
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

        ewsViewModel.weatherNotificationEvent.observe(this) { weatherDesc ->
            sendWeatherNotification(weatherDesc)
        }

        weatherViewModel.getWeatherForecast(adm4Code)

//        observeViewModel()
//        viewModel.getWeatherForecast(adm4Code)
//
//        database = FirebaseDatabase.getInstance().getReference("WeatherAlert")
//
//        database.child("detected").addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val detected = snapshot.getValue(Boolean::class.java)
//                lastDataReceivedTime = System.currentTimeMillis()
//                dummyAlarmTriggered = false
//
//                if (detected == true) {
//                    latestWeatherDescription?.let {
//                        sendWeatherNotification(it)
//                    } ?: run {
//                        Log.w("WeatherForecast", "Detected true from Firebase but latestWeatherDescription is null.")
//                    }
//                }
//            }
//
//
//            override fun onCancelled(error: DatabaseError) {
//                firebaseConnected = false
//                Log.e("WeatherForecast", "Firebase connection cancelled: ${error.message}")
//            }
//        })

//        handler.postDelayed(dummyRunnable, 1000)
    }

//    private fun observeViewModel() {
//        viewModel.weatherData.observe(this) { weatherResponse ->
//            if (weatherResponse != null && weatherResponse.data.isNotEmpty()) {
//                val weatherDataWrapper = weatherResponse.data.firstOrNull()
//
//                if (weatherDataWrapper != null && weatherDataWrapper.cuaca.isNotEmpty()) {
//                    val allFlattenedForecasts = weatherDataWrapper.cuaca.flatten()
//                    Log.d("WeatherForecast", "Total item perkiraan cuaca (per 3 jam) tersedia: ${allFlattenedForecasts.size}")
//
//                    val forecastsToDisplay = allFlattenedForecasts.take(MAX_FORECAST_ITEMS_TO_DISPLAY)
//                    Log.d("WeatherForecast", "Jumlah item yang akan ditampilkan (setelah take): ${forecastsToDisplay.size}")
//
//                    if (forecastsToDisplay.isNotEmpty()) {
//                        adapter.updateData(forecastsToDisplay)
//
//                        latestWeatherDescription = forecastsToDisplay.first().weatherDesc
//
//                        sendWeatherNotification(latestWeatherDescription)
//
//                        binding.recyclerViewForecast.visibility = View.VISIBLE
//                        supportActionBar?.title = "${weatherResponse.lokasi.desa}, ${weatherResponse.lokasi.kotkab}"
//                        binding.textEmptyMessage.visibility = View.GONE
//                    } else {
//                        showEmptyMessage("Tidak ada data cuaca yang relevan tersedia.")
//                    }
//                } else {
//                    showEmptyMessage("Data perkiraan cuaca tidak ditemukan dalam respons.")
//                }
//            } else {
//                showEmptyMessage("Gagal memuat data cuaca atau respons kosong.")
//            }
//        }
//    }

    private fun sendWeatherNotification(weatherDesc: String?) {
//        if (weatherDesc == null || weatherDesc == lastNotifiedWeatherDescription) {
//            return
//        }

        val title: String
        val message: String
        val smallIconResId: Int

        when (weatherDesc?.lowercase(Locale.ROOT)) {
            "cerah", "cerah berawan" -> {
                title = "Cuaca Cerah!"
                message = "Cuaca saat ini sedang cerah, semoga hari Anda cerah selalu!"
                smallIconResId = R.drawable.sun
            }
            "berawan", "berawan tebal" -> {
                title = "Cuaca Berawan"
                message = "Cuaca saat ini sedang berawan, rasakan angin segar saat beraktifitas."
                smallIconResId = R.drawable.cloud
            }
            "hujan ringan", "hujan sedang", "hujan lebat", "hujan petir" -> {
                title = "Peringatan Hujan!"
                message = "BMKG memperkirakan potensi hujan hari ini di wilayah Anda."
                smallIconResId = R.drawable.rain
            }
            "kabut" -> {
                title = "Waspada Kabut"
                message = "Cuaca berkabut, harap berhati-hati saat berkendara."
                smallIconResId = R.drawable.cloud_fog
            }
            "asap", "polusi" -> { // Asumsi jika ada deskripsi polusi
                title = "Kualitas Udara"
                message = "Kualitas udara kurang baik, disarankan mengurangi aktivitas di luar ruangan."
                smallIconResId = R.drawable.cloud_fog
            }
            else -> {
                title = "Informasi Cuaca"
                message = "Cuaca saat ini: $weatherDesc."
                smallIconResId = R.drawable.cloud_drizzle
            }
        }

//        lastNotifiedWeatherDescription = weatherDesc
        showNotification(title, message, smallIconResId)
    }

    private fun showEmptyMessage(message: String) {
        binding.recyclerViewForecast.visibility = View.GONE
        binding.textEmptyMessage.visibility = View.VISIBLE
        binding.textEmptyMessage.text = message
    }

    private fun showNotification(title: String, message: String, smallIconResId: Int) {
        val intent = Intent(this, WeatherForecastActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val fullScreenIntent = Intent(this, WeatherForecastActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(smallIconResId)
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

        NotificationHistoryManager.addNotificationToHistory("Cuaca", message)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Weather Alert Channel"
            val description = "Channel untuk notifikasi perkiraan cuaca"
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