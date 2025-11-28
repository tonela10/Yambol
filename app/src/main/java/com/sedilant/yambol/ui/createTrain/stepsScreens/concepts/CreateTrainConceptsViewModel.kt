package com.sedilant.yambol.ui.createTrain.stepsScreens.concepts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.draftTrain.TrainingDraftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTrainConceptsViewModel @Inject constructor(
    private val repository: TrainingDraftRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Get automatically from the repository
    private var draftId: String? = null

    init {
        // Load the draft automatically
        loadActiveDraft()
    }

    /**
     * Carga el borrador activo
     */
    private fun loadActiveDraft() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val id = repository.getOrCreateActiveDraft()
                draftId = id

                val draft = repository.getDraft(id)
                if (draft != null) {
                    // Transform the saved concepts to an UI list
                    val concepts = draft.concepts.map { conceptName ->
                        Concept(
                            conceptName = conceptName,
                            isSelected = true
                        )
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            conceptsList = concepts,
                            selectedConcepts = draft.concepts.toSet()
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar conceptos: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Add a new concepts to the list
     */
    fun onAddConcept(conceptName: String) {
        if (conceptName.isBlank()) return

        val trimmedName = conceptName.trim()

        // Check if the concepts already exists
        if (_uiState.value.conceptsList.any {
                it.conceptName.equals(
                    trimmedName,
                    ignoreCase = true
                )
            }) {
            _uiState.update { it.copy(error = "Este concepto ya existe") }
            return
        }

        val newConcept = Concept(
            conceptName = trimmedName,
            isSelected = true // By default we added selected
        )

        _uiState.update { currentState ->
            currentState.copy(
                conceptsList = currentState.conceptsList + newConcept,
                selectedConcepts = currentState.selectedConcepts + trimmedName
            )
        }

        // Save on the repository
        saveConceptsToRepository()
    }

    /**
     * Check/uncheck a concept
     */
    fun onConceptSelected(conceptName: String) {
        _uiState.update { currentState ->
            val updatedList = currentState.conceptsList.map { concept ->
                if (concept.conceptName == conceptName) {
                    concept.copy(isSelected = !concept.isSelected)
                } else {
                    concept
                }
            }

            val selectedConcepts = updatedList
                .filter { it.isSelected }
                .map { it.conceptName }
                .toSet()

            currentState.copy(
                conceptsList = updatedList,
                selectedConcepts = selectedConcepts
            )
        }

        // Save on the repository
        saveConceptsToRepository()
    }

    /**
     * Save the selected concepts on the repository
     */
    private fun saveConceptsToRepository() {
        val id = draftId ?: return

        viewModelScope.launch {
            try {
                val selectedConcepts = _uiState.value.selectedConcepts.toList()
                repository.updateTrainingData(
                    id = id,
                    concepts = selectedConcepts
                )
            } catch (e: Exception) {
                // Log error silently
            }
        }
    }

    /**
     * Save the concepts before go to the next step
     */
    fun saveStepData() {
        val id = draftId ?: return

        viewModelScope.launch {
            try {
                val selectedConcepts = _uiState.value.selectedConcepts.toList()
                repository.updateTrainingData(
                    id = id,
                    concepts = selectedConcepts
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Error al guardar conceptos: ${e.message}")
                }
            }
        }
    }

    /**
     * Clean the error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val conceptsList: List<Concept> = emptyList(),
        val selectedConcepts: Set<String> = emptySet()
    )
}

data class Concept(
    val conceptName: String,
    val isSelected: Boolean
)