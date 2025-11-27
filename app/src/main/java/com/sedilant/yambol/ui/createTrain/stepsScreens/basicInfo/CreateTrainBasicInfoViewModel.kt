package com.sedilant.yambol.ui.createTrain.stepsScreens.basicInfo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.draftTrain.Training
import com.sedilant.yambol.data.draftTrain.TrainingDraftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateTrainBasicInfoViewModel @Inject constructor(
    private val repository: TrainingDraftRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    public val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // ID del borrador actual - se guarda en SavedStateHandle para sobrevivir a process death
    private var draftId: String?
        get() = savedStateHandle.get<String>(KEY_DRAFT_ID)
        set(value) {
            savedStateHandle[KEY_DRAFT_ID] = value
        }

    init {
        // Si ya existe un borrador, cargar sus datos
        draftId?.let { draftId ->
            loadDraft(draftId)
        }
    }

    /**
     * Inicializa un nuevo borrador de entrenamiento
     * Se llama desde el NavGraph cuando se inicia el flow
     */
    public fun initializeDraft() {
        if (draftId != null) return // Ya existe un borrador

        viewModelScope.launch {
            try {
                val newTraining = Training(
                    date = Date(), // Fecha actual por defecto
                    duration = 90f, // 1h 30min por defecto
                    hour = getCurrentHourAsFloat(),
                    concepts = emptyList(),
                    tasks = emptyList()
                )

                val draftId = repository.createDraft(newTraining)
                this@CreateTrainBasicInfoViewModel.draftId = draftId

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al crear el borrador: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Carga un borrador existente
     */
    private fun loadDraft(draftId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val draft = repository.getDraft(draftId)
                if (draft != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            selectedDate = draft.date,
                            selectedHour = draft.hour,
                            selectedDuration = draft.duration
                        )
                    }
                } else {
                    // El borrador no existe, crear uno nuevo
                    this@CreateTrainBasicInfoViewModel.draftId = null
                    initializeDraft()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar el borrador: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Actualiza la fecha seleccionada
     */
    public fun onDateSelected(date: Date) {
        _uiState.update { it.copy(selectedDate = date) }
        saveDateToRepository(date)
    }

    /**
     * Actualiza la hora seleccionada
     */
    public fun onTimeChanged(hour: Int, minute: Int) {
        val hourAsFloat = hour + (minute / 60f)
        _uiState.update { it.copy(selectedHour = hourAsFloat) }
        saveTimeToRepository(hourAsFloat)
    }

    /**
     * Actualiza la duración seleccionada
     */
    public fun onDurationChanged(hour: Int, minute: Int) {
        val durationInMinutes = (hour * 60) + minute
        _uiState.update { it.copy(selectedDuration = durationInMinutes.toFloat()) }
        saveDurationToRepository(durationInMinutes.toFloat())
    }

    /**
     * Actualiza el equipo seleccionado
     */
    public fun onTeamSelected(team: String) {
        _uiState.update { it.copy(selectedTeam = team) }
        // El team se puede guardar como concepto o en otro campo según tu modelo
    }

    /**
     * Guarda todos los datos del paso actual antes de avanzar
     */
    public fun saveStepData() {
        val draftId = draftId ?: return

        viewModelScope.launch {
            try {
                repository.updateTrainingData(
                    id = draftId,
                    date = _uiState.value.selectedDate,
                    hour = _uiState.value.selectedHour,
                    duration = _uiState.value.selectedDuration
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Error al guardar: ${e.message}")
                }
            }
        }
    }

    // Métodos privados para guardar en Repository de forma reactiva

    private fun saveDateToRepository(date: Date) {
        val draftId = draftId ?: return

        viewModelScope.launch {
            try {
                repository.updateTrainingData(
                    id = draftId,
                    date = date
                )
            } catch (e: Exception) {
                // Log error silently
            }
        }
    }

    private fun saveTimeToRepository(hour: Float) {
        val draftId = draftId ?: return

        viewModelScope.launch {
            try {
                repository.updateTrainingData(
                    id = draftId,
                    hour = hour
                )
            } catch (e: Exception) {
                // Log error silently
            }
        }
    }

    private fun saveDurationToRepository(duration: Float) {
        val draftId = draftId ?: return

        viewModelScope.launch {
            try {
                repository.updateTrainingData(
                    id = draftId,
                    duration = duration
                )
            } catch (e: Exception) {
                // Log error silently
            }
        }
    }

    /**
     * Limpia el error mostrado
     */
    public fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun getCurrentHourAsFloat(): Float {
        val calendar = java.util.Calendar.getInstance()
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = calendar.get(java.util.Calendar.MINUTE)
        return hour + (minute / 60f)
    }

    data class UiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val teamsList: List<String> = emptyList(),
        val selectedTeam: String = "",
        val selectedDate: Date = Date(),
        val selectedHour: Float = 0f, // Hora en formato decimal (ej: 17.5 = 17:30)
        val selectedDuration: Float = 90f // Duración en minutos
    )

    companion object {
        private const val KEY_DRAFT_ID = "draft_id"
    }
}