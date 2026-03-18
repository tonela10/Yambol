package com.sedilant.yambol.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.ui.theme.YambolTheme

@Composable
fun EditTeamDialog(
    currentTeamName: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var teamNameInput by remember { mutableStateOf(currentTeamName) }
    var isError by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    // Focus the text field when dialog opens
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Update error state when errorMessage changes
    LaunchedEffect(errorMessage) {
        isError = errorMessage != null
    }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text(
                text = "Edit Team Name",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter a new name for your team",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = teamNameInput,
                    onValueChange = {
                        teamNameInput = it
                        isError = false
                    },
                    label = { Text("Team Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (teamNameInput.trim()
                                    .isNotEmpty() && teamNameInput.trim() != currentTeamName
                            ) {
                                onSave(teamNameInput.trim())
                            }
                        }
                    ),
                    isError = isError,
                    supportingText = if (errorMessage != null) {
                        {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    singleLine = true
                )

                if (isError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Please enter a valid team name",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmedName = teamNameInput.trim()
                    when {
                        trimmedName.isEmpty() -> isError = true
                        trimmedName == currentTeamName -> onDismiss()
                        else -> onSave(trimmedName)
                    }
                },
                enabled = !isLoading && teamNameInput.trim().isNotEmpty(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isLoading) "Saving..." else "Save")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun EditTeamDialogPreview() {
    YambolTheme {
        EditTeamDialog(
            currentTeamName = "Utebo",
            onSave = {},
            onDismiss = {},
            errorMessage = null
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditTeamDialogWithErrorPreview() {
    YambolTheme {
        EditTeamDialog(
            currentTeamName = "Utebo",
            onSave = {},
            onDismiss = {},
            errorMessage = "Team name already exists"
        )
    }
}
