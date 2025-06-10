package com.sedilant.yambol.ui.playerCard.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.domain.Position
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import com.sedilant.yambol.ui.theme.YambolTheme

data class EditPlayerData(
    val name: String,
    val number: String,
    val position: Position
)

@Composable
fun EditPlayerBottomSheet(
    modifier: Modifier = Modifier,
    player: PlayerUiModel,
    onSave: (EditPlayerData) -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
) {
    var playerName by remember { mutableStateOf(player.name) }
    var playerNumber by remember { mutableStateOf(player.number) }
    var selectedPosition by remember { mutableStateOf(player.position) }
    var isNameError by remember { mutableStateOf(false) }
    var isNumberError by remember { mutableStateOf(false) }
    var numberErrorMessage by remember { mutableStateOf("") }

    // Update error states when errorMessage changes
    LaunchedEffect(errorMessage) {
        when {
            errorMessage?.contains("name", ignoreCase = true) == true -> {
                isNameError = true
                isNumberError = false
            }

            errorMessage?.contains("number", ignoreCase = true) == true -> {
                isNumberError = true
                isNameError = false
                numberErrorMessage = errorMessage
            }

            errorMessage != null -> {
                isNameError = true
                isNumberError = true
            }

            else -> {
                isNameError = false
                isNumberError = false
                numberErrorMessage = ""
            }
        }
    }

    val positions = listOf(
        Position.POINT_GUARD to "Point Guard",
        Position.SHOOTING_GUARD to "Shooting Guard",
        Position.SMALL_FORWARD to "Small Forward",
        Position.POWER_FORWARD to "Power Forward",
        Position.CENTER to "Center"
    )

    fun validateAndSave() {
        val trimmedName = playerName.trim()
        val numberInt = playerNumber.toIntOrNull()

        isNameError = trimmedName.isEmpty()
        isNumberError = numberInt == null || numberInt < 0 || numberInt > 99

        if (!isNameError && !isNumberError && numberInt != null) {
            onSave(
                EditPlayerData(
                    name = trimmedName,
                    number = playerNumber,
                    position = selectedPosition
                )
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp)// Extra padding for better bottom sheet appearance
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Edit Player",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Update player information",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }

        // Current Player Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )

                Column {
                    Text(
                        text = "Current: ${player.name} #${player.number}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = positions.find { it.first == player.position }?.second ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Form Fields
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Player Name Field
            OutlinedTextField(
                value = playerName,
                onValueChange = {
                    playerName = it
                    isNameError = false
                },
                label = { Text("Player Name") },
                placeholder = { Text("Enter player name") },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words
                ),
                isError = isNameError,
                supportingText = if (isNameError) {
                    { Text("Please enter a valid player name") }
                } else null,
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                singleLine = true
            )

            // Jersey Number Field
            OutlinedTextField(
                value = playerNumber,
                onValueChange = { number ->
                    if (number.isEmpty() || (number.toIntOrNull()?.let { it in 0..99 } == true)) {
                        playerNumber = number
                        isNumberError = false
                        numberErrorMessage = ""
                    }
                },
                label = { Text("Jersey Number") },
                placeholder = { Text("0-99") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        validateAndSave()
                    }
                ),
                isError = isNumberError,
                supportingText = if (isNumberError && numberErrorMessage.isNotEmpty()) {
                    { Text(numberErrorMessage) }
                } else if (isNumberError) {
                    { Text("Please enter a valid jersey number (0-99)") }
                } else null,
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                singleLine = true
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Position",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        positions.forEachIndexed { index, (position, displayName) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .selectable(
                                        selected = selectedPosition == position,
                                        onClick = { selectedPosition = position }
                                    )
                                    .then(
                                        if (selectedPosition == position) {
                                            Modifier.border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                        } else Modifier
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = selectedPosition == position,
                                    onClick = null,
                                    enabled = !isLoading,
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary,
                                        unselectedColor = MaterialTheme.colorScheme.outline
                                    )
                                )

                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (selectedPosition == position) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },
                                    fontWeight = if (selectedPosition == position) {
                                        FontWeight.Medium
                                    } else {
                                        FontWeight.Normal
                                    },
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            }

                            // Add divider between items (except for the last item)
                            if (index < positions.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (errorMessage != null && !isNameError && !isNumberError) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { validateAndSave() },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isLoading) "Saving Changes..." else "Save Changes",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            OutlinedButton(
                onClick = onDismiss,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditPlayerBottomSheetPreview() {
    YambolTheme {
        EditPlayerBottomSheet(
            player = PlayerUiModel(
                name = "Carlos Canut",
                number = "23",
                position = Position.POINT_GUARD,
                id = 1
            ),
            onSave = {},
            onDismiss = {},
            errorMessage = null
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditPlayerBottomSheetWithErrorPreview() {
    YambolTheme {
        EditPlayerBottomSheet(
            player = PlayerUiModel(
                name = "Carlos Canut",
                number = "23",
                position = Position.POINT_GUARD,
                id = 1
            ),
            onSave = {},
            onDismiss = {},
            errorMessage = "Jersey number 23 is already taken"
        )
    }
}
