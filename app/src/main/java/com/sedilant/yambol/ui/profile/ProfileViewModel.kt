package com.sedilant.yambol.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
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
data class ProfileUiState(
    val user: FirebaseUser? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showDeleteConfirmation: Boolean = false,

    // Estado del Formulario de Login/Registro Personalizado
    val emailInput: String = "",
    val passwordInput: String = "",
    val isLoginMode: Boolean = true,
    val isPasswordVisible: Boolean = false
)

/**
 * ViewModel for Profile Screen
 * Manages authentication state, user profile operations and login form
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeAuthState()
    }

    /**
     * Observe authentication state changes
     */
    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.getAuthStateFlow().collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }
    }

    // =================================================================
    // MÉTODOS DEL FORMULARIO DE LOGIN / REGISTRO
    // =================================================================

    fun onEmailChange(newValue: String) {
        _uiState.update { it.copy(emailInput = newValue, errorMessage = null) }
    }

    fun onPasswordChange(newValue: String) {
        _uiState.update { it.copy(passwordInput = newValue, errorMessage = null) }
    }

    fun toggleLoginMode() {
        _uiState.update {
            it.copy(
                isLoginMode = !it.isLoginMode,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    /**
     * Ejecuta el inicio de sesión o el registro basado en el modo actual
     */
    fun authenticate() {
        val email = _uiState.value.emailInput.trim()
        val password = _uiState.value.passwordInput.trim()

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in all fields") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Elegimos función del repo según el modo (Login o Registro)
            val result = if (_uiState.value.isLoginMode) {
                authRepository.signInWithEmail(email, password)
            } else {
                authRepository.signUpWithEmail(email, password)
            }

            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = if (it.isLoginMode) "Welcome back!" else "Account created successfully!",
                            emailInput = "",     // Limpiamos campos por seguridad
                            passwordInput = ""
                        )
                    }
                    // No hace falta llamar a reloadUser manualmente,
                    // observeAuthState detectará el cambio automáticamente.
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.exception.message ?: "Authentication failed"
                        )
                    }
                }
                AuthResult.Loading -> {
                    // El loading ya se gestionó al inicio
                }
            }
        }
    }

    // =================================================================
    // MÉTODOS DE GESTIÓN DE CUENTA (SignOut, Delete, Info)
    // =================================================================

    /**
     * Sign out the current user
     */
    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = authRepository.signOut()) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Successfully signed out",
                            user = null,
                            emailInput = "", // Reset inputs
                            passwordInput = ""
                        )
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to sign out: ${result.exception.message}"
                        )
                    }
                }
                is AuthResult.Loading -> { }
            }
        }
    }

    /**
     * Show delete account confirmation dialog
     */
    fun showDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = true) }
    }

    /**
     * Hide delete account confirmation dialog
     */
    fun hideDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = false) }
    }

    /**
     * Delete the current user account
     */
    fun deleteAccount() {
        viewModelScope.launch {
            // Cerramos el diálogo y mostramos loading
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    showDeleteConfirmation = false
                )
            }

            when (val result = authRepository.deleteAccount()) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Account deleted successfully",
                            user = null,
                            emailInput = "",
                            passwordInput = ""
                        )
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to delete account: ${result.exception.message}"
                        )
                    }
                }
                is AuthResult.Loading -> { }
            }
        }
    }

    // =================================================================
    // GETTERS Y UTILIDADES DE UI
    // =================================================================

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    /**
     * Get user display name or email, or a default string
     */
    fun getUserDisplayName(): String {
        val user = _uiState.value.user
        return when {
            !user?.displayName.isNullOrBlank() -> user.displayName ?: ""
            !user?.email.isNullOrBlank() -> user.email ?: ""
            else -> "User"
        }
    }

    /**
     * Get user email safely
     */
    fun getUserEmail(): String {
        return _uiState.value.user?.email ?: "No email"
    }

    /**
     * Check if user is signed in
     */
    fun isUserSignedIn(): Boolean {
        return _uiState.value.user != null
    }
}