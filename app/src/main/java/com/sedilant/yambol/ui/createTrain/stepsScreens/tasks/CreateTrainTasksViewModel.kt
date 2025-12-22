package com.sedilant.yambol.ui.createTrain.stepsScreens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.draftTrain.TrainingDraftRepository
import com.sedilant.yambol.data.draftTrain.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTrainTasksViewModel @Inject constructor(
    private val repository: TrainingDraftRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var draftId: String? = null

    init {
        loadActiveDraft()
    }

    /**
     * Load the active draft and its tasks
     */
    private fun loadActiveDraft() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val id = repository.getOrCreateActiveDraft()
                draftId = id

                val draft = repository.getDraft(id)
                if (draft != null) {
                    val tasksUI = draft.tasks.map { task ->
                        TaskUI(
                            id = task.id,
                            name = task.name,
                            concepts = task.concepts,
                            description = task.description,
                            variation = task.variation,
                            duration = ""
                        )
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            tasksList = tasksUI
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar tareas: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Add a new task
     */
    fun onAddTask(
        name: String,
        description: String = "",
        variation: List<String> = emptyList(),
        concepts: List<String> = emptyList()
    ) {
        val id = draftId ?: return
        if (name.isBlank()) return

        viewModelScope.launch {
            try {
                val newTask = Task(
                    name = name.trim(),
                    concepts = concepts,
                    description = description.trim(),
                    variation = variation.toCommaSeparatedString().trim(),
                )

                repository.addTask(id, newTask)

                val newTaskUI = TaskUI(
                    id = newTask.id,
                    name = newTask.name,
                    concepts = newTask.concepts,
                    description = newTask.description,
                    variation = newTask.variation,
                    duration = ""
                )

                _uiState.update { currentState ->
                    currentState.copy(
                        tasksList = currentState.tasksList + newTaskUI
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Error al añadir tarea: ${e.message}")
                }
            }
        }
    }

    /**
     * Reorder the task when the user moves it
     */
    fun onTasksMove(from: Int, to: Int) {
        val currentTasks = _uiState.value.tasksList.toMutableList()
        if (from < 0 || from >= currentTasks.size || to < 0 || to >= currentTasks.size) {
            return
        }

        // Reorder memory
        val movedTask = currentTasks.removeAt(from)
        currentTasks.add(to, movedTask)

        _uiState.update { it.copy(tasksList = currentTasks) }

        // Save the new orden in the repository
        saveTasksOrder(currentTasks)
    }

    /**
     * Save the tasks order in the repository
     */
    private fun saveTasksOrder(tasks: List<TaskUI>) {
        val id = draftId ?: return

        viewModelScope.launch {
            try {
                val domainTasks = tasks.map { taskUI ->
                    Task(
                        id = taskUI.id,
                        name = taskUI.name,
                        concepts = taskUI.concepts,
                        description = taskUI.description,
                        variation = taskUI.variation
                    )
                }

                domainTasks.forEachIndexed { index, task ->
                    repository.updateTask(id, task)
                }
            } catch (e: Exception) {
                // Log error silently
            }
        }
    }

    /**
     * Remove a task
     */
    fun onDeleteTask(taskId: String) {
        val id = draftId ?: return

        viewModelScope.launch {
            try {
                repository.removeTask(id, taskId)

                _uiState.update { currentState ->
                    currentState.copy(
                        tasksList = currentState.tasksList.filter { it.id != taskId }
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Error al eliminar tarea: ${e.message}")
                }
            }
        }
    }

    /**
     * Guarda el paso actual antes de avanzar al siguiente
     *  TODO remove this method
     */
    fun saveStepData() {
        // Las tareas ya se guardan automáticamente al añadirlas/reordenarlas
        // Este método existe por consistencia con los otros ViewModels
    }

    /**
     * Clean the shown error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val tasksList: List<TaskUI> = emptyList()
    )
}

private fun List<String>.toCommaSeparatedString(): String {
    return this.joinToString(separator = ",")
}