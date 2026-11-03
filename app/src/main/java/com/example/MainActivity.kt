package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.FirebaseAuthViewModel
import com.example.viewmodel.SettingsViewModel
import com.example.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()
    private val taskViewModel: TaskViewModel by viewModels()
    private val firebaseAuthViewModel: FirebaseAuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()

            // Reactively apply the persisted Dark Mode preference from SharedPreferences!
            MyApplicationTheme(
                darkTheme = settingsState.persistedDarkMode,
                dynamicColor = false // Keep consistent educational brand styling
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavigation(
                        settingsViewModel = settingsViewModel,
                        taskViewModel = taskViewModel,
                        firebaseAuthViewModel = firebaseAuthViewModel
                    )
                }
            }
        }
    }
}
