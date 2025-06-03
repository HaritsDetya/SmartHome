package com.example.dashboardsmarthome.ui.home

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dashboardsmarthome.dataAPI.KontrolData
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

    private val _kontrolData = MutableLiveData<KontrolData?>()
    val kontrolData: LiveData<KontrolData?> = _kontrolData

    private val powerDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("monitoring")
    private val kontrolDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("kontrol")

    private var firebasePowerListener: ValueEventListener? = null
    private var firebaseKontrolListener: ValueEventListener? = null

    init {
        generateAndSetDummyData()
        _kontrolData.value = KontrolData(relay1 = false, relay2 = false)

        listenForPowerData()
        listenForKontrolData()
    }

    private fun listenForPowerData() {
        if (firebasePowerListener == null) {
            firebasePowerListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(PowerData::class.java)
                    if (data != null &&
                        data.current != null && data.energy != null &&
                        data.frequency != null && data.pf != null &&
                        data.power != null && data.voltage != null) {
                        _powerData.value = data
                        Log.d(TAG, "Firebase Power data received: $data")
                    } else {
                        Log.w(TAG, "Firebase Power data is null or incomplete, using fallback data.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Firebase Power connection cancelled: ${error.message}, using fallback data.", error.toException())
                }
            }
            powerDatabase.addValueEventListener(firebasePowerListener!!)
            Log.d(TAG, "Firebase Power listener ADDED.")
        }
    }

    private fun stopListeningForPowerData() {
        firebasePowerListener?.let {
            powerDatabase.removeEventListener(it)
            firebasePowerListener = null
            Log.d(TAG, "Firebase Power listener REMOVED.")
        }
    }

    private fun generateAndSetDummyData() {
        val current = String.format("%.2f", Random.nextDouble(0.5, 2.0)).toDouble()
        val energy = String.format("%.5f", Random.nextDouble(0.0001, 0.0005)).toDouble()
        val frequency = String.format("%.1f", Random.nextDouble(49.5, 50.5)).toDouble()
        val pf = String.format("%.2f", Random.nextDouble(0.90, 1.0)).toDouble()
        val power = String.format("%.1f", Random.nextDouble(200.0, 300.0)).toDouble()
        val voltage = String.format("%.3f", Random.nextDouble(210.0, 230.0)).toDouble()

        val dummyData = PowerData(
            current, energy, frequency, pf, power, voltage
        )
        _powerData.value = dummyData
        Log.d(TAG, "Dummy data generated and set: $dummyData")
    }

    private fun listenForKontrolData() {
        if (firebaseKontrolListener == null) {
            firebaseKontrolListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(KontrolData::class.java)
                    if (data != null) {
                        _kontrolData.value = data
                        Log.d(TAG, "Firebase Kontrol data received: $data")
                    } else {
                        Log.w(TAG, "Firebase Kontrol data is null. Initializing with default false.")
                        _kontrolData.value = KontrolData(relay1 = false, relay2 = false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Firebase Kontrol connection cancelled: ${error.message}", error.toException())
                    _kontrolData.value = KontrolData(relay1 = false, relay2 = false)
                }
            }
            kontrolDatabase.addValueEventListener(firebaseKontrolListener!!)
            Log.d(TAG, "Firebase Kontrol listener ADDED.")
        }
    }

    private fun stopListeningForKontrolData() {
        firebaseKontrolListener?.let {
            kontrolDatabase.removeEventListener(it)
            firebaseKontrolListener = null
            Log.d(TAG, "Firebase Kontrol listener REMOVED.")
        }
    }

    fun updateRelayStatus(relayName: String, status: Boolean) {
        kontrolDatabase.child(relayName).setValue(status)
            .addOnSuccessListener {
                Log.d(TAG, "$relayName updated to $status in Firebase.")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to update $relayName in Firebase: ${e.message}", e)
                val currentKontrolData = _kontrolData.value ?: KontrolData()
                val revertedKontrolData = when (relayName) {
                    "relay1" -> currentKontrolData.copy(relay1 = !status)
                    "relay2" -> currentKontrolData.copy(relay2 = !status)
                    else -> currentKontrolData
                }
                _kontrolData.value = revertedKontrolData
            }
    }

    override fun onCleared() {
        super.onCleared()
        stopListeningForPowerData()
        stopListeningForKontrolData()
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

}