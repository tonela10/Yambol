package com.sedilant.yambol.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firebaseAuth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for the Profile Screen
 */
sealed class ProfileUiState {
    data object Loading : ProfileUiState()

    data class Authenticated(
        val userName: String,
        val userEmail: String,
        val isEmailVerified: Boolean,
        val photoUrl: String?,
        val showDeleteConfirmation: Boolean = false
    ) : ProfileUiState()

    data class Unauthenticated(
        val emailInput: String = "",
        val passwordInput: String = "",
        val isPasswordVisible: Boolean = false,
        val isLoginMode: Boolean = true,
    ) : ProfileUiState()

    data class Error(val message: String) : ProfileUiState()
}

/**
 * ViewModel for Profile Screen
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Temporary storage to restore form if an error occurs (since Error state replaces the Form state)
    private var lastUnauthenticatedState = ProfileUiState.Unauthenticated()

    init {
        observeAuthState()
    }

    /**
     * Observe authentication state changes
     * This acts as the "Source of Truth" for which major state we are in.
     */
    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.getAuthStateFlow().collect { user ->
                if (user != null) {
                    _uiState.update {
                        ProfileUiState.Authenticated(
                            userName = user.displayName ?: "User",
                            userEmail = user.email ?: "No Email",
                            isEmailVerified = user.isEmailVerified,
                            photoUrl = user.photoUrl?.toString(),
                            showDeleteConfirmation = false
                        )
                    }
                } else {
                    // If we logged out, or started fresh, we show the form
                    // We try to use the last known form state to keep inputs if this was a quick transition
                    _uiState.update { lastUnauthenticatedState }
                }
            }
        }
    }

    // =================================================================
    // FORM ACTIONS (Only work when Unauthenticated)
    // =================================================================

    fun onEmailChange(newValue: String) {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Unauthenticated) {
            val newState = currentState.copy(emailInput = newValue)
            lastUnauthenticatedState = newState // Backup
            _uiState.update { newState }
        }
    }

    fun onPasswordChange(newValue: String) {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Unauthenticated) {
            val newState = currentState.copy(passwordInput = newValue)
            lastUnauthenticatedState = newState // Backup
            _uiState.update { newState }
        }
    }

    fun toggleLoginMode() {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Unauthenticated) {
            val newState = currentState.copy(isLoginMode = !currentState.isLoginMode)
            lastUnauthenticatedState = newState
            _uiState.update { newState }
        }
    }

    fun togglePasswordVisibility() {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Unauthenticated) {
            val newState = currentState.copy(isPasswordVisible = !currentState.isPasswordVisible)
            lastUnauthenticatedState = newState
            _uiState.update { newState }
        }
    }

    // =================================================================
    // AUTHENTICATION LOGIC
    // =================================================================

    fun authenticate() {
        // We can only authenticate if we are currently in the Unauthenticated state (form visible)
        val currentState = _uiState.value as? ProfileUiState.Unauthenticated ?: return

        val email = currentState.emailInput.trim()
        val password = currentState.passwordInput.trim()

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { ProfileUiState.Error("Please fill in all fields") }
            return
        }

        viewModelScope.launch {
            _uiState.update { ProfileUiState.Loading }

            val result = if (currentState.isLoginMode) {
                authRepository.signInWithEmail(email, password)
            } else {
                authRepository.signUpWithEmail(email, password)
            }

            when (result) {
                is AuthResult.Success -> {
                    // We do NOT manually set Authenticated state here.
                    // The observeAuthState() collector will detect the Firebase User change
                    // and update the UI automatically.

                    // Optional: Reset form inputs for next time
                    lastUnauthenticatedState = ProfileUiState.Unauthenticated()
                }
                is AuthResult.Error -> {
                    // If failed, show Error screen/dialog
                    _uiState.update {
                        ProfileUiState.Error(result.exception.message ?: "Authentication failed")
                    }
                }
                AuthResult.Loading -> { /* Handled by state */ }
            }
        }
    }

    // =================================================================
    // PROFILE MANAGEMENT (Only work when Authenticated)
    // =================================================================

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { ProfileUiState.Loading }
            authRepository.signOut()
            // observeAuthState will handle the transition to Unauthenticated
        }
    }

    fun showDeleteConfirmation() {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Authenticated) {
            _uiState.update { currentState.copy(showDeleteConfirmation = true) }
        }
    }

    fun hideDeleteConfirmation() {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Authenticated) {
            _uiState.update { currentState.copy(showDeleteConfirmation = false) }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update { ProfileUiState.Loading }

            when (val result = authRepository.deleteAccount()) {
                is AuthResult.Success -> {
                    // observeAuthState will handle the transition
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        ProfileUiState.Error("Failed to delete account: ${result.exception.message}")
                    }
                }
                is AuthResult.Loading -> {}
            }
        }
    }

    // =================================================================
    // ERROR RECOVERY
    // =================================================================

    /**
     * Call this when the user clicks "OK" on the error dialog/screen.
     * It restores the previous state based on whether the user is logged in or not.
     */
    fun clearError() {
        if (authRepository.currentUser != null) {
            // Restore Authenticated State
            val user = authRepository.currentUser!!
            _uiState.update {
                ProfileUiState.Authenticated(
                    userName = user.displayName ?: "User",
                    userEmail = user.email ?: "",
                    isEmailVerified = user.isEmailVerified,
                    photoUrl = user.photoUrl?.toString(),
                    showDeleteConfirmation = false
                )
            }
        } else {
            // Restore Unauthenticated State (The Form) with previous inputs
            _uiState.update { lastUnauthenticatedState }
        }
    }
}
