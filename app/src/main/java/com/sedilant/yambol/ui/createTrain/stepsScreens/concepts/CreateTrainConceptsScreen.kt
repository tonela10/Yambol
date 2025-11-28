package com.sedilant.yambol.ui.createTrain.stepsScreens.concepts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sedilant.yambol.ui.createTrain.commonComposables.CreateTrainScaffold

@Composable
fun CreateTrainConceptsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit,
    viewModel: CreateTrainConceptsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // TODO we can show here a snackBar or someting
            viewModel.clearError()
        }
    }

    CreateTrainConceptsScreenStateless(
        uiState = uiState,
        onBack = onBack,
        onNext = {
            viewModel.saveStepData()
            onNext()
        },
        onClose = onClose,
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
    onClose: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // TODO filter the concepts, we have two show all the concepts the app have
    val filteredConcepts = remember(uiState.conceptsList, searchQuery) {
        if (searchQuery.isBlank()) {
            uiState.conceptsList
        } else {
            uiState.conceptsList.filter {
                it.conceptName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    CreateTrainScaffold(
        title = "Crear entrenamiento",
        onCloseClick = onClose,
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

            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
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

            // Show selected concepts coutner
            if (uiState.selectedConcepts.isNotEmpty()) {
                Text(
                    text = "${uiState.selectedConcepts.size} concepto(s) seleccionado(s)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredConcepts) { concept ->
                    ConceptItem(
                        concept = concept.conceptName,
                        isSelected = concept.isSelected,
                        onConceptSelected = { onConceptSelected(concept.conceptName) }
                    )
                }

                // Message if there are no results from the search
                if (filteredConcepts.isEmpty() && searchQuery.isNotBlank()) {
                    item {
                        Text(
                            text = "No se encontraron conceptos",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Message if there are no concepts created
                if (uiState.conceptsList.isEmpty() && !uiState.isLoading) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No hay conceptos todavía",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Añade tu primer concepto",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                onAddConcept(conceptName)
                conceptName = "" // Clean after add something
            },
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
            .clickable { onConceptSelected() }
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
        onClose = {}
    )
}
