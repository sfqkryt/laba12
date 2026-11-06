package com.example.eyetracker_app.data

import android.content.Context
import com.example.eyetracker_app.model.CustomGesture
import com.example.eyetracker_app.model.EyeTrackerUiState
import org.json.JSONArray
import org.json.JSONObject

/**
 * Local data source for the prototype. SharedPreferences is enough for the
 * small amount of settings and custom gestures used by the application.
 */
class EyeTrackerRepository(context: Context) {
    private val preferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    fun loadInitialState(): EyeTrackerUiState = EyeTrackerUiState(
        eyeStrainPercentage = preferences.getInt(EYE_STRAIN_PERCENTAGE_KEY, 34),
        customGestureList = loadCustomGestures(),
        isRestNotificationEnabled = preferences.getBoolean(
            REST_NOTIFICATION_ENABLED_KEY,
            true
        ),
        areEyeGesturesEnabled = preferences.getBoolean(
            EYE_GESTURES_ENABLED_KEY,
            true
        )
    )

    fun saveEyeStrainPercentage(value: Int) {
        preferences.edit().putInt(EYE_STRAIN_PERCENTAGE_KEY, value).apply()
    }

    fun saveRestNotificationEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(REST_NOTIFICATION_ENABLED_KEY, enabled).apply()
    }

    fun saveEyeGesturesEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(EYE_GESTURES_ENABLED_KEY, enabled).apply()
    }

    fun saveCustomGestures(gestures: List<CustomGesture>) {
        val json = JSONArray()
        gestures.forEach { gesture ->
            json.put(
                JSONObject()
                    .put("name", gesture.gestureName)
                    .put("movement", gesture.eyeMovement)
            )
        }
        preferences.edit().putString(CUSTOM_GESTURES_JSON_KEY, json.toString()).apply()
    }

    private fun loadCustomGestures(): List<CustomGesture> = runCatching {
        val json = preferences.getString(CUSTOM_GESTURES_JSON_KEY, "[]") ?: "[]"
        val array = JSONArray(json)
        List(array.length()) { index ->
            val item = array.getJSONObject(index)
            CustomGesture(
                gestureName = item.getString("name"),
                eyeMovement = item.getString("movement")
            )
        }
    }.getOrDefault(emptyList())

    private companion object {
        const val PREFERENCES_NAME = "eyetrack_preferences"
        const val EYE_STRAIN_PERCENTAGE_KEY = "eye_load"
        const val CUSTOM_GESTURES_JSON_KEY = "custom_gestures"
        const val REST_NOTIFICATION_ENABLED_KEY = "rest_notification_enabled"
        const val EYE_GESTURES_ENABLED_KEY = "eye_gestures_enabled"
    }
}
