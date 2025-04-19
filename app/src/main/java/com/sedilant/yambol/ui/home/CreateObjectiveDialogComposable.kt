package com.sedilant.yambol.ui.home

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
internal fun CreateObjectiveDialogComposable(
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var inputText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Enter New Objective")
        },
        text = {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Objective") },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    onSave(inputText)
                },
                enabled = inputText.isNotEmpty()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
