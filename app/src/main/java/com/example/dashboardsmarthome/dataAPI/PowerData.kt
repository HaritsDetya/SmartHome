package com.example.dashboardsmarthome.dataAPI

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PowerData(
    val current: Double? = null,
    val energy: Double? = null,
    val frequency: Double? = null,
    val pf: Double? = null,
    val power: Double? = null,
    val voltage: Double? = null
) : Parcelable

@Parcelize
data class KontrolData(
    val relay1: Boolean? = null,
    val relay2: Boolean? = null
    // val relay3: Boolean? = null
) : Parcelable
