package com.example.dashboardsmarthome

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.dashboardsmarthome.databinding.ActivityFireNotifBinding
import com.google.firebase.database.*

class FireNotifActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityFireNotifBinding
    private val channelId = "fire_alert_channel"
    private val notificationId = 101
    private var firebaseConnected = true
    private var lastDataReceivedTime: Long = System.currentTimeMillis()
    private var dummyAlarmTriggered = false

    private val handler = Handler(Looper.getMainLooper())
    private val dummyRunnable = object : Runnable {
        override fun run() {
            val timeSinceLastData = System.currentTimeMillis() - lastDataReceivedTime

            if (timeSinceLastData > 1_000 && !dummyAlarmTriggered) {
                showNotification("Peringatan Kebakaran", "Sensor lokal mendeteksi potensi kebakaran!")
                dummyAlarmTriggered = true
            }

            handler.postDelayed(this, 1_000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFireNotifBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, BottomNavFrameActivity::class.java)
            startActivity(intent)
            finish()
        }

        database = FirebaseDatabase.getInstance().getReference("FireAlert")
        createNotificationChannel()

        database.child("detected").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val detected = snapshot.getValue(Boolean::class.java)
                lastDataReceivedTime = System.currentTimeMillis()
                dummyAlarmTriggered = false

                if (detected == true) {
                    showNotification("Peringatan Kebakaran!", "Sensor mendeteksi potensi kebakaran!")
                }
            }


            override fun onCancelled(error: DatabaseError) {
                firebaseConnected = false
            }
        })

        handler.postDelayed(dummyRunnable, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(dummyRunnable)
    }

    private fun showNotification(title: String, message: String) {
        val intent = Intent(this, FireNotifActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val fullScreenIntent = Intent(this, FireNotifActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.fire_solid)
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
            val name = "Fire Alert Channel"
            val description = "Channel untuk notifikasi kebakaran"
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
