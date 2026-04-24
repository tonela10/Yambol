package com.sedilant.yambol.feature.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import com.sedilant.yambol.core.designsystem.Res
import com.sedilant.yambol.core.designsystem.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenStateless(
    uiState: LoginUiState,
    onSignInEmailChange: (String) -> Unit,
    onSignInPasswordChange: (String) -> Unit,
    onToggleSignInPasswordVisibility: () -> Unit,
    onContinueWithEmail: () -> Unit,
    onSignIn: () -> Unit,
    onGoBackToEmail: () -> Unit,
    onSignUpEmailChange: (String) -> Unit,
    onSignUpPasswordChange: (String) -> Unit,
    onSignUpConfirmPasswordChange: (String) -> Unit,
    onToggleSignUpPasswordVisibility: () -> Unit,
    onToggleSignUpConfirmPasswordVisibility: () -> Unit,
    onSignUp: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onErrorDismiss: () -> Unit
) {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is LoginUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is LoginUiState.SignIn -> {
                    SignInContent(
                        email = uiState.emailInput,
                        password = uiState.passwordInput,
                        showPasswordField = uiState.showPasswordField,
                        isPasswordVisible = uiState.isPasswordVisible,
                        emailError = uiState.emailError,
                        passwordError = uiState.passwordError,
                        isGoogleSignInLoading = uiState.isGoogleSignInLoading,
                        onEmailChange = onSignInEmailChange,
                        onPasswordChange = onSignInPasswordChange,
                        onTogglePasswordVisibility = onToggleSignInPasswordVisibility,
                        onContinueWithEmail = onContinueWithEmail,
                        onSignIn = onSignIn,
                        onGoBackToEmail = onGoBackToEmail,
                        onNavigateToSignUp = onNavigateToSignUp,
                        onGoogleSignInClick = onGoogleSignIn
                    )
                }

                is LoginUiState.SignUp -> {
                    SignUpContent(
                        email = uiState.emailInput,
                        password = uiState.passwordInput,
                        confirmPassword = uiState.confirmPasswordInput,
                        isPasswordVisible = uiState.isPasswordVisible,
                        isConfirmPasswordVisible = uiState.isConfirmPasswordVisible,
                        emailError = uiState.emailError,
                        passwordValidation = uiState.passwordValidation,
                        confirmPasswordError = uiState.confirmPasswordError,
                        isGoogleSignInLoading = uiState.isGoogleSignInLoading,
                        onEmailChange = onSignUpEmailChange,
                        onPasswordChange = onSignUpPasswordChange,
                        onConfirmPasswordChange = onSignUpConfirmPasswordChange,
                        onTogglePasswordVisibility = onToggleSignUpPasswordVisibility,
                        onToggleConfirmPasswordVisibility = onToggleSignUpConfirmPasswordVisibility,
                        onSignUp = onSignUp,
                        onNavigateToSignIn = onNavigateToSignIn,
                        onGoogleSignInClick = onGoogleSignIn
                    )
                }

                is LoginUiState.Error -> {
                    AlertDialog(
                        onDismissRequest = onErrorDismiss,
                        title = { Text(stringResource(Res.string.error)) },
                        text = { Text(uiState.message) },
                        confirmButton = {
                            TextButton(onClick = onErrorDismiss) {
                                Text(stringResource(Res.string.ok))
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
        }
    }
}
