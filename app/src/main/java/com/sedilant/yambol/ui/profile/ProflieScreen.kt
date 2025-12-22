package com.sedilant.yambol.ui.profile

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseUser

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onAuthSuccess: (FirebaseUser) -> Unit = {}
) {
    val authState by viewModel.profileUiState.collectAsState()

    // Handle auth state changes
    LaunchedEffect(authState) {
        if (authState is ProfileUiState.Authenticated) {
            val user = (authState as ProfileUiState.Authenticated).user
            onAuthSuccess(user)
        }
    }

    ProfileScreenStateless(
        uiState = authState,
        onSignInClick = {},
        onSignOutClick = { context ->
            if (context is ComponentActivity) {
                viewModel.signOut(context)
            }
        },
        onDeleteAccountClick = { context ->
            if (context is ComponentActivity) {
                viewModel.deleteAccount(context)
            }
        },
        handleSignInResult = { result ->
            viewModel.handleSignInResult(result)
        },
        getSignInIntent = viewModel::getSignInIntent,
        onRetryClick = {
            viewModel.checkAuthStatus()
        }
    )
}

@Composable
private fun ProfileScreenStateless(
    uiState: ProfileUiState,
    onSignInClick: () -> Unit,
    onSignOutClick: (Context) -> Unit,
    onDeleteAccountClick: (Context) -> Unit,
    getSignInIntent: () -> Intent,
    handleSignInResult: (FirebaseAuthUIAuthenticationResult) -> Unit,
    onRetryClick: () -> Unit
) {
    val context = LocalContext.current
    // Register the launcher for FirebaseUI sign-in
    val signInLauncher = rememberLauncherForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        handleSignInResult(result)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is ProfileUiState.Initial, ProfileUiState.Unauthenticated -> {
                SignInContent(
                    onSignInClick = {
                        val signInIntent = getSignInIntent()
                        signInLauncher.launch(signInIntent)
                    }
                )
            }

            is ProfileUiState.Loading -> {
                CircularProgressIndicator()
            }

            is ProfileUiState.Authenticated -> {
                AuthenticatedContent(
                    user = uiState.user,
                    onSignOutClick = { onSignOutClick(context) },
                    onDeleteAccountClick = { onDeleteAccountClick(context) }
                )
            }

            is ProfileUiState.Error -> {
                ErrorContent(
                    errorMessage = uiState.message,
                    onRetryClick = onRetryClick
                )
            }
        }
    }
}

@Composable
private fun SignInContent(onSignInClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Welcome",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.bodyLarge
        )

        Button(
            onClick = onSignInClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Sign In")
        }
    }
}

@Composable
private fun AuthenticatedContent(
    user: FirebaseUser,
    onSignOutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Welcome back!",
            style = MaterialTheme.typography.headlineMedium
        )

        user.displayName?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleLarge
            )
        }

        user.email?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSignOutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Sign Out")
        }

        OutlinedButton(
            onClick = onDeleteAccountClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Delete Account")
        }
    }
}

@Composable
private fun ErrorContent(
    errorMessage: String,
    onRetryClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = onRetryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Retry")
        }
    }
}
