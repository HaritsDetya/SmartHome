package com.example.dashboardsmarthome.ui.home

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.dashboardsmarthome.EnergyMonitoringActivity
import com.example.dashboardsmarthome.R
import com.example.dashboardsmarthome.ui.weatherForecast.WeatherForecastActivity
import com.example.dashboardsmarthome.databinding.FragmentHomeBinding
import com.example.dashboardsmarthome.ui.weatherForecast.WeatherViewModel
import com.google.android.material.card.MaterialCardView

class HomeFragment : Fragment() {

    private lateinit var weatherViewModel: WeatherViewModel
    private val homeViewModel: HomeViewModel by activityViewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val NUMBER_OF_HOURLY_FORECAST_ICONS = 7

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        val adm4Code = "34.04.07.2001"
        weatherViewModel.getWeatherForecast(adm4Code)

        weatherViewModel.weatherData.observe(viewLifecycleOwner) { weatherResponse ->
            if (weatherResponse != null && weatherResponse.data.isNotEmpty()) {
                val weatherDataWrapper = weatherResponse.data.firstOrNull()

                if (weatherDataWrapper != null && weatherDataWrapper.cuaca.isNotEmpty()) {
                    val lokasiGlobal = weatherResponse.lokasi
                    val allFlattenedForecasts = weatherDataWrapper.cuaca.flatten()

                    if (allFlattenedForecasts.isNotEmpty()) {
                        val currentWeather = allFlattenedForecasts.first()

                        binding.mainWeatherText.text = "${currentWeather.t}°C"
                        binding.mainWeatherTextSub.text = "${lokasiGlobal.desa}, ${lokasiGlobal.kotkab}"

                        val iconResId = getWeatherIconResId(currentWeather.weatherDesc)
                        binding.mainWeatherIcon.setImageResource(iconResId)
                    }

                    val icons = listOf(
                        binding.day1WeatherIcon,
                        binding.day2WeatherIcon,
                        binding.day3WeatherIcon,
                        binding.day4WeatherIcon,
                        binding.day5WeatherIcon,
                        binding.day6WeatherIcon,
                        binding.day7WeatherIcon
                    )

                    val temperatureTexts = listOf(
                        binding.day1TemperatureText,
                        binding.day2TemperatureText,
                        binding.day3TemperatureText,
                        binding.day4TemperatureText,
                        binding.day5TemperatureText,
                        binding.day6TemperatureText,
                        binding.day7TemperatureText
                    )

                    val forecastsForIcons = allFlattenedForecasts.take(NUMBER_OF_HOURLY_FORECAST_ICONS)

                    for (i in forecastsForIcons.indices) {
                        val forecastItem = forecastsForIcons[i]
                        val iconResId = getWeatherIconResId(forecastItem.weatherDesc)
                        icons[i].setImageResource(iconResId)
                        temperatureTexts[i].text = "${forecastItem.t}°C"
                    }

                    for (i in forecastsForIcons.size until icons.size) {
                        icons[i].setImageResource(R.drawable.cloud)
                        temperatureTexts[i].text = "--°C"
                    }


                } else {
                    Log.e("HomeFragment", "Weather data wrapper or cuaca list is empty.")
                    binding.mainWeatherText.text = "?"
                    binding.mainWeatherTextSub.text = "Error loading weather"
                    binding.mainWeatherIcon.setImageResource(R.drawable.cloud)
                    val icons = listOf(
                        binding.day1WeatherIcon, binding.day2WeatherIcon, binding.day3WeatherIcon,
                        binding.day4WeatherIcon, binding.day5WeatherIcon, binding.day6WeatherIcon, binding.day7WeatherIcon
                    )
                    icons.forEach { it.setImageResource(R.drawable.cloud) }
                }

            } else {
                Log.e("HomeFragment", "Failed to load weather data or data is null.")
                binding.mainWeatherText.text = "?"
                binding.mainWeatherTextSub.text = "Error loading weather"
                binding.mainWeatherIcon.setImageResource(R.drawable.cloud)
                val icons = listOf(
                    binding.day1WeatherIcon, binding.day2WeatherIcon, binding.day3WeatherIcon,
                    binding.day4WeatherIcon, binding.day5WeatherIcon, binding.day6WeatherIcon, binding.day7WeatherIcon
                )
                icons.forEach { it.setImageResource(R.drawable.cloud) }
            }
        }

        homeViewModel.powerData.observe(viewLifecycleOwner) { powerData ->
            if (powerData != null) {
                binding.textEnergyCost.text = "${powerData.energy?.let { String.format("%.5f", it) } ?: "--"} Wh"
                binding.valueTotalEnergyUsage.text = "${powerData.power?.let { String.format("%.2f", it) } ?: "--"} W"
                binding.valueDeviceEnergyUsage.text = "${powerData.voltage?.let { String.format("%.3f", it) } ?: "--"} V"
            } else {
                binding.textEnergyCost.text = "-- Wh"
                binding.valueTotalEnergyUsage.text = "-- W"
                binding.valueDeviceEnergyUsage.text = "-- V"
                Log.e("HomeFragment", "Power data in HomeFragment is null. This indicates a deeper issue.")
            }
        }

        homeViewModel.kontrolData.observe(viewLifecycleOwner) { kontrolData ->
            val relay1Status = kontrolData?.relay1 == true
            val relay2Status = kontrolData?.relay2 == true

            initializeCardAppearance(binding.cardViewHomeBtn, R.id.icon_home_btn, R.id.title_home_btn, R.id.text_home_btn, requireContext(), relay1Status)
            initializeCardAppearance(binding.cardViewNightModeBtn, R.id.icon_nightMode_btn, R.id.title_nightMode_btn, R.id.text_nightMode_btn, requireContext(), relay2Status)
            initializeCardAppearance(binding.cardViewAwayModeBtn, R.id.icon_awayMode_btn, R.id.title_awayMode_btn, R.id.text_awayMode_btn, requireContext(), false)
        }

        binding.cardViewWeather.setOnClickListener {
            val intent = Intent(requireContext(), WeatherForecastActivity::class.java)
            intent.putExtra("adm4_code", adm4Code)
            startActivity(intent)
        }

        val energyCost = binding.cardViewEnergyCost
        val totalEnergyUsage = binding.cardViewTotalEnergyUsage
        val deviceEnergyUsage = binding.cardViewDeviceEnergyUsage

        energyCost.setOnClickListener {
            val intent = Intent(requireContext(), EnergyMonitoringActivity::class.java)
            homeViewModel.powerData.value?.let {
                intent.putExtra("power_data", it)
            }
            startActivity(intent)
        }

        totalEnergyUsage.setOnClickListener {
            val intent = Intent(requireContext(), EnergyMonitoringActivity::class.java)
            homeViewModel.powerData.value?.let {
                intent.putExtra("power_data", it)
            }
            startActivity(intent)
        }

        deviceEnergyUsage.setOnClickListener {
            val intent = Intent(requireContext(), EnergyMonitoringActivity::class.java)
            homeViewModel.powerData.value?.let {
                intent.putExtra("power_data", it)
            }
            startActivity(intent)
        }

        binding.cardViewHomeBtn.setOnClickListener {
            val currentRelay1Status = homeViewModel.kontrolData.value?.relay1 == true
            homeViewModel.updateRelayStatus("relay1", !currentRelay1Status)
        }

        binding.cardViewNightModeBtn.setOnClickListener {
            val currentRelay2Status = homeViewModel.kontrolData.value?.relay2 == true
            homeViewModel.updateRelayStatus("relay2", !currentRelay2Status)
        }

        binding.cardViewAwayModeBtn.setOnClickListener {
            val cardId = binding.cardViewAwayModeBtn.id
            val currentState = (binding.cardViewAwayModeBtn.tag as? Boolean) == true
            toggleCardStateVisualOnly(
                cardView = binding.cardViewAwayModeBtn,
                iconId = R.id.icon_awayMode_btn,
                titleId = R.id.title_awayMode_btn,
                subtitleId = R.id.text_awayMode_btn,
                context = requireContext(),
                currentState = currentState
            )
            binding.cardViewAwayModeBtn.tag = !currentState
        }
    }

    private fun getWeatherIconResId(weatherDesc: String): Int {
        return when {
            weatherDesc.contains("Cerah", ignoreCase = true) -> R.drawable.sun
            weatherDesc.contains("Berawan", ignoreCase = true) || weatherDesc.contains("Mendung", ignoreCase = true) -> R.drawable.cloud
            weatherDesc.contains("Hujan Ringan", ignoreCase = true) -> R.drawable.rain
            weatherDesc.contains("Hujan Sedang", ignoreCase = true) || weatherDesc.contains("Hujan Lebat", ignoreCase = true) -> R.drawable.rain
            weatherDesc.contains("Hujan Petir", ignoreCase = true) || weatherDesc.contains("Guntur", ignoreCase = true) -> R.drawable.storm
            weatherDesc.contains("Kabut", ignoreCase = true) -> R.drawable.cloud_fog
            weatherDesc.contains("Asap", ignoreCase = true) -> R.drawable.cloud_fog
            weatherDesc.contains("Cerah Berawan", ignoreCase = true) -> R.drawable.cloud_drizzle
            else -> R.drawable.cloud
        }
    }

    private fun toggleCardStateVisualOnly(
        cardView: MaterialCardView,
        iconId: Int,
        titleId: Int,
        subtitleId: Int,
        context: Context,
        currentState: Boolean
    ) {
        val fromColor = ContextCompat.getColor(context, if (currentState) R.color.card_on else R.color.card_off)
        val toColor = ContextCompat.getColor(context, if (currentState) R.color.card_off else R.color.card_on)

        ValueAnimator.ofArgb(fromColor, toColor).apply {
            duration = 300
            addUpdateListener { animator ->
                cardView.setCardBackgroundColor(animator.animatedValue as Int)
            }
            start()
        }

        val icon = cardView.findViewById<AppCompatImageView>(iconId)
        val title = cardView.findViewById<TextView>(titleId)
        val subtitle = cardView.findViewById<TextView>(subtitleId)

        val textColor = if (currentState) R.color.text_main_color else R.color.black
        val iconTint = if (currentState) R.color.text_main_color else R.color.black

        icon.setColorFilter(ContextCompat.getColor(context, iconTint), android.graphics.PorterDuff.Mode.SRC_IN)
        title.setTextColor(ContextCompat.getColor(context, textColor))
        subtitle.setTextColor(ContextCompat.getColor(context, textColor))

    }

    private fun initializeCardAppearance(
        cardView: MaterialCardView,
        iconId: Int,
        titleId: Int,
        subtitleId: Int,
        context: Context,
        isOn: Boolean
    ) {
        val bgColor = ContextCompat.getColor(context, if (isOn) R.color.card_on else R.color.card_off)
        val textColor = ContextCompat.getColor(context, if (isOn) R.color.black else R.color.text_main_color)
        val iconTint = ContextCompat.getColor(context, if (isOn) R.color.black else R.color.text_main_color)

        cardView.setCardBackgroundColor(bgColor)

        val icon = cardView.findViewById<AppCompatImageView>(iconId)
        val title = cardView.findViewById<TextView>(titleId)
        val subtitle = cardView.findViewById<TextView>(subtitleId)

        icon.setColorFilter(iconTint, android.graphics.PorterDuff.Mode.SRC_IN)
        title.setTextColor(textColor)
        subtitle.setTextColor(textColor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}