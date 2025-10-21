package com.sedilant.yambol.ui.createTrain.stepsScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.ui.createTrain.CreateTrainUiState
import com.sedilant.yambol.ui.createTrain.composables.CreateTrainScaffold

@Composable
fun ConceptsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    uiState: CreateTrainUiState,
    onConceptSelected: (String) -> Unit,
    onAddConcept: () -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    CreateTrainScaffold(
        title = "Crear entrenamiento",
        onCloseClick = onDismiss,
        onBottomButtonClick = onNext,
        bottomButtonText = "Siguiente",
        showBackButton = true,
        onBackClick = onBack
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "¿Qué conceptos quieres trabajar?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar conceptos") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Conceptos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Añadir concepto",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onAddConcept() }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.concepts) { concept ->
                    ConceptItem(
                        concept = concept,
                        isSelected = uiState.concepts.contains(concept),
                        onConceptSelected = { onConceptSelected(concept) }
                    )
                }
            }
        }
    }
}


@Composable
fun ConceptItem(
    concept: String,
    isSelected: Boolean,
    onConceptSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = concept)
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onConceptSelected() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ConceptsScreenPreview() {
    val concepts = listOf(
        "Tiro en suspensión",
        "Defensa individual",
        "Pase de pecho",
        "Rebote ofensivo",
        "Transición defensa-ataque"
    )

    var uiState by remember {
        mutableStateOf(
            CreateTrainUiState(
                concepts = concepts
            )
        )
    }
    ConceptsScreen(
        onBack = {},
        onNext = {},
        uiState = uiState,
        onConceptSelected = { selectedConcept ->
            val currentConcepts = uiState.concepts.toMutableList()
            if (currentConcepts.contains(selectedConcept)) {
                currentConcepts.remove(selectedConcept)
            } else {
                currentConcepts.add(selectedConcept)
            }
            uiState = uiState.copy(concepts = currentConcepts)
        },
        onAddConcept = {},
        onDismiss = {}
    )
}
