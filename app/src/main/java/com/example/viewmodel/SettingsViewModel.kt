package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.prefs.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val appLanguage: String = "English",
    val persistedDarkMode: Boolean = false,
    val persistedAppLanguage: String = "English",
    val rawXmlPreview: String = "",
    val feedbackMessage: String? = null
)

/**
 * ViewModel for Module 1: SharedPreferences Demo
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsManager = SettingsManager(application.applicationContext)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    /**
     * Reads saved values from SharedPreferences and updates the UI state.
     * Proves data persistence across screen reloads and app launches.
     */
    fun loadSettings() {
        val darkMode = settingsManager.isDarkMode()
        val language = settingsManager.getAppLanguage()
        val allMap = settingsManager.getAllAsMap()

        val xmlRepresentation = buildXmlPreview(darkMode, language)

        _uiState.update {
            it.copy(
                isDarkMode = darkMode,
                appLanguage = language,
                persistedDarkMode = darkMode,
                persistedAppLanguage = language,
                rawXmlPreview = xmlRepresentation,
                feedbackMessage = "Loaded persisted values from SharedPreferences"
            )
        }
    }

    fun onDarkModeChanged(enabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled, feedbackMessage = null) }
    }

    fun onLanguageChanged(newLanguage: String) {
        _uiState.update { it.copy(appLanguage = newLanguage, feedbackMessage = null) }
    }

    /**
     * Saves user inputs into SharedPreferences XML file.
     */
    fun saveSettings() {
        viewModelScope.launch {
            val dark = _uiState.value.isDarkMode
            val lang = _uiState.value.appLanguage

            settingsManager.saveSettings(dark, lang)

            val xmlRepresentation = buildXmlPreview(dark, lang)

            _uiState.update {
                it.copy(
                    persistedDarkMode = dark,
                    persistedAppLanguage = lang,
                    rawXmlPreview = xmlRepresentation,
                    feedbackMessage = "Settings committed to app_user_settings.xml!"
                )
            }
        }
    }

    fun clearSettings() {
        settingsManager.clearSettings()
        loadSettings()
        _uiState.update {
            it.copy(feedbackMessage = "Cleared SharedPreferences XML file.")
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    private fun buildXmlPreview(dark: Boolean, lang: String): String {
        return """
            <?xml version='1.0' encoding='utf-8' standalone='yes' ?>
            <map>
                <boolean name="dark_mode" value="$dark" />
                <string name="app_language">$lang</string>
            </map>
        """.trimIndent()
    }
}
