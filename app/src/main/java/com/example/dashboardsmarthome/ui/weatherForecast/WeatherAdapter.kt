package com.example.dashboardsmarthome.ui.weatherForecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dashboardsmarthome.dataAPI.Cuaca
import com.example.dashboardsmarthome.databinding.ItemForecastBinding
import com.example.dashboardsmarthome.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherAdapter(private var listCuaca: List<Cuaca>) :
    RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemForecastBinding) : RecyclerView.ViewHolder(binding.root) {
        val waktu = binding.weatherDate
        val suhu = binding.weatherSuhu
        val cuaca = binding.weatherDesc
        val gambar = binding.weatherIcon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cuaca = listCuaca[position]
        cuaca.localDatetime?.let { dateTimeString ->
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date: Date? = inputFormat.parse(dateTimeString)

                if (date != null) {
                    val outputFormat = SimpleDateFormat("HH.mm - dd MMMM yyyy", Locale("in", "ID"))
                    holder.waktu.text = outputFormat.format(date)
                } else {
                    holder.waktu.text = "Tanggal Tidak Valid"
                }
            } catch (e: Exception) {
                holder.waktu.text = "Error Format Tanggal"
                e.printStackTrace()
            }
        } ?: run {
            holder.waktu.text = "Tanggal Tidak Tersedia"
        }

        holder.suhu.text = "${cuaca.t}°C"
        holder.cuaca.text = cuaca.weatherDesc

        val iconResId = getWeatherIconResId(cuaca.weatherDesc)
        holder.gambar.setImageResource(iconResId)

    }

    override fun getItemCount(): Int = listCuaca.size

    fun updateData(newListCuaca: List<Cuaca>) {
        listCuaca = newListCuaca
        notifyDataSetChanged()
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
}