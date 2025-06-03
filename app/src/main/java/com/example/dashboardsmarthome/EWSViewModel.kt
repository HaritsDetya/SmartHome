package com.example.dashboardsmarthome

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dashboardsmarthome.dataAPI.ApiService
import com.example.dashboardsmarthome.dataAPI.RetrofitClient
import com.example.dashboardsmarthome.dataAPI.WeatherResponse
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EWSViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "EWSViewModel"
    }

    private val _fireNotificationEvent = SingleLiveEvent<String>()
    val fireNotificationEvent: LiveData<String> = _fireNotificationEvent

    private val _waterNotificationEvent = SingleLiveEvent<String>()
    val waterNotificationEvent: LiveData<String> = _waterNotificationEvent

    private val _weatherNotificationEvent = SingleLiveEvent<String>()
    val weatherNotificationEvent: LiveData<String> = _weatherNotificationEvent

    private val _fireDetected = MutableLiveData<Boolean>(false)
    val fireDetected: LiveData<Boolean> = _fireDetected

    private val _waterDetected = MutableLiveData<Boolean>(false)
    val waterDetected: LiveData<Boolean> = _waterDetected

    private val fireAlertDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("FireAlert")
    private val waterTankAlertDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("WaterTankAlert")

    private var fireAlertListener: ValueEventListener? = null
    private var waterTankAlertListener: ValueEventListener? = null

    private val bmkgApiService: ApiService = RetrofitClient.apiService

    private var lastNotifiedWeatherDescription: String? = null

    init {
        listenForFireAlerts()
        listenForWaterTankAlerts()

        getWeatherForecastAndCheckForAlert("34.04.07.2001")
    }

    private fun listenForFireAlerts() {
        if (fireAlertListener == null) {
            fireAlertListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val detected = snapshot.child("detected").getValue(Boolean::class.java) == true
                    Log.d(TAG, "FireAlert detected: $detected")
                    _fireDetected.value = detected

                    if (detected == true) {
                        _fireNotificationEvent.value = "Sensor mendeteksi potensi kebakaran!"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "FireAlert listener cancelled: ${error.message}", error.toException())
                    _fireDetected.value = false
                }
            }
            fireAlertDatabase.addValueEventListener(fireAlertListener!!)
        }
    }

    private fun stopListeningForFireAlerts() {
        fireAlertListener?.let {
            fireAlertDatabase.removeEventListener(it)
            fireAlertListener = null
        }
    }

    private fun listenForWaterTankAlerts() {
        if (waterTankAlertListener == null) {
            waterTankAlertListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val detected = snapshot.child("detected").getValue(Boolean::class.java) == true
                    Log.d(TAG, "WaterTankAlert detected: $detected")
                    _waterDetected.value = detected

                    if (detected == true) {
                        _waterNotificationEvent.value = "Sensor mendeteksi tandon penuh!"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "WaterTankAlert listener cancelled: ${error.message}", error.toException())
                    _waterDetected.value = false
                }
            }
            waterTankAlertDatabase.addValueEventListener(waterTankAlertListener!!)
        }
    }

    private fun stopListeningForWaterTankAlerts() {
        waterTankAlertListener?.let {
            waterTankAlertDatabase.removeEventListener(it)
            waterTankAlertListener = null
        }
    }

    fun triggerFireAlert(isDetected: Boolean) {
        fireAlertDatabase.child("detected").setValue(isDetected)
            .addOnSuccessListener { Log.d(TAG, "Manual FireAlert set to $isDetected") }
            .addOnFailureListener { e -> Log.e(TAG, "Failed to set manual FireAlert: ${e.message}") }
    }

    fun triggerWaterTankAlert(isDetected: Boolean) {
        waterTankAlertDatabase.child("detected").setValue(isDetected)
            .addOnSuccessListener { Log.d(TAG, "Manual WaterTankAlert set to $isDetected") }
            .addOnFailureListener { e -> Log.e(TAG, "Failed to set manual WaterTankAlert: ${e.message}") }
    }

    fun getWeatherForecastAndCheckForAlert(adm4Code: String) {
        viewModelScope.launch {
            try {
                val weatherResponse = bmkgApiService.getWeatherForecast(adm4Code)
                weatherResponse.let {
                    val allFlattenedForecasts = it.data.firstOrNull()?.cuaca?.flatten()
                    val currentWeatherDescription = allFlattenedForecasts?.firstOrNull()?.weatherDesc
                    Log.d(TAG, "Current weather for BMKG check: $currentWeatherDescription")

                    currentWeatherDescription?.let { desc ->
                        if (isWeatherConditionAlert(desc) && desc != lastNotifiedWeatherDescription) {
                            _weatherNotificationEvent.value = desc
                            lastNotifiedWeatherDescription = desc
                        }
                    }
                }
            } catch (t: Throwable) {
                Log.e(TAG, "BMKG API call failed: ${t.message}", t)
            }
        }
    }

    private fun isWeatherConditionAlert(weatherDesc: String): Boolean {
        return when (weatherDesc.lowercase()) {
            "hujan ringan", "hujan sedang", "hujan lebat",
            "hujan petir", "cerah", "cerah berawan",
            "berawan", "berawan tebal", "kabut", "asap" -> true
            else -> false
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopListeningForFireAlerts()
        stopListeningForWaterTankAlerts()
    }
}