package com.sedilant.yambol.feature.profile

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

    data class Error(val message: String) : ProfileUiState()
}
