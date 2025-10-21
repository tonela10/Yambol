package com.sedilant.yambol.ui.createTrain.stepsScreens.concepts

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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sedilant.yambol.ui.createTrain.CreateTrainUiState
import com.sedilant.yambol.ui.createTrain.commonComposables.CreateTrainScaffold

@Composable
fun ConceptsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    onDismiss: () -> Unit,
    viewModel: CreateTrainConceptsViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateTrainConceptsScreenStateless(
        uiState = uiState,
        onBack = onBack,
        onNext = onNext,
        onDismiss = onDismiss,
        onConceptSelected = viewModel::onConceptSelected,
        onAddConcept = viewModel::onAddConcept
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTrainConceptsScreenStateless(
    uiState: CreateTrainConceptsViewModel.UiState,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onConceptSelected: (String) -> Unit,
    onAddConcept: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    CreateTrainScaffold(
        title = "Crear entrenamiento",
        onCloseClick = onDismiss,
        onBottomButtonClick = onNext,
        bottomButtonText = "Siguiente",
        showBackButton = true,
        onBackClick = onBack,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState
                ) {
                    AddConceptBottomSheet(
                        onAddConcept = {
                            onAddConcept(it)
                            showBottomSheet = false
                        }
                    )
                }
            }

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
                    modifier = Modifier.clickable { showBottomSheet = true }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.conceptsList) { concept ->
                    ConceptItem(
                        concept = concept.conceptName,
                        isSelected = uiState.conceptsList.contains(concept),
                        onConceptSelected = { onConceptSelected(concept.conceptName) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AddConceptBottomSheet(onAddConcept: (String) -> Unit) {
    var conceptName by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Añadir concepto",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = conceptName,
            onValueChange = { conceptName = it },
            label = { Text("Nombre del concepto") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onAddConcept(conceptName) },
            modifier = Modifier.fillMaxWidth(),
            enabled = conceptName.isNotBlank()
        ) {
            Text(text = "Guardar")
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

    CreateTrainConceptsScreenStateless(
        uiState = CreateTrainConceptsViewModel.UiState(
            conceptsList = concepts.map {
                Concept(
                    conceptName = it,
                    isSelected = true
                )
            }
        ),
        onBack = {},
        onNext = {},
        onConceptSelected = { _ -> },
        onAddConcept = { _ -> },
        onDismiss = {}
    )
}
