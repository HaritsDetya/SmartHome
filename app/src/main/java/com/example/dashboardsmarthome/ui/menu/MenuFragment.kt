package com.example.dashboardsmarthome.ui.menu

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dashboardsmarthome.EnergyMonitoringActivity
import com.example.dashboardsmarthome.R
import com.example.dashboardsmarthome.databinding.FragmentHomeBinding
import com.example.dashboardsmarthome.databinding.FragmentMenuBinding
import com.example.dashboardsmarthome.ui.home.HomeFragment
import com.example.dashboardsmarthome.ui.home.HomeViewModel

class MenuFragment : Fragment() {

    companion object {
        fun newInstance() = MenuFragment()
    }

    private val viewModel: MenuViewModel by viewModels()

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}