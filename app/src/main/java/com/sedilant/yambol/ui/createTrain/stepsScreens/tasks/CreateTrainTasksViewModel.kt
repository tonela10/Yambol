package com.sedilant.yambol.ui.createTrain.stepsScreens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.draftTrain.Task
import com.sedilant.yambol.data.draftTrain.TrainingDraftRepository
import com.sedilant.yambol.data.team.concept.ConceptRepository
import com.sedilant.yambol.domain.get.GetAllTaskUseCase
import com.sedilant.yambol.ui.createTrain.stepsScreens.concepts.Concept
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
class CreateTrainTasksViewModel @Inject constructor(
    private val draftRepository: TrainingDraftRepository,
    private val conceptsRepository: ConceptRepository,
  //  private val getAllTaskUseCase: GetAllTaskUseCase // Optional: if needed for existing exercises
) : ViewModel() {

    private val _draftId = MutableStateFlow<String?>(null)

    // Internal flow for transient errors (like failed network/db operations)
    private val _manualError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<UiStateNew> = _draftId
        .filterNotNull()
        .flatMapLatest { id ->
            combine(
                draftRepository.observeDraft(id),
                _manualError
            ) { draft, manualError ->
                when {
                    manualError != null -> UiStateNew.Error(manualError)
                    draft != null -> {
                        UiStateNew.Success(
                            tasks = draft.tasks.map { task ->
                                TaskUI(
                                    id = task.id,
                                    name = task.name,
                                    // Mapping IDs to names reactively
                                    concepts = conceptsRepository.getListOfConcepts(task.concepts)
                                        .map {
                                            Concept(
                                                id = it.id,
                                                conceptName = it.name,
                                            )
                                        },
                                    description = task.description,
                                    variation = task.variation,
                                    duration = "0"
                                )
                            },
                            listOfConcept = conceptsRepository.getListOfConcepts(draft.concepts)
                                .map { concept ->
                                    Concept(
                                        id = concept.id,
                                        conceptName = concept.name,
                                    )
                                },
                        )
                    }

                    else -> UiStateNew.Error("No se pudo encontrar el borrador")
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiStateNew.Loading
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
                _manualError.value = "Error al inicializar: ${e.message}"
            }
        }
    }

    /**
     * Add a new task directly to repository.
     * The uiState will update automatically via observeDraft.
     */
    fun onAddTask(
        name: String,
        description: String = "",
        variation: List<String> = emptyList(),
        concepts: List<Long> = emptyList()
    ) {
        val id = _draftId.value ?: return
        if (name.isBlank()) return

        viewModelScope.launch {
            try {
                val newTask = Task(
                    name = name.trim(),
                    concepts = concepts,
                    description = description.trim(),
                    variation = variation.toCommaSeparatedString().trim(),
                )
                draftRepository.addTask(id, newTask)
            } catch (e: Exception) {
                _manualError.value = "Error al añadir tarea"
            }
        }
    }

    /**
     * Reorder tasks by updating the whole list in the repository.
     *  // TODO adjust how to change the order in the repository
     */
    fun onTasksMove(from: Int, to: Int) {
        val currentState = uiState.value
        val id = _draftId.value ?: return

        if (currentState is UiStateNew.Success) {
            val currentTasks = currentState.tasks.toMutableList()
            if (from !in currentTasks.indices || to !in currentTasks.indices) return

            val movedTask = currentTasks.removeAt(from)
            currentTasks.add(to, movedTask)

            saveTasksOrder(id, currentTasks)
        }
    }

    // update the id's list in the draft
    private fun saveTasksOrder(id: String, tasks: List<TaskUI>) {
        viewModelScope.launch {
            try {
                // Re-mapping UI tasks back to Domain Tasks for the repository
                val domainTasks = tasks.map { taskUI ->
                    Task(
                        id = taskUI.id,
                        name = taskUI.name,
                        // Note: You might need to preserve concept IDs here
                        concepts = emptyList(), // TODO preserve the concepts here
                        description = taskUI.description,
                        variation = taskUI.variation
                    )
                }
                    draftRepository.updateTasksList(id, domainTasks)
            } catch (e: Exception) {
                _manualError.value = "Error al guardar orden"
            }
        }
    }

    /**
     * Remove a task. UI updates automatically.
     */
    fun onDeleteTask(taskId: String) {
        val id = _draftId.value ?: return
        viewModelScope.launch {
            try {
                draftRepository.removeTask(id, taskId)
            } catch (e: Exception) {
                _manualError.value = "Error al eliminar tarea"
            }
        }
    }

    fun clearError() {
        _manualError.value = null
    }

    sealed interface UiStateNew {
        data class Success(val tasks: List<TaskUI>, val listOfConcept: List<Concept>) : UiStateNew
        data class Error(val message: String) : UiStateNew
        data object Loading : UiStateNew
    }
}

private fun List<String>.toCommaSeparatedString(): String = joinToString(",")
