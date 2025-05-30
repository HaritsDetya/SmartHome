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
import androidx.lifecycle.ViewModelProvider
import com.example.dashboardsmarthome.EnergyMonitoringActivity
import com.example.dashboardsmarthome.R
import com.example.dashboardsmarthome.ui.weatherForecast.WeatherForecastActivity
import com.example.dashboardsmarthome.databinding.FragmentHomeBinding
import com.example.dashboardsmarthome.ui.weatherForecast.WeatherViewModel
import com.google.android.material.card.MaterialCardView

class HomeFragment : Fragment() {

    private lateinit var weatherViewModel: WeatherViewModel

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
            startActivity(intent)
        }

        totalEnergyUsage.setOnClickListener {
            val intent = Intent(requireContext(), EnergyMonitoringActivity::class.java)
            startActivity(intent)
        }

        deviceEnergyUsage.setOnClickListener {
            val intent = Intent(requireContext(), EnergyMonitoringActivity::class.java)
            startActivity(intent)
        }

        val isOnMap = mutableMapOf<Int, Boolean>()

        isOnMap[R.id.cardView_home_btn] = false
        isOnMap[R.id.cardView_nightMode_btn] = false
        isOnMap[R.id.cardView_awayMode_btn] = false

        val cardHome = view.findViewById<MaterialCardView>(R.id.cardView_home_btn)
        val cardNight = view.findViewById<MaterialCardView>(R.id.cardView_nightMode_btn)
        val cardAway = view.findViewById<MaterialCardView>(R.id.cardView_awayMode_btn)

        initializeCardAppearance(cardHome, R.id.icon_home_btn, R.id.title_home_btn, R.id.text_home_btn, requireContext(), false)
        initializeCardAppearance(cardNight, R.id.icon_nightMode_btn, R.id.title_nightMode_btn, R.id.text_nightMode_btn, requireContext(), false)
        initializeCardAppearance(cardAway, R.id.icon_awayMode_btn, R.id.title_awayMode_btn, R.id.text_awayMode_btn, requireContext(), false)

        cardHome.setOnClickListener {
            toggleCardState(
                cardView = cardHome,
                iconId = R.id.icon_home_btn,
                titleId = R.id.title_home_btn,
                subtitleId = R.id.text_home_btn,
                context = requireContext(),
                isOnMap = isOnMap
            )
        }

        cardNight.setOnClickListener {
            toggleCardState(
                cardView = cardNight,
                iconId = R.id.icon_nightMode_btn,
                titleId = R.id.title_nightMode_btn,
                subtitleId = R.id.text_nightMode_btn,
                context = requireContext(),
                isOnMap = isOnMap
            )
        }

        cardAway.setOnClickListener {
            toggleCardState(
                cardView = cardAway,
                iconId = R.id.icon_awayMode_btn,
                titleId = R.id.title_awayMode_btn,
                subtitleId = R.id.text_awayMode_btn,
                context = requireContext(),
                isOnMap = isOnMap
            )
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

    private fun toggleCardState(
        cardView: MaterialCardView,
        iconId: Int,
        titleId: Int,
        subtitleId: Int,
        context: Context,
        isOnMap: MutableMap<Int, Boolean>
    ) {
        val cardId = cardView.id
        val currentState = isOnMap[cardId] == true

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

        isOnMap[cardId] = !currentState
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