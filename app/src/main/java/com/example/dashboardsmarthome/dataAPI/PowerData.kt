package com.example.dashboardsmarthome.dataAPI

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PowerData(
    val current: Double? = null,
    val energy: Double? = null,
    val frequency: Double? = null,
    val powerFactor: Double? = null,
    val power: Double? = null,
    val voltage: Double? = null
) : Parcelable