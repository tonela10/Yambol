package com.sedilant.yambol.ui.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

private const val WEB_CLIENT_ID = "448568418891-4vq7apk7c16o5s69qg6gvns664oc8cif.apps.googleusercontent.com"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
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
                // User cancelled
                Log.d("ProfileScreen", "Google Sign-In cancelled by user")
            } catch (e: Exception) {
                Log.e("ProfileScreen", "Google Sign-In failed", e)
                viewModel.onGoogleSignInFailed(e.message ?: "Google Sign-In failed")
            }
        }
    }

    ProfileScreenStateless(
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
        onSignOut = viewModel::signOut,
        onShowDeleteConfirmation = viewModel::showDeleteConfirmation,
        onHideDeleteConfirmation = viewModel::hideDeleteConfirmation,
        onConfirmDelete = viewModel::deleteAccount,
        onErrorDismiss = viewModel::clearError,
        onResendVerification = viewModel::resendVerificationEmail,
        onCheckVerification = viewModel::checkEmailVerification,
        onCancelVerification = viewModel::cancelVerificationAndSignOut
    )
}

@Composable
private fun ProfileScreenStateless(
    uiState: ProfileUiState,
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
    onSignOut: () -> Unit,
    onShowDeleteConfirmation: () -> Unit,
    onHideDeleteConfirmation: () -> Unit,
    onConfirmDelete: () -> Unit,
    onErrorDismiss: () -> Unit,
    onResendVerification: () -> Unit,
    onCheckVerification: () -> Unit,
    onCancelVerification: () -> Unit
) {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is ProfileUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is ProfileUiState.Authenticated -> {
                    AuthenticatedContent(
                        userName = uiState.userName,
                        userEmail = uiState.userEmail,
                        isEmailVerified = uiState.isEmailVerified,
                        onShowDeleteConfirmation = onShowDeleteConfirmation,
                        onSignOutClick = onSignOut
                    )

                    if (uiState.showDeleteConfirmation) {
                        DeleteAccountDialog(
                            onConfirm = onConfirmDelete,
                            onDismiss = onHideDeleteConfirmation
                        )
                    }
                }

                is ProfileUiState.SignIn -> {
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

                is ProfileUiState.SignUp -> {
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

                is ProfileUiState.EmailVerificationPending -> {
                    EmailVerificationContent(
                        email = uiState.email,
                        isResending = uiState.isResending,
                        isChecking = uiState.isChecking,
                        message = uiState.message,
                        onResendClick = onResendVerification,
                        onCheckVerificationClick = onCheckVerification,
                        onCancelClick = onCancelVerification
                    )
                }

                is ProfileUiState.Error -> {
                    AlertDialog(
                        onDismissRequest = onErrorDismiss,
                        title = { Text("Error") },
                        text = { Text(uiState.message) },
                        confirmButton = {
                            TextButton(onClick = onErrorDismiss) {
                                Text("OK")
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

@Composable
private fun AuthenticatedContent(
    userName: String,
    userEmail: String,
    isEmailVerified: Boolean,
    onShowDeleteConfirmation: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // User Avatar
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User Avatar",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // User Name
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // User Email
        Text(
            text = userEmail,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Account Information Card
        UserInfoCard(
            email = userEmail,
            isVerified = isEmailVerified
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        ActionButtons(
            onSignOutClick = onSignOutClick,
            onDeleteAccountClick = onShowDeleteConfirmation
        )
    }
}

@Composable
private fun UserInfoCard(
    email: String,
    isVerified: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Account Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(
                icon = Icons.Default.Email,
                label = "Email",
                value = email
            )
            Spacer(modifier = Modifier.height(12.dp))

            InfoRow(
                icon = if (isVerified) Icons.Default.CheckCircle else Icons.Default.Warning,
                label = "Email Verified",
                value = if (isVerified) "Yes" else "No"
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ActionButtons(
    onSignOutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onSignOutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Sign Out", style = MaterialTheme.typography.bodyLarge)
        }

        OutlinedButton(
            onClick = onDeleteAccountClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Delete Account", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun DeleteAccountDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text(text = "Delete Account?", style = MaterialTheme.typography.titleLarge) },
        text = {
            Text(
                text = "This action cannot be undone. All your data will be permanently deleted.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Delete") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun EmailVerificationContent(
    email: String,
    isResending: Boolean,
    isChecking: Boolean,
    message: String?,
    onResendClick: () -> Unit,
    onCheckVerificationClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MarkEmailRead,
                contentDescription = "Email Verification",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Verify Your Email",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "We've sent a verification email to:",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Please check your inbox and click the verification link to complete your registration.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        message?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (it.contains("sent", ignoreCase = true))
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (it.contains("sent", ignoreCase = true))
                            Icons.Default.CheckCircle
                        else
                            Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (it.contains("sent", ignoreCase = true))
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (it.contains("sent", ignoreCase = true))
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onCheckVerificationClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isChecking && !isResending,
            shape = MaterialTheme.shapes.medium
        ) {
            if (isChecking) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = if (isChecking) "Checking..." else "I've Verified My Email",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onResendClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isResending && !isChecking,
            shape = MaterialTheme.shapes.medium
        ) {
            if (isResending) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = if (isResending) "Sending..." else "Resend Verification Email",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = onCancelClick,
            enabled = !isResending && !isChecking
        ) {
            Text(
                text = "Cancel and Sign Out",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

// ======================= PREVIEWS =======================

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun ProfileScreenLoadingPreview() {
    ProfileScreenStateless(
        uiState = ProfileUiState.Loading,
        onSignInEmailChange = {},
        onSignInPasswordChange = {},
        onToggleSignInPasswordVisibility = {},
        onContinueWithEmail = {},
        onSignIn = {},
        onGoBackToEmail = {},
        onSignUpEmailChange = {},
        onSignUpPasswordChange = {},
        onSignUpConfirmPasswordChange = {},
        onToggleSignUpPasswordVisibility = {},
        onToggleSignUpConfirmPasswordVisibility = {},
        onSignUp = {},
        onNavigateToSignUp = {},
        onNavigateToSignIn = {},
        onGoogleSignIn = {},
        onSignOut = {},
        onShowDeleteConfirmation = {},
        onHideDeleteConfirmation = {},
        onConfirmDelete = {},
        onErrorDismiss = {},
        onResendVerification = {},
        onCheckVerification = {},
        onCancelVerification = {}
    )
}

@Preview(showBackground = true, name = "Sign In - Email Step")
@Composable
private fun ProfileScreenSignInEmailPreview() {
    ProfileScreenStateless(
        uiState = ProfileUiState.SignIn(
            emailInput = "user@example.com",
            showPasswordField = false
        ),
        onSignInEmailChange = {},
        onSignInPasswordChange = {},
        onToggleSignInPasswordVisibility = {},
        onContinueWithEmail = {},
        onSignIn = {},
        onGoBackToEmail = {},
        onSignUpEmailChange = {},
        onSignUpPasswordChange = {},
        onSignUpConfirmPasswordChange = {},
        onToggleSignUpPasswordVisibility = {},
        onToggleSignUpConfirmPasswordVisibility = {},
        onSignUp = {},
        onNavigateToSignUp = {},
        onNavigateToSignIn = {},
        onGoogleSignIn = {},
        onSignOut = {},
        onShowDeleteConfirmation = {},
        onHideDeleteConfirmation = {},
        onConfirmDelete = {},
        onErrorDismiss = {},
        onResendVerification = {},
        onCheckVerification = {},
        onCancelVerification = {}
    )
}

@Preview(showBackground = true, name = "Sign In - Password Step")
@Composable
private fun ProfileScreenSignInPasswordPreview() {
    ProfileScreenStateless(
        uiState = ProfileUiState.SignIn(
            emailInput = "user@example.com",
            showPasswordField = true
        ),
        onSignInEmailChange = {},
        onSignInPasswordChange = {},
        onToggleSignInPasswordVisibility = {},
        onContinueWithEmail = {},
        onSignIn = {},
        onGoBackToEmail = {},
        onSignUpEmailChange = {},
        onSignUpPasswordChange = {},
        onSignUpConfirmPasswordChange = {},
        onToggleSignUpPasswordVisibility = {},
        onToggleSignUpConfirmPasswordVisibility = {},
        onSignUp = {},
        onNavigateToSignUp = {},
        onNavigateToSignIn = {},
        onGoogleSignIn = {},
        onSignOut = {},
        onShowDeleteConfirmation = {},
        onHideDeleteConfirmation = {},
        onConfirmDelete = {},
        onErrorDismiss = {},
        onResendVerification = {},
        onCheckVerification = {},
        onCancelVerification = {}
    )
}

@Preview(showBackground = true, name = "Sign Up")
@Composable
private fun ProfileScreenSignUpPreview() {
    ProfileScreenStateless(
        uiState = ProfileUiState.SignUp(
            emailInput = "newuser@example.com",
            passwordInput = "pass1!",
            confirmPasswordInput = "",
            passwordValidation = PasswordValidation(
                isValid = true,
                hasMinLength = true,
                hasSymbol = true,
                hasNumber = true
            )
        ),
        onSignInEmailChange = {},
        onSignInPasswordChange = {},
        onToggleSignInPasswordVisibility = {},
        onContinueWithEmail = {},
        onSignIn = {},
        onGoBackToEmail = {},
        onSignUpEmailChange = {},
        onSignUpPasswordChange = {},
        onSignUpConfirmPasswordChange = {},
        onToggleSignUpPasswordVisibility = {},
        onToggleSignUpConfirmPasswordVisibility = {},
        onSignUp = {},
        onNavigateToSignUp = {},
        onNavigateToSignIn = {},
        onGoogleSignIn = {},
        onSignOut = {},
        onShowDeleteConfirmation = {},
        onHideDeleteConfirmation = {},
        onConfirmDelete = {},
        onErrorDismiss = {},
        onResendVerification = {},
        onCheckVerification = {},
        onCancelVerification = {}
    )
}

@Preview(showBackground = true, name = "Sign Up - Password Validation")
@Composable
private fun ProfileScreenSignUpValidationPreview() {
    ProfileScreenStateless(
        uiState = ProfileUiState.SignUp(
            emailInput = "newuser@example.com",
            passwordInput = "pass",
            confirmPasswordInput = "",
            passwordValidation = PasswordValidation(
                isValid = false,
                hasMinLength = false,
                hasSymbol = false,
                hasNumber = false
            )
        ),
        onSignInEmailChange = {},
        onSignInPasswordChange = {},
        onToggleSignInPasswordVisibility = {},
        onContinueWithEmail = {},
        onSignIn = {},
        onGoBackToEmail = {},
        onSignUpEmailChange = {},
        onSignUpPasswordChange = {},
        onSignUpConfirmPasswordChange = {},
        onToggleSignUpPasswordVisibility = {},
        onToggleSignUpConfirmPasswordVisibility = {},
        onSignUp = {},
        onNavigateToSignUp = {},
        onNavigateToSignIn = {},
        onGoogleSignIn = {},
        onSignOut = {},
        onShowDeleteConfirmation = {},
        onHideDeleteConfirmation = {},
        onConfirmDelete = {},
        onErrorDismiss = {},
        onResendVerification = {},
        onCheckVerification = {},
        onCancelVerification = {}
    )
}

@Preview(showBackground = true, name = "Email Verification Pending")
@Composable
private fun ProfileScreenEmailVerificationPreview() {
    ProfileScreenStateless(
        uiState = ProfileUiState.EmailVerificationPending(
            email = "newuser@example.com",
            verificationSent = true,
            message = "Verification email sent! Please check your inbox."
        ),
        onSignInEmailChange = {},
        onSignInPasswordChange = {},
        onToggleSignInPasswordVisibility = {},
        onContinueWithEmail = {},
        onSignIn = {},
        onGoBackToEmail = {},
        onSignUpEmailChange = {},
        onSignUpPasswordChange = {},
        onSignUpConfirmPasswordChange = {},
        onToggleSignUpPasswordVisibility = {},
        onToggleSignUpConfirmPasswordVisibility = {},
        onSignUp = {},
        onNavigateToSignUp = {},
        onNavigateToSignIn = {},
        onGoogleSignIn = {},
        onSignOut = {},
        onShowDeleteConfirmation = {},
        onHideDeleteConfirmation = {},
        onConfirmDelete = {},
        onErrorDismiss = {},
        onResendVerification = {},
        onCheckVerification = {},
        onCancelVerification = {}
    )
}

@Preview(showBackground = true, name = "Authenticated")
@Composable
private fun ProfileScreenAuthenticatedPreview() {
    ProfileScreenStateless(
        uiState = ProfileUiState.Authenticated(
            userName = "John Doe",
            userEmail = "john.doe@example.com",
            isEmailVerified = true,
            photoUrl = null,
            showDeleteConfirmation = false
        ),
        onSignInEmailChange = {},
        onSignInPasswordChange = {},
        onToggleSignInPasswordVisibility = {},
        onContinueWithEmail = {},
        onSignIn = {},
        onGoBackToEmail = {},
        onSignUpEmailChange = {},
        onSignUpPasswordChange = {},
        onSignUpConfirmPasswordChange = {},
        onToggleSignUpPasswordVisibility = {},
        onToggleSignUpConfirmPasswordVisibility = {},
        onSignUp = {},
        onNavigateToSignUp = {},
        onNavigateToSignIn = {},
        onGoogleSignIn = {},
        onSignOut = {},
        onShowDeleteConfirmation = {},
        onHideDeleteConfirmation = {},
        onConfirmDelete = {},
        onErrorDismiss = {},
        onResendVerification = {},
        onCheckVerification = {},
        onCancelVerification = {}
    )
}

@Preview(showBackground = true, name = "Error State")
@Composable
private fun ProfileScreenErrorPreview() {
    ProfileScreenStateless(
        uiState = ProfileUiState.Error(message = "Authentication failed. Please check your credentials."),
        onSignInEmailChange = {},
        onSignInPasswordChange = {},
        onToggleSignInPasswordVisibility = {},
        onContinueWithEmail = {},
        onSignIn = {},
        onGoBackToEmail = {},
        onSignUpEmailChange = {},
        onSignUpPasswordChange = {},
        onSignUpConfirmPasswordChange = {},
        onToggleSignUpPasswordVisibility = {},
        onToggleSignUpConfirmPasswordVisibility = {},
        onSignUp = {},
        onNavigateToSignUp = {},
        onNavigateToSignIn = {},
        onGoogleSignIn = {},
        onSignOut = {},
        onShowDeleteConfirmation = {},
        onHideDeleteConfirmation = {},
        onConfirmDelete = {},
        onErrorDismiss = {},
        onResendVerification = {},
        onCheckVerification = {},
        onCancelVerification = {}
    )
}

