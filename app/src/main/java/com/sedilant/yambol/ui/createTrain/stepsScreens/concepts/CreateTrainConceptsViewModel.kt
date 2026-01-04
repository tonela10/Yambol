package com.sedilant.yambol.ui.createTrain.stepsScreens.concepts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.draftTrain.TrainingDraftRepository
import com.sedilant.yambol.data.team.concept.ConceptEntity
import com.sedilant.yambol.data.team.concept.ConceptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class CreateTrainConceptsViewModel @Inject constructor(
    private val draftRepository: TrainingDraftRepository,
    private val conceptRepository: ConceptRepository,
) : ViewModel() {

    private val _draftId = MutableStateFlow<String?>(null)


    val uiState: StateFlow<UiState> = _draftId
        .filterNotNull()
        .flatMapLatest { id ->
            combine(
                conceptRepository.getAllConcepts(),
                draftRepository.observeDraft(id)
            ) { concepts, draft ->
                if (draft != null) {
                    val listOfConcepts = concepts.map { concept ->
                        Concept(
                            id = concept.id,
                            conceptName = concept.name,
                            isSelected = draft.concepts.contains(concept.id),
                        )
                    }
                    UiState.Success(concepts = listOfConcepts)
                } else {
                    UiState.Error("No se pudo encontrar el borrador")
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                val id = draftRepository.getOrCreateActiveDraft()
                _draftId.value = id
            } catch (e: Exception) {
                // TODO If ID fails, the screen will stay Loading or you can emit an Error
            }
        }
    }

    fun onConceptSelected(conceptId: Long) {
        val id = _draftId.value ?: return
        viewModelScope.launch {
            val draft = draftRepository.getDraft(id) ?: return@launch

            // IMPROVEMENT: Toggle logic (Add if missing, Remove if exists)
            val newConcepts = if (draft.concepts.contains(conceptId)) {
                draft.concepts.filter { it != conceptId }
            } else {
                draft.concepts + conceptId
            }

            draftRepository.updateTrainingData(
                id = id,
                concepts = newConcepts,
            )
        }
    }

    fun onAddConcept(conceptName: String) {
        val name = conceptName.trim()
        if (name.isEmpty()) return

        viewModelScope.launch {
            conceptRepository.insertConcept(ConceptEntity(name = name))
        }
    }

    sealed interface UiState {
        data class Success(val concepts: List<Concept>) : UiState
        data class Error(val message: String) : UiState
        data object Loading : UiState
    }
}

data class Concept(
    val id: Long,
    val conceptName: String,
    val isSelected: Boolean = false
)