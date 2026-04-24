package com.sedilant.yambol.ui.login

import co.touchlab.kermit.Logger
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import org.koin.androidx.compose.koinViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.sedilant.yambol.R
import com.sedilant.yambol.ui.login.SignInContent
import com.sedilant.yambol.ui.login.SignUpContent
import kotlinx.coroutines.launch

private const val WEB_CLIENT_ID = "448568418891-4vq7apk7c16o5s69qg6gvns664oc8cif.apps.googleusercontent.com"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val onGoogleSignIn: () -> Unit = {
        coroutineScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(WEB_CLIENT_ID)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                viewModel.signInWithGoogle(idToken)
            } catch (e: GetCredentialCancellationException) {
                Logger.d("LoginScreen") { "Google Sign-In cancelled by user" }
            } catch (e: Exception) {
                Logger.e("LoginScreen", e) { "Google Sign-In failed" }
                viewModel.onGoogleSignInFailed(e.message ?: "Google Sign-In failed")
            }
        }
    }

    LoginScreenStateless(
        uiState = uiState,
        onSignInEmailChange = viewModel::onSignInEmailChange,
        onSignInPasswordChange = viewModel::onSignInPasswordChange,
        onToggleSignInPasswordVisibility = viewModel::toggleSignInPasswordVisibility,
        onContinueWithEmail = viewModel::continueWithEmail,
        onSignIn = viewModel::signIn,
        onGoBackToEmail = viewModel::goBackToEmail,
        onSignUpEmailChange = viewModel::onSignUpEmailChange,
        onSignUpPasswordChange = viewModel::onSignUpPasswordChange,
        onSignUpConfirmPasswordChange = viewModel::onSignUpConfirmPasswordChange,
        onToggleSignUpPasswordVisibility = viewModel::toggleSignUpPasswordVisibility,
        onToggleSignUpConfirmPasswordVisibility = viewModel::toggleSignUpConfirmPasswordVisibility,
        onSignUp = viewModel::signUp,
        onNavigateToSignUp = viewModel::navigateToSignUp,
        onNavigateToSignIn = viewModel::navigateToSignIn,
        onGoogleSignIn = onGoogleSignIn,
        onErrorDismiss = viewModel::clearError
    )
}

@Composable
private fun LoginScreenStateless(
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
                        title = { Text(stringResource(R.string.error)) },
                        text = { Text(uiState.message) },
                        confirmButton = {
                            TextButton(onClick = onErrorDismiss) {
                                Text(stringResource(R.string.ok))
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
