package com.sedilant.yambol.ui.createTrain.stepsScreens.concepts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.draftTrain.TrainingDraftRepository
import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.ConceptDto
import com.sedilant.yambol.data.firestore.ConceptRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CreateTrainConceptsViewModel(
    private val draftRepository: TrainingDraftRepository,
    private val conceptRepository: ConceptRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _draftId = MutableStateFlow<String?>(null)


    val uiState: StateFlow<UiState> = _draftId
        .filterNotNull()
        .flatMapLatest { id ->
            authRepository.getAuthStateFlow().flatMapLatest { user ->
                val userId = user?.uid
                if (userId == null) {
                    flowOf(UiState.Error("User not logged in"))
                } else {
                    combine(
                        draftRepository.observeDraft(id),
                        conceptRepository.listByUserFlow(userId = userId)
                    ) { draft, concepts ->
                        if (draft != null) {
                            val listOfConcepts = concepts.map { concept ->
                                Concept(
                                    id = concept.id,
                                    conceptName = concept.name,
                                    isSelected = draft.conceptIds.contains(concept.id),
                                )
                            }
                            UiState.Success(concepts = listOfConcepts)
                        } else {
                            UiState.Error("No se pudo encontrar el borrador")
                        }
                    }
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

    fun onConceptSelected(conceptId: String) {
        val id = _draftId.value ?: return
        viewModelScope.launch {
            val draft = draftRepository.getDraft(id) ?: return@launch

            // IMPROVEMENT: Toggle logic (Add if missing, Remove if exists)
            val newConcepts = if (draft.conceptIds.contains(conceptId)) {
                draft.conceptIds.filter { it != conceptId }
            } else {
                draft.conceptIds + conceptId
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

        val userId = authRepository.currentUser?.uid ?: return

        viewModelScope.launch {
            val conceptId = conceptRepository.upsert(
                ConceptDto(
                    name = name,
                    userId = userId,
                )
            )
            onConceptSelected(conceptId)
        }
    }

    sealed interface UiState {
        data class Success(val concepts: List<Concept>) : UiState
        data class Error(val message: String) : UiState
        data object Loading : UiState
    }
}

data class Concept(
    val id: String,
    val conceptName: String,
    val isSelected: Boolean = false
)