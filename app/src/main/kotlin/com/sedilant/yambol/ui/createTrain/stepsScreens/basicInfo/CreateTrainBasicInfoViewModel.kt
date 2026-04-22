package com.sedilant.yambol.ui.createTrain.stepsScreens.basicInfo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.draftTrain.TrainingDraftRepository
import com.sedilant.yambol.domain.get.GetTeamsUseCase
import com.sedilant.yambol.domain.models.TeamDomainModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/*
    TODO it is not recovering well the endTime
 */

@HiltViewModel(assistedFactory = CreateTrainBasicInfoViewModelFactory::class)
class CreateTrainBasicInfoViewModel @AssistedInject constructor(
    @Assisted private val teamId: String,
    private val repository: TrainingDraftRepository,
    private val getAllTeamsUseCase: GetTeamsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiStateNew>(UiStateNew.Loading)
    val uiState: StateFlow<UiStateNew> = _uiState.asStateFlow()

    private var draftId: String?
        get() = savedStateHandle.get<String>(KEY_DRAFT_ID)
        set(value) {
            savedStateHandle[KEY_DRAFT_ID] = value
        }

    init {
        loadOrCreateActiveDraft()
    }

    /**
     * Carga el borrador activo o crea uno nuevo si no existe
     */
    private fun loadOrCreateActiveDraft() {
        viewModelScope.launch {
            _uiState.value = UiStateNew.Loading

            try {
                val teamList = getAllTeamsUseCase().first()
                val id = repository.getOrCreateActiveDraft()
                draftId = id

                val draft = repository.getDraft(id)
                if (draft != null) {
                    _uiState.value = UiStateNew.Success(
                        selectedDate = draft.date,
                        startHour = draft.startTime,
                        endHour = draft.startTime + (draft.endTime / 60f), // change duration from endHour
                        teamsList = teamList,
                        selectedTeamId = teamId
                    )
                } else {
                    _uiState.value = UiStateNew.Success(
                        selectedDate = Clock.System.now(),
                        teamsList = teamList,
                        selectedTeamId = teamId,
                        startHour = getCurrentHourAsFloat(),
                        endHour = getCurrentHourAsFloat() + 1f,
                    )
                }
            } catch (e: Exception) {
                _uiState.value = UiStateNew.Error("Error al cargar el borrador: ${e.message}")
            }
        }
    }

    fun onDateSelected(date: Instant) {
        val currentState = _uiState.value
        if (currentState is UiStateNew.Success) {
            _uiState.value = currentState.copy(selectedDate = date)
            saveDateToRepository(date)
        }
    }

    fun onStartTimeChanged(hour: Int, minute: Int) {
        val hourAsFloat = hour + (minute / 60f)
        val currentState = _uiState.value
        if (currentState is UiStateNew.Success) {
            _uiState.value = currentState.copy(startHour = hourAsFloat)
            saveTimeToRepository(hourAsFloat)
        }
    }

    fun onEndTimeChanged(hour: Int, minute: Int) {
        val hourAsFloat = hour + (minute / 60f)
        val currentState = _uiState.value
        if (currentState is UiStateNew.Success) {
            _uiState.value = currentState.copy(endHour = hourAsFloat)
            saveDurationToRepository(hourAsFloat)
        }
    }

    /**
     * Actualiza el equipo seleccionado
     */
    fun onTeamSelected(teamId: String) {
        val currentState = _uiState.value
        if (currentState is UiStateNew.Success) {
            _uiState.value = currentState.copy(selectedTeamId = teamId)
        }
    }

    /**
     * Guarda todos los datos del paso actual antes de avanzar
     */
    fun saveStepData() {
        val id = draftId ?: return
        val currentState = _uiState.value
        if (currentState is UiStateNew.Success) {
            viewModelScope.launch {
                try {
                    repository.updateTrainingData(
                        id = id,
                        date = currentState.selectedDate,
                        startTime = currentState.startHour,
                        endTime = currentState.endHour,
                        teamId = currentState.selectedTeamId
                    )
                } catch (e: Exception) {
                    _uiState.value = UiStateNew.Error("Error al guardar: ${e.message}")
                }
            }
        }
    }

    // Private methods

    private fun saveDateToRepository(date: Instant) {
        val id = draftId ?: return

        viewModelScope.launch {
            try {
                repository.updateTrainingData(
                    id = id,
                    date = date
                )
            } catch (e: Exception) {
                // TODO Log error not silently
            }
        }
    }

    private fun saveTimeToRepository(hour: Float) {
        val id = draftId ?: return

        viewModelScope.launch {
            try {
                repository.updateTrainingData(
                    id = id,
                    startTime = hour
                )
            } catch (e: Exception) {
                // TODO  Log error not silently
            }
        }
    }

    private fun saveDurationToRepository(duration: Float) {
        val id = draftId ?: return

        viewModelScope.launch {
            try {
                repository.updateTrainingData(
                    id = id,
                    endTime = duration
                )
            } catch (e: Exception) {
                //TODO Log error not silently
            }
        }
    }

    private fun getCurrentHourAsFloat(): Float {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return now.hour + (now.minute / 60f)
    }

    sealed class UiStateNew {
        data object Loading : UiStateNew()
        data class Success(
            val selectedDate: Instant,
            val startHour: Float,
            val endHour: Float,
            val teamsList: List<TeamDomainModel>,
            val selectedTeamId: String
        ) : UiStateNew()
        data class Error(val message: String) : UiStateNew()
    }

    companion object {
        private const val KEY_DRAFT_ID = "draft_id"
    }
}

@AssistedFactory
interface CreateTrainBasicInfoViewModelFactory {
    fun create(teamId: String): CreateTrainBasicInfoViewModel
}
