package com.sedilant.yambol.ui.profile

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseUser
import com.sedilant.yambol.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileUiState {
    object Initial : ProfileUiState()
    object Loading : ProfileUiState()
    data class Authenticated(val user: FirebaseUser) : ProfileUiState()
    object Unauthenticated : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _profileUiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Initial)
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        val user = authRepository.currentUser
        _profileUiState.value = if (user != null) {
            ProfileUiState.Authenticated(user)
        } else {
            ProfileUiState.Unauthenticated
        }
    }

    fun getSignInIntent(): Intent {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            // Add other providers as needed
        )
        return authRepository.createSignInIntent(providers)
    }

    fun handleSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            // Successfully signed in
            val user = authRepository.currentUser
            if (user != null) {
                _profileUiState.value = ProfileUiState.Authenticated(user)
            } else {
                _profileUiState.value = ProfileUiState.Error("Sign in succeeded but user is null")
            }
        } else {
            // Sign in failed
            val response = result.idpResponse
            if (response == null) {
                // User pressed back button
                _profileUiState.value = ProfileUiState.Unauthenticated
            } else {
                val errorMessage = response.error?.message ?: "Unknown error occurred"
                _profileUiState.value = ProfileUiState.Error(errorMessage)
            }
        }
    }

    fun signOut(activity: Activity) {
        viewModelScope.launch {
            _profileUiState.value = ProfileUiState.Loading
            authRepository.signOut(activity)
                .onSuccess {
                    _profileUiState.value = ProfileUiState.Unauthenticated
                }
                .onFailure { exception ->
                    _profileUiState.value = ProfileUiState.Error(
                        exception.message ?: "Failed to sign out"
                    )
                }
        }
    }

    fun deleteAccount(activity: Activity) {
        viewModelScope.launch {
            _profileUiState.value = ProfileUiState.Loading
            authRepository.deleteAccount(activity)
                .onSuccess {
                    _profileUiState.value = ProfileUiState.Unauthenticated
                }
                .onFailure { exception ->
                    _profileUiState.value = ProfileUiState.Error(
                        exception.message ?: "Failed to delete account"
                    )
                }
        }
    }

    fun getUserEmail(): String? = authRepository.getUserEmail()

    fun getUserDisplayName(): String? = authRepository.getUserDisplayName()

    fun getUserPhotoUrl(): String? = authRepository.getUserPhotoUrl()
}