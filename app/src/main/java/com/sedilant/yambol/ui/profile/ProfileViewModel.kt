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
 * Password validation result
 */
data class PasswordValidation(
    val isValid: Boolean = false,
    val hasMinLength: Boolean = false,
    val hasSymbol: Boolean = false,
    val hasNumber: Boolean = false
)

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

    /**
     * Sign-in flow: email first, then password
     */
    data class SignIn(
        val emailInput: String = "",
        val passwordInput: String = "",
        val isPasswordVisible: Boolean = false,
        val showPasswordField: Boolean = false,
        val emailError: String? = null,
        val passwordError: String? = null,
        val isGoogleSignInLoading: Boolean = false
    ) : ProfileUiState()

    /**
     * Sign-up flow: email, password with validation, confirm password
     */
    data class SignUp(
        val emailInput: String = "",
        val passwordInput: String = "",
        val confirmPasswordInput: String = "",
        val isPasswordVisible: Boolean = false,
        val isConfirmPasswordVisible: Boolean = false,
        val emailError: String? = null,
        val passwordValidation: PasswordValidation = PasswordValidation(),
        val confirmPasswordError: String? = null,
        val isGoogleSignInLoading: Boolean = false
    ) : ProfileUiState()

    data class EmailVerificationPending(
        val email: String,
        val isResending: Boolean = false,
        val isChecking: Boolean = false,
        val verificationSent: Boolean = false,
        val message: String? = null
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

    // Temporary storage to restore form if an error occurs
    private var lastAuthState: ProfileUiState = ProfileUiState.SignIn()

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
                    if (!user.isEmailVerified && user.providerData.none { it.providerId == "google.com" }) {
                        // User signed up with email but not verified (skip for Google users)
                        _uiState.update {
                            ProfileUiState.EmailVerificationPending(
                                email = user.email ?: "No Email",
                                verificationSent = false
                            )
                        }
                    } else {
                        _uiState.update {
                            ProfileUiState.Authenticated(
                                userName = user.displayName ?: "User",
                                userEmail = user.email ?: "No Email",
                                isEmailVerified = user.isEmailVerified,
                                photoUrl = user.photoUrl?.toString(),
                                showDeleteConfirmation = false
                            )
                        }
                    }
                } else {
                    _uiState.update { lastAuthState }
                }
            }
        }
    }

    // =================================================================
    // SIGN-IN FLOW
    // =================================================================

    fun onSignInEmailChange(newValue: String) {
        val currentState = _uiState.value as? ProfileUiState.SignIn ?: return
        val newState = currentState.copy(
            emailInput = newValue,
            emailError = null,
            showPasswordField = false,
            passwordInput = ""
        )
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun onSignInPasswordChange(newValue: String) {
        val currentState = _uiState.value as? ProfileUiState.SignIn ?: return
        val newState = currentState.copy(
            passwordInput = newValue,
            passwordError = null
        )
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun toggleSignInPasswordVisibility() {
        val currentState = _uiState.value as? ProfileUiState.SignIn ?: return
        val newState = currentState.copy(isPasswordVisible = !currentState.isPasswordVisible)
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun continueWithEmail() {
        val currentState = _uiState.value as? ProfileUiState.SignIn ?: return
        val email = currentState.emailInput.trim()

        if (!isValidEmail(email)) {
            _uiState.update { currentState.copy(emailError = "Please enter a valid email address") }
            return
        }

        val newState = currentState.copy(
            showPasswordField = true,
            emailError = null
        )
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun signIn() {
        val currentState = _uiState.value as? ProfileUiState.SignIn ?: return
        val email = currentState.emailInput.trim()
        val password = currentState.passwordInput

        if (password.isBlank()) {
            _uiState.update { currentState.copy(passwordError = "Password cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { ProfileUiState.Loading }

            when (val result = authRepository.signInWithEmail(email, password)) {
                is AuthResult.Success -> {
                    lastAuthState = ProfileUiState.SignIn()
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        ProfileUiState.Error(result.exception.message ?: "Sign in failed")
                    }
                }
                AuthResult.Loading -> {}
            }
        }
    }

    // =================================================================
    // SIGN-UP FLOW
    // =================================================================

    fun onSignUpEmailChange(newValue: String) {
        val currentState = _uiState.value as? ProfileUiState.SignUp ?: return
        val newState = currentState.copy(
            emailInput = newValue,
            emailError = null
        )
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun onSignUpPasswordChange(newValue: String) {
        val currentState = _uiState.value as? ProfileUiState.SignUp ?: return
        val validation = validatePassword(newValue)
        val confirmError = if (currentState.confirmPasswordInput.isNotEmpty() &&
            newValue != currentState.confirmPasswordInput
        ) {
            "Passwords do not match"
        } else null

        val newState = currentState.copy(
            passwordInput = newValue,
            passwordValidation = validation,
            confirmPasswordError = confirmError
        )
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun onSignUpConfirmPasswordChange(newValue: String) {
        val currentState = _uiState.value as? ProfileUiState.SignUp ?: return
        val confirmError = if (newValue != currentState.passwordInput) {
            "Passwords do not match"
        } else null

        val newState = currentState.copy(
            confirmPasswordInput = newValue,
            confirmPasswordError = confirmError
        )
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun toggleSignUpPasswordVisibility() {
        val currentState = _uiState.value as? ProfileUiState.SignUp ?: return
        val newState = currentState.copy(isPasswordVisible = !currentState.isPasswordVisible)
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun toggleSignUpConfirmPasswordVisibility() {
        val currentState = _uiState.value as? ProfileUiState.SignUp ?: return
        val newState = currentState.copy(isConfirmPasswordVisible = !currentState.isConfirmPasswordVisible)
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun signUp() {
        val currentState = _uiState.value as? ProfileUiState.SignUp ?: return
        val email = currentState.emailInput.trim()
        val password = currentState.passwordInput
        val confirmPassword = currentState.confirmPasswordInput

        // Validate email
        if (!isValidEmail(email)) {
            _uiState.update { currentState.copy(emailError = "Please enter a valid email address") }
            return
        }

        // Validate password
        val validation = validatePassword(password)
        if (!validation.isValid) {
            _uiState.update { currentState.copy(passwordValidation = validation) }
            return
        }

        // Check password confirmation
        if (password != confirmPassword) {
            _uiState.update { currentState.copy(confirmPasswordError = "Passwords do not match") }
            return
        }

        viewModelScope.launch {
            _uiState.update { ProfileUiState.Loading }

            when (val result = authRepository.signUpWithEmail(email, password)) {
                is AuthResult.Success -> {
                    // Send verification email
                    authRepository.sendEmailVerification()
                    _uiState.update {
                        ProfileUiState.EmailVerificationPending(
                            email = email,
                            verificationSent = true,
                            message = "Verification email sent! Please check your inbox."
                        )
                    }
                    lastAuthState = ProfileUiState.SignIn()
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        ProfileUiState.Error(result.exception.message ?: "Sign up failed")
                    }
                }
                AuthResult.Loading -> {}
            }
        }
    }

    // =================================================================
    // GOOGLE SIGN-IN
    // =================================================================

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            // Update loading state for Google button
            val currentState = _uiState.value
            when (currentState) {
                is ProfileUiState.SignIn -> _uiState.update { currentState.copy(isGoogleSignInLoading = true) }
                is ProfileUiState.SignUp -> _uiState.update { currentState.copy(isGoogleSignInLoading = true) }
                else -> _uiState.update { ProfileUiState.Loading }
            }

            when (val result = authRepository.signInWithGoogle(idToken)) {
                is AuthResult.Success -> {
                    lastAuthState = ProfileUiState.SignIn()
                    // observeAuthState will handle the transition
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        ProfileUiState.Error(result.exception.message ?: "Google sign in failed")
                    }
                }
                AuthResult.Loading -> {}
            }
        }
    }

    fun onGoogleSignInFailed(errorMessage: String) {
        val currentState = _uiState.value
        when (currentState) {
            is ProfileUiState.SignIn -> _uiState.update { currentState.copy(isGoogleSignInLoading = false) }
            is ProfileUiState.SignUp -> _uiState.update { currentState.copy(isGoogleSignInLoading = false) }
            else -> {}
        }
        _uiState.update { ProfileUiState.Error(errorMessage) }
    }

    // =================================================================
    // NAVIGATION BETWEEN SIGN-IN AND SIGN-UP
    // =================================================================

    fun navigateToSignUp() {
        val newState = ProfileUiState.SignUp()
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun navigateToSignIn() {
        val newState = ProfileUiState.SignIn()
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun goBackToEmail() {
        val currentState = _uiState.value as? ProfileUiState.SignIn ?: return
        val newState = currentState.copy(
            showPasswordField = false,
            passwordInput = "",
            passwordError = null
        )
        lastAuthState = newState
        _uiState.update { newState }
    }

    // =================================================================
    // EMAIL VERIFICATION
    // =================================================================

    fun resendVerificationEmail() {
        val currentState = _uiState.value as? ProfileUiState.EmailVerificationPending ?: return

        viewModelScope.launch {
            _uiState.update { currentState.copy(isResending = true, message = null) }

            when (val result = authRepository.sendEmailVerification()) {
                is AuthResult.Success -> {
                    _uiState.update {
                        currentState.copy(
                            isResending = false,
                            verificationSent = true,
                            message = "Verification email sent! Please check your inbox."
                        )
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        currentState.copy(
                            isResending = false,
                            message = "Failed to send email: ${result.exception.message}"
                        )
                    }
                }
                AuthResult.Loading -> {}
            }
        }
    }

    fun checkEmailVerification() {
        val currentState = _uiState.value as? ProfileUiState.EmailVerificationPending ?: return

        viewModelScope.launch {
            _uiState.update { currentState.copy(isChecking = true, message = null) }

            when (val result = authRepository.checkEmailVerified()) {
                is AuthResult.Success -> {
                    if (result.data) {
                        authRepository.reloadUser()
                        val user = authRepository.currentUser
                        if (user != null) {
                            _uiState.update {
                                ProfileUiState.Authenticated(
                                    userName = user.displayName ?: "User",
                                    userEmail = user.email ?: "No Email",
                                    isEmailVerified = true,
                                    photoUrl = user.photoUrl?.toString(),
                                    showDeleteConfirmation = false
                                )
                            }
                        }
                    } else {
                        _uiState.update {
                            currentState.copy(
                                isChecking = false,
                                message = "Email not verified yet. Please check your inbox."
                            )
                        }
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        currentState.copy(
                            isChecking = false,
                            message = "Failed to check verification: ${result.exception.message}"
                        )
                    }
                }
                AuthResult.Loading -> {}
            }
        }
    }

    fun cancelVerificationAndSignOut() {
        viewModelScope.launch {
            _uiState.update { ProfileUiState.Loading }
            authRepository.signOut()
        }
    }

    // =================================================================
    // PROFILE MANAGEMENT (Authenticated)
    // =================================================================

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { ProfileUiState.Loading }
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
        if (authRepository.currentUser != null) {
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
            _uiState.update { lastAuthState }
        }
    }

    // =================================================================
    // VALIDATION HELPERS
    // =================================================================

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): PasswordValidation {
        val hasMinLength = password.length >= 6
        val hasSymbol = password.any { !it.isLetterOrDigit() }
        val hasNumber = password.any { it.isDigit() }

        return PasswordValidation(
            isValid = hasMinLength && hasSymbol && hasNumber,
            hasMinLength = hasMinLength,
            hasSymbol = hasSymbol,
            hasNumber = hasNumber
        )
    }
}
