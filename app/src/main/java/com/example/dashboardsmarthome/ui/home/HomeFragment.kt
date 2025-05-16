package com.example.dashboardsmarthome.ui.home

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dashboardsmarthome.EnergyMonitoringActivity
import com.example.dashboardsmarthome.R
import com.example.dashboardsmarthome.databinding.BottomNavFrameBinding
import com.example.dashboardsmarthome.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val homeViewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val totalEnergyUsage = binding.cardViewTotalEnergyUsage
        val deviceEnergyUsage = binding.cardViewDeviceEnergyUsage

        totalEnergyUsage.setOnClickListener {
            val intent = Intent(requireContext(), EnergyMonitoringActivity::class.java)
            startActivity(intent)
        }

        deviceEnergyUsage.setOnClickListener {
            val intent = Intent(requireContext(), EnergyMonitoringActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}