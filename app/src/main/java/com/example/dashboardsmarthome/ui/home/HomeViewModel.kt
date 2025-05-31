package com.example.dashboardsmarthome.ui.home

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dashboardsmarthome.EnergyMonitoringActivity
import com.example.dashboardsmarthome.dataAPI.PowerData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    companion object{
        private const val TAG = "HomeViewModel"
    }

    private val _powerData = MutableLiveData<PowerData?>()
    val powerData: LiveData<PowerData?> = _powerData

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("Power")
    private var firebaseListener: ValueEventListener? = null

    private val dummyHandler = Handler(Looper.getMainLooper())

    init {
        generateAndSetDummyData()

        listenForFirebaseData()
    }

    private fun listenForFirebaseData() {
        if (firebaseListener == null) {
            firebaseListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(PowerData::class.java)

                    if (data != null &&
                        data.current != null && data.energy != null &&
                        data.frequency != null && data.powerFactor != null &&
                        data.power != null && data.voltage != null) {
                        _powerData.value = data
                        Log.d(TAG, "Firebase data received: $data")
                    } else {
                        Log.w(TAG, "Firebase Power data is null or incomplete, using fallback data.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Firebase connection cancelled: ${error.message}, using fallback data.", error.toException())
                }
            }
            database.addValueEventListener(firebaseListener!!)
        }
    }

    private fun generateAndSetDummyData() {
        val current = String.format("%.2f", Random.nextDouble(0.5, 2.0)).toDouble()
        val energy = String.format("%.1f", Random.nextDouble(100.0, 150.0)).toDouble()
        val frequency = String.format("%.1f", Random.nextDouble(49.5, 50.5)).toDouble()
        val powerFactor = String.format("%.2f", Random.nextDouble(0.90, 1.0)).toDouble()
        val power = String.format("%.1f", Random.nextDouble(200.0, 300.0)).toDouble()
        val voltage = String.format("%.1f", Random.nextDouble(210.0, 230.0)).toDouble()

        val dummyData = PowerData(
            current, energy, frequency, powerFactor, power, voltage
        )
        _powerData.value = dummyData
        Log.d(TAG, "Dummy data generated and set: $dummyData")
    }

    override fun onCleared() {
        super.onCleared()
        firebaseListener?.let {
            database.removeEventListener(it)
        }
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

}