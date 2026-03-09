package com.sedilant.yambol.ui.login

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
 * UI State for the Login Screen
 */
sealed class LoginUiState {
    data object Loading : LoginUiState()

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
    ) : LoginUiState()

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
    ) : LoginUiState()

    data class Error(val message: String) : LoginUiState()
}

/**
 * ViewModel for Login Screen
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.SignIn())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Temporary storage to restore form if an error occurs
    private var lastAuthState: LoginUiState = LoginUiState.SignIn()

    // =================================================================
    // SIGN-IN FLOW
    // =================================================================

    fun onSignInEmailChange(newValue: String) {
        val currentState = _uiState.value as? LoginUiState.SignIn ?: return
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
        val currentState = _uiState.value as? LoginUiState.SignIn ?: return
        val newState = currentState.copy(
            passwordInput = newValue,
            passwordError = null
        )
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun toggleSignInPasswordVisibility() {
        val currentState = _uiState.value as? LoginUiState.SignIn ?: return
        val newState = currentState.copy(isPasswordVisible = !currentState.isPasswordVisible)
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun continueWithEmail() {
        val currentState = _uiState.value as? LoginUiState.SignIn ?: return
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
        val currentState = _uiState.value as? LoginUiState.SignIn ?: return
        val email = currentState.emailInput.trim()
        val password = currentState.passwordInput

        if (password.isBlank()) {
            _uiState.update { currentState.copy(passwordError = "Password cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { LoginUiState.Loading }

            when (val result = authRepository.signInWithEmail(email, password)) {
                is AuthResult.Success -> {
                    lastAuthState = LoginUiState.SignIn()
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        LoginUiState.Error(result.exception.message ?: "Sign in failed")
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
        val currentState = _uiState.value as? LoginUiState.SignUp ?: return
        val newState = currentState.copy(
            emailInput = newValue,
            emailError = null
        )
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun onSignUpPasswordChange(newValue: String) {
        val currentState = _uiState.value as? LoginUiState.SignUp ?: return
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
        val currentState = _uiState.value as? LoginUiState.SignUp ?: return
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
        val currentState = _uiState.value as? LoginUiState.SignUp ?: return
        val newState = currentState.copy(isPasswordVisible = !currentState.isPasswordVisible)
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun toggleSignUpConfirmPasswordVisibility() {
        val currentState = _uiState.value as? LoginUiState.SignUp ?: return
        val newState = currentState.copy(isConfirmPasswordVisible = !currentState.isConfirmPasswordVisible)
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun signUp() {
        val currentState = _uiState.value as? LoginUiState.SignUp ?: return
        val email = currentState.emailInput.trim()
        val password = currentState.passwordInput
        val confirmPassword = currentState.confirmPasswordInput

        if (!isValidEmail(email)) {
            _uiState.update { currentState.copy(emailError = "Please enter a valid email address") }
            return
        }

        val validation = validatePassword(password)
        if (!validation.isValid) {
            _uiState.update { currentState.copy(passwordValidation = validation) }
            return
        }

        if (password != confirmPassword) {
            _uiState.update { currentState.copy(confirmPasswordError = "Passwords do not match") }
            return
        }

        viewModelScope.launch {
            _uiState.update { LoginUiState.Loading }

            when (val result = authRepository.signUpWithEmail(email, password)) {
                is AuthResult.Success -> {
                    lastAuthState = LoginUiState.SignIn()
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        LoginUiState.Error(result.exception.message ?: "Sign up failed")
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
            val currentState = _uiState.value
            when (currentState) {
                is LoginUiState.SignIn -> _uiState.update { currentState.copy(isGoogleSignInLoading = true) }
                is LoginUiState.SignUp -> _uiState.update { currentState.copy(isGoogleSignInLoading = true) }
                else -> _uiState.update { LoginUiState.Loading }
            }

            when (val result = authRepository.signInWithGoogle(idToken)) {
                is AuthResult.Success -> {
                    lastAuthState = LoginUiState.SignIn()
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        LoginUiState.Error(result.exception.message ?: "Google sign in failed")
                    }
                }
                AuthResult.Loading -> {}
            }
        }
    }

    fun onGoogleSignInFailed(errorMessage: String) {
        val currentState = _uiState.value
        when (currentState) {
            is LoginUiState.SignIn -> _uiState.update { currentState.copy(isGoogleSignInLoading = false) }
            is LoginUiState.SignUp -> _uiState.update { currentState.copy(isGoogleSignInLoading = false) }
            else -> {}
        }
        _uiState.update { LoginUiState.Error(errorMessage) }
    }

    // =================================================================
    // NAVIGATION BETWEEN SIGN-IN AND SIGN-UP
    // =================================================================

    fun navigateToSignUp() {
        val newState = LoginUiState.SignUp()
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun navigateToSignIn() {
        val newState = LoginUiState.SignIn()
        lastAuthState = newState
        _uiState.update { newState }
    }

    fun goBackToEmail() {
        val currentState = _uiState.value as? LoginUiState.SignIn ?: return
        val newState = currentState.copy(
            showPasswordField = false,
            passwordInput = "",
            passwordError = null
        )
        lastAuthState = newState
        _uiState.update { newState }
    }

    // =================================================================
    // ERROR RECOVERY
    // =================================================================

    fun clearError() {
        _uiState.update { lastAuthState }
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

