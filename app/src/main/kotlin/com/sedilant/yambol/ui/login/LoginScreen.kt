package com.sedilant.yambol.ui.login

import co.touchlab.kermit.Logger
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import org.koin.androidx.compose.koinViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.sedilant.yambol.feature.login.LoginScreenStateless
import kotlinx.coroutines.launch

private const val WEB_CLIENT_ID = "448568418891-4vq7apk7c16o5s69qg6gvns664oc8cif.apps.googleusercontent.com"

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
