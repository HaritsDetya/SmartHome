package com.example.dashboardsmarthome.ui.virtual

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.dashboardsmarthome.R
import com.example.dashboardsmarthome.databinding.FragmentVirtualBinding
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VirtualFragment : Fragment() {

    companion object {
        fun newInstance() = VirtualFragment()
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

        handler = Handler(Looper.getMainLooper())
        updateTimeRunnable = object : Runnable {
            override fun run() {
                updateDateTime()
                handler.postDelayed(this, 1000)
            }
        }

        handler.post(updateTimeRunnable)

        setupSecurityLogs()

        setupLoremButtons()
    }

    private fun updateDateTime() {
        val currentTime = System.currentTimeMillis()

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        binding.timeText.text = timeFormat.format(Date(currentTime))

        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        binding.dateText.text = dateFormat.format(Date(currentTime))
    }

    private fun setupSecurityLogs() {

        val log1TextView = binding.securityLogContainer.findViewById<TextView>(R.id.log_text_1)
        val log2TextView = binding.securityLogContainer.findViewById<TextView>(R.id.log_text_2)
        val log3TextView = binding.securityLogContainer.findViewById<TextView>(R.id.log_text_3)
        val log4TextView = binding.securityLogContainer.findViewById<TextView>(R.id.log_text_4)

        log1TextView?.text = "08:30 AM - Door sensor triggered"
        log2TextView?.text = "10:15 AM - Motion detected in living room"
        log3TextView?.text = "12:00 PM - System check completed"
        log4TextView?.text = "12:00 PM - System check completed"
    }

    private fun setupLoremButtons() {
        binding.btn1.setOnClickListener {
            binding.timeText.text = "Button 1!"
        }

        binding.btn2.setOnClickListener {
            binding.dateText.text = "Button 2!"
        }

        binding.btn3.setOnClickListener {
            binding.timeText.text = "Button 3!"
        }

        binding.btn4.setOnClickListener {
            binding.dateText.text = "Button 4!"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateTimeRunnable)
        _binding = null
    }
}