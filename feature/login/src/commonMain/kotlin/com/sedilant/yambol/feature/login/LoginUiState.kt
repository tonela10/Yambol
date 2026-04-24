package com.sedilant.yambol.feature.login

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
