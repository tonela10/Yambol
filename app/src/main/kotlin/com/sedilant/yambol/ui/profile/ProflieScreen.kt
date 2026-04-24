package com.sedilant.yambol.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.sedilant.yambol.feature.profile.ProfileScreenStateless
import com.sedilant.yambol.feature.profile.ProfileUiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel()
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
