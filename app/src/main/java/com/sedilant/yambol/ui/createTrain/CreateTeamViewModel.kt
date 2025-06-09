package com.sedilant.yambol.ui.createTrain

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.domain.get.GetTeamsUseCase
import com.sedilant.yambol.domain.insert.CreateTrainTaskUseCase
import com.sedilant.yambol.domain.insert.CreateTrainUseCase
import com.sedilant.yambol.domain.models.TeamDomainModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.Date

@HiltViewModel(assistedFactory = CreateTrainViewModelFactory::class)
class CreateTrainViewModel @AssistedInject constructor(
    @Assisted private val defaultTeamId: Int,
    private val savedStateHandle: SavedStateHandle,
    private val createTrainUseCase: CreateTrainUseCase,
    private val createTrainTaskUseCase: CreateTrainTaskUseCase,
    private val getAllTeamsUseCase: GetTeamsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTrainUiState())
    val uiState: StateFlow<CreateTrainUiState> = _uiState.asStateFlow()

    companion object {
        private const val KEY_CURRENT_STEP = "current_step"
        private const val KEY_SELECTED_DATE = "selected_date"
        private const val KEY_SELECTED_HOURS = "selected_hours"
        private const val KEY_SELECTED_MINUTES = "selected_minutes"
        private const val KEY_SELECTED_TEAM_ID = "selected_team_id"
        private const val KEY_CONCEPTS = "concepts"
        private const val KEY_TASKS = "tasks"
    }

    init {
        loadTeams()
        restoreState()
    }

    private fun loadTeams() {
        viewModelScope.launch {
            try {
                val teams = getAllTeamsUseCase().first()
                _uiState.update { currentState ->
                    currentState.copy(
                        teams = teams,
                        selectedTeamId = savedStateHandle.get<Int>(KEY_SELECTED_TEAM_ID)
                            ?: defaultTeamId,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = "Failed to load teams: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Restore the data when the user leaves the app in background and start it again
     */
    private fun restoreState() {
        val currentStep = savedStateHandle.get<Int>(KEY_CURRENT_STEP) ?: 0
        val selectedDate = savedStateHandle.get<Long>(KEY_SELECTED_DATE)?.let { Date(it) }
        val selectedHours = savedStateHandle.get<Int>(KEY_SELECTED_HOURS) ?: 1
        val selectedMinutes = savedStateHandle.get<Int>(KEY_SELECTED_MINUTES) ?: 30
        val selectedTeamId = savedStateHandle.get<Int>(KEY_SELECTED_TEAM_ID) ?: defaultTeamId
        val concepts = savedStateHandle.get<Array<String>>(KEY_CONCEPTS)?.toList() ?: emptyList()
        val tasks = savedStateHandle.get<Array<TrainTaskData>>(KEY_TASKS)?.toList() ?: emptyList()

        _uiState.update { currentState ->
            currentState.copy(
                currentStep = CreateTrainStep.entries[currentStep],
                selectedDate = selectedDate,
                selectedHours = selectedHours,
                selectedMinutes = selectedMinutes,
                selectedTeamId = selectedTeamId,
                concepts = concepts,
                tasks = tasks.toMutableList()
            )
        }
    }

    fun nextStep() {
        val currentStep = _uiState.value.currentStep
        val nextStepOrdinal =
            (currentStep.ordinal + 1).coerceAtMost(CreateTrainStep.entries.size - 1)
        val nextStep = CreateTrainStep.entries[nextStepOrdinal]

        _uiState.update { it.copy(currentStep = nextStep) }
        savedStateHandle[KEY_CURRENT_STEP] = nextStepOrdinal
    }

    fun previousStep() {
        val currentStep = _uiState.value.currentStep
        val previousStepOrdinal = (currentStep.ordinal - 1).coerceAtLeast(0)
        val previousStep = CreateTrainStep.entries[previousStepOrdinal]

        _uiState.update { it.copy(currentStep = previousStep) }
        savedStateHandle[KEY_CURRENT_STEP] = previousStepOrdinal
    }

    fun updateDate(date: Date) {
        _uiState.update { it.copy(selectedDate = date) }
        savedStateHandle[KEY_SELECTED_DATE] = date.time
    }

    fun updateHours(hours: Int) {
        _uiState.update { it.copy(selectedHours = hours) }
        savedStateHandle[KEY_SELECTED_HOURS] = hours
    }

    fun updateMinutes(minutes: Int) {
        _uiState.update { it.copy(selectedMinutes = minutes) }
        savedStateHandle[KEY_SELECTED_MINUTES] = minutes
    }

    fun updateTeam(teamId: Int) {
        _uiState.update { it.copy(selectedTeamId = teamId) }
        savedStateHandle[KEY_SELECTED_TEAM_ID] = teamId
    }

    fun addConcept(concept: String) {
        if (concept.isNotBlank() && !_uiState.value.concepts.contains(concept)) {
            val updatedConcepts = _uiState.value.concepts + concept
            _uiState.update { it.copy(concepts = updatedConcepts) }
            savedStateHandle[KEY_CONCEPTS] = updatedConcepts.toTypedArray()
        }
    }

    fun removeConcept(concept: String) {
        val updatedConcepts = _uiState.value.concepts - concept
        _uiState.update { it.copy(concepts = updatedConcepts) }
        savedStateHandle[KEY_CONCEPTS] = updatedConcepts.toTypedArray()
    }

    fun addTask(task: TrainTaskData) {
        val updatedTasks = _uiState.value.tasks.toMutableList().apply { add(task) }
        _uiState.update { it.copy(tasks = updatedTasks) }
        savedStateHandle[KEY_TASKS] = updatedTasks.toTypedArray()
    }

    fun removeTask(index: Int) {
        val updatedTasks = _uiState.value.tasks.toMutableList().apply { removeAt(index) }
        _uiState.update { it.copy(tasks = updatedTasks) }
        savedStateHandle[KEY_TASKS] = updatedTasks.toTypedArray()
    }

    fun updateTask(index: Int, task: TrainTaskData) {
        val updatedTasks = _uiState.value.tasks.toMutableList().apply { set(index, task) }
        _uiState.update { it.copy(tasks = updatedTasks) }
        savedStateHandle[KEY_TASKS] = updatedTasks.toTypedArray()
    }

    fun canProceedFromBasicInfo(): Boolean {
        with(_uiState.value) {
            return selectedDate != null && selectedTeamId != -1
        }
    }

    fun canProceedFromConcepts(): Boolean {
        return _uiState.value.concepts.isNotEmpty()
    }

    private fun getDurationInHours(): Float {
        with(_uiState.value) {
            return selectedHours + (selectedMinutes / 60.0f)
        }
    }

    fun saveTrain() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val state = _uiState.value

                // Create the train and use the id to save relation in many to many relation table
                val trainId = createTrainUseCase(
                    date = state.selectedDate!!,
                    time = getDurationInHours(),
                    concepts = state.concepts,
                    teamId = state.selectedTeamId
                )

                // Create tasks if any
                state.tasks.forEach { taskData ->
                    createTrainTaskUseCase(
                        trainId = trainId,
                        name = taskData.name,
                        numberOfPlayer = taskData.numberOfPlayer,
                        concept = taskData.concept,
                        description = taskData.description,
                        variables = taskData.variables
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isTrainSaved = true
                    )
                }

                clearSavedState()

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to save training: ${e.message}"
                    )
                }
            }
        }
    }

    private fun clearSavedState() {
        savedStateHandle.remove<Int>(KEY_CURRENT_STEP)
        savedStateHandle.remove<Long>(KEY_SELECTED_DATE)
        savedStateHandle.remove<Int>(KEY_SELECTED_HOURS)
        savedStateHandle.remove<Int>(KEY_SELECTED_MINUTES)
        savedStateHandle.remove<Int>(KEY_SELECTED_TEAM_ID)
        savedStateHandle.remove<Array<String>>(KEY_CONCEPTS)
        savedStateHandle.remove<Array<TrainTaskData>>(KEY_TASKS)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class CreateTrainUiState(
    val currentStep: CreateTrainStep = CreateTrainStep.BASIC_INFO,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isTrainSaved: Boolean = false,

    val teams: List<TeamDomainModel> = emptyList(),

    val selectedDate: Date? = null,
    val selectedHours: Int = 1,
    val selectedMinutes: Int = 30,
    val selectedTeamId: Int = -1,

    val concepts: List<String> = emptyList(),

    val tasks: MutableList<TrainTaskData> = mutableListOf()
)

enum class CreateTrainStep(val title: String, val stepNumber: Int) {
    BASIC_INFO("Basic Information", 1),
    CONCEPTS("Training Concepts", 2),
    TASKS("Training Tasks", 3),
    REVIEW("Review & Save", 4)
}

@Serializable
data class TrainTaskData(
    val name: String,
    val numberOfPlayer: Int,
    val concept: String,
    val description: String,
    val variables: List<String>
)

@AssistedFactory
interface CreateTrainViewModelFactory {
    fun create(defaultTeamId: Int): CreateTrainViewModel
}
