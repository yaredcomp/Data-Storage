package com.example.data.prefs

import android.content.Context
import android.content.SharedPreferences

/**
 * Module 1: SharedPreferences Manager
 *
 * Demonstrates saving lightweight key-value pairs persistently in Android.
 * SharedPreferences persists data in an XML file stored in the app's private
 * internal storage directory: /data/data/<package_name>/shared_prefs/<file_name>.xml
 */
class SettingsManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        const val PREFS_NAME = "app_user_settings"
        const val KEY_DARK_MODE = "dark_mode"
        const val KEY_APP_LANGUAGE = "app_language"
        const val DEFAULT_LANGUAGE = "English"
        const val DEFAULT_DARK_MODE = false
    }

    /**
     * Saves user preferences using the SharedPreferences.Editor.
     * Uses apply() for asynchronous writing or commit() for synchronous writing.
     */
    fun saveSettings(isDarkMode: Boolean, language: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_DARK_MODE, isDarkMode)
        editor.putString(KEY_APP_LANGUAGE, language.trim())
        editor.apply()
    }

    /**
     * Reads the boolean value stored under KEY_DARK_MODE.
     */
    fun isDarkMode(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, DEFAULT_DARK_MODE)
    }

    /**
     * Reads the string value stored under KEY_APP_LANGUAGE.
     */
    fun getAppLanguage(): String {
        return sharedPreferences.getString(KEY_APP_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    /**
     * Clears all stored key-value pairs (helpful for demonstrations).
     */
    fun clearSettings() {
        sharedPreferences.edit().clear().apply()
    }

    /**
     * Helper to retrieve raw stored XML representation for educational inspection.
     */
    fun getAllAsMap(): Map<String, *> {
        return sharedPreferences.all
    }
}
