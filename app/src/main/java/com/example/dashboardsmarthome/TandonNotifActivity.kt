package com.example.dashboardsmarthome

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.dashboardsmarthome.dataAPI.NotificationHistoryManager
import com.example.dashboardsmarthome.databinding.ActivityTandonNotifBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TandonNotifActivity : AppCompatActivity() {
//    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityTandonNotifBinding
    private lateinit var ewsViewModel: EWSViewModel

    private val channelId = "water_tank_alert_channel"
    private val notificationId = 102

    private var navigationDestination: String = "menu"

//    private var firebaseConnected = true
//    private var lastDataReceivedTime: Long = System.currentTimeMillis()
//    private var dummyAlarmTriggered = false

//    private val handler = Handler(Looper.getMainLooper())
//    private val dummyRunnable = object : Runnable {
//        override fun run() {
//            val timeSinceLastData = System.currentTimeMillis() - lastDataReceivedTime
//
//            if (timeSinceLastData > 1_000 && !dummyAlarmTriggered) {
//                showNotification("Tandon Penuh", "Sensor lokal mendeteksi tandon penuh!")
//                dummyAlarmTriggered = true
//            }
//
//            handler.postDelayed(this, 1_000)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTandonNotifBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()
        NotificationHistoryManager.init(applicationContext)

        ewsViewModel = ViewModelProvider(this)[EWSViewModel::class.java]

//        if (intent.getBooleanExtra("trigger_water_notification", false)) {
////            showNotification("Tandon Penuh!", "Tandon air dipicu secara manual.")
//            ewsViewModel.triggerWaterTankAlert(true)
//            finish()
//            return
//        }

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleBold)

        binding.topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, BottomNavFrameActivity::class.java)
            intent.putExtra("start_destination", navigationDestination)
            startActivity(intent)
            finish()
        }

//        database = FirebaseDatabase.getInstance().getReference("WaterTankAlert")
//
//        database.child("detected").addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val detected = snapshot.getValue(Boolean::class.java)
//                lastDataReceivedTime = System.currentTimeMillis()
//                dummyAlarmTriggered = false
//
//                if (detected == true) {
//                    showNotification("Peringatan Tandon Penuh!", "Sensor mendeteksi tandon penuh!")
//                }
//            }
//
//
//            override fun onCancelled(error: DatabaseError) {
//                firebaseConnected = false
//            }
//        })
//
//        handler.postDelayed(dummyRunnable, 1000)

        ewsViewModel.waterDetected.observe(this) { detected ->
            binding.triggerSwitch.isChecked = detected
            binding.tvTandonStatus.text = if (detected) "Status: Tandon Penuh!" else "Status: Normal"

            binding.main.setBackgroundColor(ContextCompat.getColor(this,
                if (detected) R.color.alert_danger else R.color.safe_status))

            navigationDestination = if (detected) "ews" else "menu"
        }

        binding.triggerSwitch.setOnCheckedChangeListener { _, isChecked ->
            ewsViewModel.triggerWaterTankAlert(isChecked)
        }

        ewsViewModel.waterNotificationEvent.observe(this) { message ->
            showNotification("Peringatan Tandon Penuh!", message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        handler.removeCallbacks(dummyRunnable)
    }

    private fun showNotification(title: String, message: String) {
        val intent = Intent(this, TandonNotifActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val fullScreenIntent = Intent(this, TandonNotifActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.water_drop)
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

        NotificationHistoryManager.addNotificationToHistory("Tandon Air", message)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Water Alert Channel"
            val description = "Channel untuk notifikasi tandon air"
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