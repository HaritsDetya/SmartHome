package com.example.dashboardsmarthome.ui.virtual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.dashboardsmarthome.R
import com.example.dashboardsmarthome.databinding.FragmentMenuBinding
import com.example.dashboardsmarthome.databinding.FragmentVirtualBinding
import com.example.dashboardsmarthome.ui.virtual.VirtualViewModel

class VirtualFragment : Fragment() {

    companion object {
        fun newInstance() = VirtualFragment()
    }

    private val viewModel: VirtualViewModel by viewModels()

    private var _binding: FragmentVirtualBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVirtualBinding.inflate(inflater, container, false)
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