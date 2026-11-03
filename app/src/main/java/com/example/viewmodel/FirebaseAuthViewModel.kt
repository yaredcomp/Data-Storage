package com.example.viewmodel

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val currentUserEmail: String? = null,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isDemoSession: Boolean = false
)

/**
 * ViewModel for Module 3: Firebase Authentication
 *
 * Demonstrates cloud-based session & user identity storage.
 * Automatically synchronizes login state changes via FirebaseAuth.AuthStateListener.
 */
class FirebaseAuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var auth: FirebaseAuth? = null

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            _uiState.update {
                it.copy(
                    currentUserEmail = user.email ?: "Authenticated User",
                    isLoggedIn = true,
                    isDemoSession = false
                )
            }
        } else if (!_uiState.value.isDemoSession) {
            _uiState.update {
                it.copy(
                    currentUserEmail = null,
                    isLoggedIn = false
                )
            }
        }
    }

    init {
        initFirebase()
    }

    private fun initFirebase() {
        try {
            if (FirebaseApp.getApps(getApplication()).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("com.aistudio.datastorage.kdemox")
                    .setApiKey("AIzaSyChapter4DataStorageDemoKey1234567")
                    .setProjectId("datastorage-chapter4")
                    .build()
                FirebaseApp.initializeApp(getApplication(), options)
            }

            val firebaseAuth = FirebaseAuth.getInstance()
            auth = firebaseAuth
            firebaseAuth.addAuthStateListener(authListener)

            val current = firebaseAuth.currentUser
            if (current != null) {
                _uiState.update {
                    it.copy(
                        currentUserEmail = current.email ?: "Active Session",
                        isLoggedIn = true
                    )
                }
            }
        } catch (e: Exception) {
            Log.w("FirebaseAuthViewModel", "Firebase init warning: ${e.message}")
        }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(emailInput = newEmail, errorMessage = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(passwordInput = newPassword, errorMessage = null) }
    }

    /**
     * Module 3 Requirement: Register with createUserWithEmailAndPassword
     */
    fun register() {
        val email = _uiState.value.emailInput.trim()
        val password = _uiState.value.passwordInput

        if (!validateInputs(email, password)) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        val firebaseAuth = auth
        if (firebaseAuth == null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "FirebaseAuth instance not available. Please ensure google-services.json is configured."
                )
            }
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            currentUserEmail = user?.email ?: email,
                            successMessage = "Account registered and logged in successfully!",
                            passwordInput = ""
                        )
                    }
                } else {
                    val ex = task.exception
                    val readableError = parseAuthError(ex?.message)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = readableError
                        )
                    }
                }
            }
    }

    /**
     * Module 3 Requirement: Login with signInWithEmailAndPassword
     */
    fun login() {
        val email = _uiState.value.emailInput.trim()
        val password = _uiState.value.passwordInput

        if (!validateInputs(email, password)) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        val firebaseAuth = auth
        if (firebaseAuth == null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "FirebaseAuth instance not available."
                )
            }
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            currentUserEmail = user?.email ?: email,
                            successMessage = "Logged in successfully!",
                            passwordInput = ""
                        )
                    }
                } else {
                    val ex = task.exception
                    val readableError = parseAuthError(ex?.message)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = readableError
                        )
                    }
                }
            }
    }

    /**
     * Module 3 Requirement: Logout with signOut()
     */
    fun logout() {
        auth?.signOut()
        _uiState.update {
            it.copy(
                currentUserEmail = null,
                isLoggedIn = false,
                isDemoSession = false,
                successMessage = "Signed out successfully.",
                errorMessage = null
            )
        }
    }

    /**
     * Educational helper: Allows students to simulate a successful session state
     * if they are testing offline without an active Firebase backend connection.
     */
    fun simulateDemoSession(mockEmail: String = "student.demo@university.edu") {
        _uiState.update {
            it.copy(
                isLoggedIn = true,
                isDemoSession = true,
                currentUserEmail = mockEmail,
                successMessage = "Demo session simulated! Testing UI response to active login.",
                errorMessage = null
            )
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private fun validateInputs(email: String, pass: String): Boolean {
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address.") }
            return false
        }
        if (pass.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters.") }
            return false
        }
        return true
    }

    private fun parseAuthError(rawMessage: String?): String {
        if (rawMessage == null) return "Authentication failed. Please verify credentials."
        return when {
            rawMessage.contains("password", ignoreCase = true) && rawMessage.contains("invalid", ignoreCase = true) ->
                "Invalid password for this account."
            rawMessage.contains("no user record", ignoreCase = true) || rawMessage.contains("user-not-found", ignoreCase = true) ->
                "No user registered with this email. Please click 'Register' first."
            rawMessage.contains("already in use", ignoreCase = true) || rawMessage.contains("email-already-in-use", ignoreCase = true) ->
                "An account with this email already exists. Please click 'Login'."
            rawMessage.contains("network", ignoreCase = true) ->
                "Network connection error. Check device connectivity."
            rawMessage.contains("API key not valid", ignoreCase = true) || rawMessage.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) ->
                "Firebase project configuration required: Ensure valid google-services.json is linked to an active Firebase project with Email/Password Auth enabled."
            else -> rawMessage
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            auth?.removeAuthStateListener(authListener)
        } catch (ignored: Exception) {}
    }
}
