package com.sedilant.yambol.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.DataStoreManager
import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firebaseAuth.AuthResult
import com.sedilant.yambol.feature.profile.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Profile Screen
 */
class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
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
                if (user != null) {
                    _uiState.update {
                        ProfileUiState.Authenticated(
                            userName = user.displayName ?: "User",
                            userEmail = user.email ?: "No Email",
                            isEmailVerified = user.isEmailVerified,
                            photoUrl = user.photoURL,
                            showDeleteConfirmation = false
                        )
                    }
                } else {
                    _uiState.update { ProfileUiState.Loading }
                }
            }
        }
    }

    // =================================================================
    // PROFILE MANAGEMENT (Authenticated)
    // =================================================================

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { ProfileUiState.Loading }
            dataStoreManager.saveCurrentTeam(null)
            authRepository.signOut()
        }
    }

    fun showDeleteConfirmation() {
        val currentState = _uiState.value as? ProfileUiState.Authenticated ?: return
        _uiState.update { currentState.copy(showDeleteConfirmation = true) }
    }

    fun hideDeleteConfirmation() {
        val currentState = _uiState.value as? ProfileUiState.Authenticated ?: return
        _uiState.update { currentState.copy(showDeleteConfirmation = false) }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update { ProfileUiState.Loading }

            when (val result = authRepository.deleteAccount()) {
                is AuthResult.Success -> {
                    dataStoreManager.saveCurrentTeam(null)
                    // observeAuthState will handle the transition
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        ProfileUiState.Error("Failed to delete account: ${result.exception.message}")
                    }
                }
                AuthResult.Loading -> {}
            }
        }
    }

    // =================================================================
    // ERROR RECOVERY
    // =================================================================

    fun clearError() {
        val user = authRepository.currentUser
        if (user != null) {
            _uiState.update {
                ProfileUiState.Authenticated(
                    userName = user.displayName ?: "User",
                    userEmail = user.email ?: "",
                    isEmailVerified = user.isEmailVerified,
                    photoUrl = user.photoURL,
                    showDeleteConfirmation = false
                )
            }
        } else {
            _uiState.update { ProfileUiState.Loading }
        }
    }
}
