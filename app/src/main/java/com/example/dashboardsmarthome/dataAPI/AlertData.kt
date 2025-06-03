package com.example.dashboardsmarthome.dataAPI

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FireAlertData(
    val detected: Boolean? = null
) : Parcelable

@Parcelize
data class WaterTankAlertData(
    val detected: Boolean? = null
) : Parcelable