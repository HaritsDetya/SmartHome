package com.example.dashboardsmarthome.ui.virtual

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.dashboardsmarthome.FireNotifActivity
import com.example.dashboardsmarthome.R
import com.example.dashboardsmarthome.TandonNotifActivity
import com.example.dashboardsmarthome.dataAPI.NotificationHistoryManager
import com.example.dashboardsmarthome.databinding.FragmentVirtualBinding
import com.example.dashboardsmarthome.ui.weatherForecast.WeatherForecastActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class NotificationLog(
    val timestamp: Long,
    val type: String,
    val message: String
)

class VirtualFragment : Fragment() {

    companion object {
        fun newInstance() = VirtualFragment()
        private const val NOTIFICATION_LOGS_KEY = "notification_logs"
        private const val MAX_LOG_ENTRIES = 4
    }

    private val viewModel: VirtualViewModel by viewModels()

    private var _binding: FragmentVirtualBinding? = null
    private val binding get() = _binding!!

    private lateinit var handler: Handler
    private lateinit var updateTimeRunnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVirtualBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NotificationHistoryManager.init(requireContext().applicationContext)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                NotificationHistoryManager.notificationLogsFlow.collect { logs ->
                    updateSecurityLogsUI(logs)
                }
            }
        }

        handler = Handler(Looper.getMainLooper())
        updateTimeRunnable = object : Runnable {
            override fun run() {
                updateDateTime()
                handler.postDelayed(this, 1000)
            }
        }

        handler.post(updateTimeRunnable)

        setupButtons()
    }

    private fun updateDateTime() {
        val currentTime = System.currentTimeMillis()

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        binding.timeText.text = timeFormat.format(Date(currentTime))

        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        binding.dateText.text = dateFormat.format(Date(currentTime))
    }

    private fun updateSecurityLogsUI(logs: List<NotificationLog>) {
        val logTextViews = listOf(
            binding.securityLogContainer.findViewById<TextView>(R.id.log_text_1),
            binding.securityLogContainer.findViewById<TextView>(R.id.log_text_2),
            binding.securityLogContainer.findViewById<TextView>(R.id.log_text_3),
            binding.securityLogContainer.findViewById<TextView>(R.id.log_text_4)
        )

        logTextViews.forEach { it?.text = "" }

        logs.take(MAX_LOG_ENTRIES).forEachIndexed { index, log ->
            val timestampFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val formattedTime = timestampFormat.format(Date(log.timestamp))
            logTextViews[index]?.text = "$formattedTime - [${log.type.capitalize(Locale.ROOT)}] ${log.message}"
        }
    }

    private fun setupButtons() {
        val buttonViews = listOf(binding.btn1, binding.btn2, binding.btn3, binding.btn4)

        buttonViews.forEach { button ->
            button.setOnClickListener {
                val scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.9f, 1f)
                val scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.9f, 1f)
                val animatorSet = AnimatorSet()
                animatorSet.playTogether(scaleX, scaleY)
                animatorSet.duration = 200
                animatorSet.interpolator = AccelerateDecelerateInterpolator()
                animatorSet.start()

                when (button.id) {
                    R.id.btn1 -> triggerWeatherNotification("cerah")
                    R.id.btn2 -> triggerFireAlertNotification()
                    R.id.btn3 -> triggerWaterTankAlertNotification()
                    R.id.btn4 -> NotificationHistoryManager.clearNotificationHistory()
                }
            }
        }
    }

    private fun triggerWeatherNotification(weatherDesc: String) {
        val intent = Intent(requireContext(), WeatherForecastActivity::class.java).apply {
            putExtra("trigger_weather_notification", true)
            putExtra("weather_description", weatherDesc)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        requireContext().startActivity(intent)
    }

    private fun triggerFireAlertNotification() {
        val intent = Intent(requireContext(), FireNotifActivity::class.java).apply {
            putExtra("trigger_fire_notification", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        requireContext().startActivity(intent)
    }

    private fun triggerWaterTankAlertNotification() {
        val intent = Intent(requireContext(), TandonNotifActivity::class.java).apply {
            putExtra("trigger_water_notification", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        requireContext().startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateTimeRunnable)
        _binding = null
    }
}