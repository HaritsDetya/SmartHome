package com.example.dashboardsmarthome.dataAPI

import android.content.Context
import android.content.SharedPreferences
import com.example.dashboardsmarthome.ui.virtual.NotificationLog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object NotificationHistoryManager {
    const val PREF_NAME = "notification_history"
    private const val NOTIFICATION_LOGS_KEY = "notification_logs"
    private const val MAX_LOG_ENTRIES = 4

    private var sharedPreferences: SharedPreferences? = null
    private val gson = Gson()

    private val _notificationLogsFlow = MutableStateFlow<List<NotificationLog>>(emptyList())
    val notificationLogsFlow: StateFlow<List<NotificationLog>> = _notificationLogsFlow.asStateFlow()

    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            _notificationLogsFlow.value = loadNotificationHistoryInternal()
        }
    }

    fun init(prefs: SharedPreferences) {
        if (sharedPreferences == null) {
            sharedPreferences = prefs
            _notificationLogsFlow.value = loadNotificationHistoryInternal()
        }
    }

    fun addNotificationToHistory(type: String, message: String) {
        val currentLogs = loadNotificationHistory().toMutableList()
        val newLog = NotificationLog(System.currentTimeMillis(), type, message)
        currentLogs.add(0, newLog)

        val limitedLogs = currentLogs.take(MAX_LOG_ENTRIES)

        sharedPreferences?.edit()?.let { editor ->
            editor.putString(NOTIFICATION_LOGS_KEY, gson.toJson(limitedLogs))
            editor.apply()
            _notificationLogsFlow.value = limitedLogs
        }
    }

    private fun loadNotificationHistoryInternal(): List<NotificationLog> {
        val json = sharedPreferences?.getString(NOTIFICATION_LOGS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<NotificationLog>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun loadNotificationHistory(): List<NotificationLog> {
        return _notificationLogsFlow.value
    }

    fun clearNotificationHistory() {
        sharedPreferences?.edit()?.let { editor ->
            editor.remove(NOTIFICATION_LOGS_KEY)
            editor.apply()
            _notificationLogsFlow.value = emptyList()
        }
    }
}
