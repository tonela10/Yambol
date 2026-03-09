package com.sedilant.yambol.ui.profile

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
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sedilant.yambol.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ProfileScreenStateless(
        uiState = uiState,
        onSignOut = viewModel::signOut,
        onShowDeleteConfirmation = viewModel::showDeleteConfirmation,
        onHideDeleteConfirmation = viewModel::hideDeleteConfirmation,
        onConfirmDelete = viewModel::deleteAccount,
        onErrorDismiss = viewModel::clearError
    )
}

@Composable
private fun ProfileScreenStateless(
    uiState: ProfileUiState,
    onSignOut: () -> Unit,
    onShowDeleteConfirmation: () -> Unit,
    onHideDeleteConfirmation: () -> Unit,
    onConfirmDelete: () -> Unit,
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

                is ProfileUiState.Error -> {
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
                contentDescription = stringResource(R.string.cd_user_avatar),
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
                text = stringResource(R.string.account_information),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(
                icon = Icons.Default.Email,
                label = stringResource(R.string.email_label),
                value = email
            )
            Spacer(modifier = Modifier.height(12.dp))

            InfoRow(
                icon = if (isVerified) Icons.Default.CheckCircle else Icons.Default.Warning,
                label = stringResource(R.string.email_verified_label),
                value = if (isVerified) stringResource(R.string.yes) else stringResource(R.string.no)
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
            Text(text = stringResource(R.string.sign_out), style = MaterialTheme.typography.bodyLarge)
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
            Text(text = stringResource(R.string.delete_account), style = MaterialTheme.typography.bodyLarge)
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
        title = { Text(text = stringResource(R.string.delete_account_title), style = MaterialTheme.typography.titleLarge) },
        text = {
            Text(
                text = stringResource(R.string.delete_account_message),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text(stringResource(R.string.delete)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
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
        onSignOut = {},
        onShowDeleteConfirmation = {},
        onHideDeleteConfirmation = {},
        onConfirmDelete = {},
        onErrorDismiss = {}
    )
}

@Preview(showBackground = true, name = "Error State")
@Composable
private fun ProfileScreenErrorPreview() {
    ProfileScreenStateless(
        uiState = ProfileUiState.Error(message = "Authentication failed. Please check your credentials."),
        onSignOut = {},
        onShowDeleteConfirmation = {},
        onHideDeleteConfirmation = {},
        onConfirmDelete = {},
        onErrorDismiss = {}
    )
}
