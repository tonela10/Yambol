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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sedilant.yambol.R
import com.sedilant.yambol.ui.createTrain.commonComposables.CreateTrainScaffold
import com.sedilant.yambol.ui.createTrain.stepsScreens.concepts.CreateTrainConceptsViewModel.UiState

@Composable
fun CreateTrainConceptsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit,
    viewModel: CreateTrainConceptsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateTrainConceptsScreenStateless(
        uiState = uiState,
        onBack = onBack,
        onNext = onNext,
        onClose = onClose,
        onConceptSelected = viewModel::onConceptSelected,
        onAddConcept = viewModel::onAddConcept
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CreateTrainConceptsScreenStateless(
    uiState: UiState,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onConceptSelected: (String) -> Unit,
    onAddConcept: (String) -> Unit,
    onClose: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val selectedCount = (uiState as? UiState.Success)?.concepts?.count { it.isSelected } ?: 0

    CreateTrainScaffold(
        title = stringResource(R.string.create_training),
        onCloseClick = onClose,
        onBottomButtonClick = onNext,
        bottomButtonText = stringResource(R.string.next),
        showBackButton = true,
        onBackClick = onBack,
        bottomButtonEnabled = selectedCount > 0,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when (uiState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        LoadingIndicator()
                    }
                }

                is UiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = onBack) { Text(stringResource(R.string.back)) }
                    }
                }

                is UiState.Success -> {
                    val filteredConcepts = remember(uiState.concepts, searchQuery) {
                        if (searchQuery.isBlank()) {
                            uiState.concepts.toList()
                        } else {
                            uiState.concepts.filter {
                                it.conceptName.contains(searchQuery, ignoreCase = true)
                            }
                        }
                    }

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
                        text = stringResource(R.string.concepts_question),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.search_concepts)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(R.string.cd_search)
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
                            text = stringResource(R.string.concepts),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.add_concept),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showBottomSheet = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedCount > 0) {
                        Text(
                            text = stringResource(R.string.concepts_selected, selectedCount),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.error_concepts_required_to_continue),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filteredConcepts, key = { it.id }) { concept ->
                            ConceptItem(
                                concept = concept.conceptName,
                                isSelected = concept.isSelected,
                                onConceptSelected = { onConceptSelected(concept.id) }
                            )
                        }

                        if (filteredConcepts.isEmpty() && searchQuery.isNotBlank()) {
                            item {
                                Text(
                                    text = stringResource(R.string.no_concepts_found),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
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
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text(
            stringResource(R.string.add_concept),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = conceptName,
            onValueChange = { conceptName = it },
            label = { Text(stringResource(R.string.concept_name)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onAddConcept(conceptName) },
            modifier = Modifier.fillMaxWidth(),
            enabled = conceptName.isNotBlank()
        ) {
            Text(stringResource(R.string.save))
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
        Checkbox(checked = isSelected, onCheckedChange = { onConceptSelected() })
    }
}

@Preview(showBackground = true)
@Composable
fun ConceptsScreenPreview() {
    val sampleConcepts = listOf(
        Concept(id = "1", conceptName = "Tiro en suspensión", isSelected = true),
        Concept(id = "2", conceptName = "Defensa individual", isSelected = false),
        Concept(id = "3", conceptName = "Pase de pecho", isSelected = true),
        Concept(id = "4", conceptName = "Rebote ofensivo", isSelected = false),
        Concept(id = "5", conceptName = "Transición defensa-ataque", isSelected = true)
    )

    CreateTrainConceptsScreenStateless(
        uiState = UiState.Success(
            concepts = sampleConcepts
        ),
        onBack = {},
        onNext = {},
        onConceptSelected = { _ -> },
        onAddConcept = { _ -> },
        onClose = {}
    )
}
