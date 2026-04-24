package com.sedilant.yambol.feature.createTrain.stepsScreens.concepts

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.core.designsystem.Res
import com.sedilant.yambol.core.designsystem.*
import com.sedilant.yambol.feature.createTrain.commonComposables.CreateTrainScaffold
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTrainConceptsScreenStateless(
    uiState: ConceptsUiState,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onConceptSelected: (String) -> Unit,
    onAddConcept: (String) -> Unit,
    onClose: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val selectedCount = (uiState as? ConceptsUiState.Success)?.concepts?.count { it.isSelected } ?: 0

    CreateTrainScaffold(
        title = stringResource(Res.string.create_training),
        onCloseClick = onClose,
        onBottomButtonClick = onNext,
        bottomButtonText = stringResource(Res.string.next),
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
                is ConceptsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is ConceptsUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = onBack) { Text(stringResource(Res.string.back)) }
                    }
                }

                is ConceptsUiState.Success -> {
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
                        text = stringResource(Res.string.concepts_question),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(Res.string.search_concepts)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(Res.string.cd_search)
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
                            text = stringResource(Res.string.concepts),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(Res.string.add_concept),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showBottomSheet = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedCount > 0) {
                        Text(
                            text = stringResource(Res.string.concepts_selected, selectedCount),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    } else {
                        Text(
                            text = stringResource(Res.string.error_concepts_required_to_continue),
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
                                    text = stringResource(Res.string.no_concepts_found),
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
            stringResource(Res.string.add_concept),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = conceptName,
            onValueChange = { conceptName = it },
            label = { Text(stringResource(Res.string.concept_name)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onAddConcept(conceptName) },
            modifier = Modifier.fillMaxWidth(),
            enabled = conceptName.isNotBlank()
        ) {
            Text(stringResource(Res.string.save))
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
