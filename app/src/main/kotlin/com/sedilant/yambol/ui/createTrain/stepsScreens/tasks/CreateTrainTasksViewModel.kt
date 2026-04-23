package com.sedilant.yambol.ui.createTrain.stepsScreens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.draftTrain.Task
import com.sedilant.yambol.data.draftTrain.TrainingDraftRepository
import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.ConceptDto
import com.sedilant.yambol.data.firestore.ConceptRepository
import com.sedilant.yambol.domain.get.GetAllTaskUseCase
import com.sedilant.yambol.domain.models.TaskDomain
import com.sedilant.yambol.ui.createTrain.stepsScreens.concepts.Concept
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

/**
 * This ViewModel is expose an ui state that have all existing tasks ( todo with the current concepts)
 * create new tasks in and expose the draft task to the ui
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CreateTrainTasksViewModel(
    private val draftRepository: TrainingDraftRepository,
    private val conceptsRepository: ConceptRepository,
    private val getAllTaskUseCase: GetAllTaskUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _draftId = MutableStateFlow<String?>(null)

    // Internal flow for transient errors (like failed network/db operations)
    private val _manualError = MutableStateFlow<String?>(null)
    private val _taskConceptError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<UiStateNew> = _draftId
        .filterNotNull()
        .flatMapLatest { id ->
            authRepository.getAuthStateFlow().flatMapLatest { user ->
                val userId = user?.uid
                if (userId == null) {
                    flowOf(UiStateNew.Error("User not logged in"))
                } else {
                    combine(
                        draftRepository.observeDraft(id),
                        _manualError,
                        _taskConceptError,
                        getAllTaskUseCase(),
                        conceptsRepository.listByUserFlow(userId)
                    ) { draft, manualError, conceptError, existingTasksList, allConceptDtos ->
                        when {
                            manualError != null -> UiStateNew.Error(manualError)
                            draft == null -> UiStateNew.Error("No se pudo encontrar el borrador")
                            else -> buildSuccessState(
                                draftTasks = draft.tasks,
                                selectedDraftConceptIds = draft.conceptIds,
                                existingTasksList = existingTasksList,
                                allConceptDtos = allConceptDtos,
                                conceptError = conceptError
                            )
                        }
                    }
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

    private fun buildSuccessState(
        draftTasks: List<Task>,
        selectedDraftConceptIds: List<String>,
        existingTasksList: List<TaskDomain>,
        allConceptDtos: List<ConceptDto>,
        conceptError: String?
    ): UiStateNew.Success {
        val selectedConceptIds = selectedDraftConceptIds.distinct().filter { it.isNotBlank() }
        val selectedConceptSet = selectedConceptIds.toSet()

        val allConceptById = allConceptDtos.associateBy { it.id }
        val selectedConcepts = selectedConceptIds.mapNotNull { conceptId ->
            allConceptById[conceptId]?.toUiConcept()
        }

        val filteredExistingTasks = if (selectedConceptSet.isEmpty()) {
            emptyList()
        } else {
            existingTasksList.filter { existingTask ->
                existingTask.concepts.any(selectedConceptSet::contains)
            }
        }

        return UiStateNew.Success(
            draftTasks = draftTasks.map { task -> task.toTaskUi(allConceptById) },
            listOfConcept = selectedConcepts,
            existingTasks = filteredExistingTasks.map { existingTask ->
                TaskUI(
                    id = existingTask.trainingTaskId,
                    name = existingTask.name,
                    concepts = existingTask.concepts.mapNotNull { conceptId ->
                        allConceptById[conceptId]?.toUiConcept()
                    },
                    description = existingTask.description,
                    variation = existingTask.variables.sorted().joinToString(","),
                    duration = "0"
                )
            },
            taskConceptError = conceptError
        )
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
        concepts: List<String> = emptyList()
    ) {
        val id = _draftId.value ?: return
        if (name.isBlank()) return

        val sanitizedConcepts = concepts.distinct().filter { it.isNotBlank() }
        if (sanitizedConcepts.isEmpty()) {
            _taskConceptError.value = "TASK_CONCEPT_REQUIRED"
            return
        }

        viewModelScope.launch {
            try {
                _taskConceptError.value = null
                val newTask = Task(
                    name = name.trim(),
                    concepts = sanitizedConcepts,
                    description = description.trim(),
                    variation = variation.toCommaSeparatedString().trim(),
                )
                draftRepository.addTask(id, newTask)
            } catch (_: Exception) {
                _manualError.value = "Error al añadir tarea"
            }
        }
    }

    fun onTaskSelected(task: TaskUI) {
        val id = _draftId.value ?: return
        if (task.name.isBlank()) return

        val selectedConcepts = task.concepts.map { it.id }.distinct().filter { it.isNotBlank() }
        if (selectedConcepts.isEmpty()) {
            _taskConceptError.value = "TASK_CONCEPT_REQUIRED"
            return
        }

        viewModelScope.launch {
            try {
                _taskConceptError.value = null
                val newTask = Task(
                    id = task.id,
                    name = task.name.trim(),
                    concepts = selectedConcepts,
                    description = task.description.trim(),
                    variation = task.variation,
                )
                draftRepository.addTask(id, newTask)
            } catch (_: Exception) {
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
            val currentTasks = currentState.draftTasks.toMutableList()
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
                val domainTasks = tasks.map { taskUI ->
                    Task(
                        id = taskUI.id,
                        name = taskUI.name,
                        concepts = taskUI.concepts.map { it.id },
                        description = taskUI.description,
                        variation = taskUI.variation
                    )
                }
                draftRepository.updateTasksList(id, domainTasks)
            } catch (_: Exception) {
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
            } catch (_: Exception) {
                _manualError.value = "Error al eliminar tarea"
            }
        }
    }

    fun clearError() {
        _manualError.value = null
    }

    fun clearTaskConceptError() {
        _taskConceptError.value = null
    }

    sealed interface UiStateNew {
        data class Success(
            val draftTasks: List<TaskUI>,
            val listOfConcept: List<Concept>,
            val existingTasks: List<TaskUI>,
            val taskConceptError: String? = null
        ) : UiStateNew

        data class Error(val message: String) : UiStateNew
        data object Loading : UiStateNew
    }
}

private fun List<String>.toCommaSeparatedString(): String = joinToString(",")

private fun ConceptDto.toUiConcept(): Concept = Concept(
    id = id,
    conceptName = name
)

private fun Task.toTaskUi(conceptById: Map<String, ConceptDto>): TaskUI = TaskUI(
    id = id,
    name = name,
    concepts = concepts.mapNotNull { conceptId ->
        conceptById[conceptId]?.toUiConcept()
    },
    description = description,
    variation = variation,
    duration = "0"
)
