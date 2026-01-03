package com.sedilant.yambol.ui.createTrain.stepsScreens.trainDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.draftTrain.TrainingDraftRepository
import com.sedilant.yambol.domain.insert.CreateTrainTaskUseCase
import com.sedilant.yambol.domain.insert.CreateTrainUseCase
import com.sedilant.yambol.ui.createTrain.stepsScreens.tasks.TaskUI
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel(assistedFactory = CreateTrainDetailsViewModelFactory::class)
class CreateTrainDetailsViewModel @AssistedInject constructor(
    @Assisted private val teamId: Long,
    private val draftRepository: TrainingDraftRepository,
    private val createTrainUseCase: CreateTrainUseCase,
    private val createTrainTaskUseCase: CreateTrainTaskUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<CreateTrainDetailsUiState>(CreateTrainDetailsUiState.Loading)
    val uiState: StateFlow<CreateTrainDetailsUiState> = _uiState.asStateFlow()

    // ID del borrador
    private var draftId: String? = null

    init {
        loadTrainingDetails()
    }

    /**
     * Load the details from the trainning.
     */
    private fun loadTrainingDetails() {
        viewModelScope.launch {
            try {
                val id = draftRepository.getOrCreateActiveDraft()
                draftId = id
                draftId?.let {
                    draftRepository.observeDraft(it).collect { draft ->
                        if (draft != null) {
                            val trainInfo = TrainInfo(
                                date = formatDate(draft.date),
                                hour = formatHour(draft.startTime),
                                duration = formatDuration(draft.endTime),
                                team = "Equipo A" // TODO: Obtener del draft cuando se implemente
                            )

                            // Convertir tareas a TaskUI
                            val tasksUI = draft.tasks.map { task ->
                                TaskUI(
                                    id = task.id,
                                    name = task.name,
                                    concepts = listOf(), // TODO task.concepts,
                                    description = task.description,
                                    variation = task.variation,
                                    duration = "" // Sin duración específica
                                )
                            }
                            _uiState.value = CreateTrainDetailsUiState.Success(
                                trainInfo = trainInfo,
                                listOfTasks = tasksUI,
                                listOfConcepts = listOf("") // TODO draft.concepts
                            )
                        } else {
                            _uiState.value = CreateTrainDetailsUiState.Error(
                                Exception("No se encontró el borrador del entrenamiento")
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = CreateTrainDetailsUiState.Error(e)
            }
        }
    }

    /**
     * End and save the training
     * Here, we have to save all the task in the main data base and save the all train
     * With the current Team id save the training
     */
    fun saveTraining() {
        val id = draftId ?: return

        viewModelScope.launch {
            try {
                _uiState.update {
                    if (it is CreateTrainDetailsUiState.Success) {
                        it.copy(isSaving = true)
                    } else {
                        it
                    }
                }

                val draft = draftRepository.getDraft(id)

                val teamId = teamId
                val trainId = createTrainUseCase(
                    date = draft?.date ?: Date(),
                    time = draft?.startTime ?: 0f,
                    concepts = listOf(), // TODO ?.concepts ?: emptyList(),
                    teamId = teamId
                )

                draft?.tasks?.forEach { task ->
                    createTrainTaskUseCase(
                        trainId = trainId,
                        name = task.name,
                        numberOfPlayer = 0,
                        concept = listOf(),// TODO task.concepts.firstOrNull() ?: "",
                        description = task.description,
                        variables = task.variation.split(",")
                    )
                }

                // Finalizar el borrador (marcarlo como completado)
                draftRepository.finalizeDraft(id)

                _uiState.update {
                    if (it is CreateTrainDetailsUiState.Success) {
                        it.copy(isSaving = false, trainingSaved = true)
                    } else {
                        it
                    }
                }
            } catch (e: Exception) {
                _uiState.value = CreateTrainDetailsUiState.Error(e)
            }
        }
    }


    // Utils, format data methods
    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        return format.format(date)
    }

    private fun formatHour(hour: Float): String {
        val hours = hour.toInt()
        val minutes = ((hour - hours) * 60).toInt()
        return String.format("%02d:%02d", hours, minutes)
    }

    private fun formatDuration(durationInMinutes: Float): String {
        val hours = (durationInMinutes / 60).toInt()
        val minutes = (durationInMinutes % 60).toInt()

        return when {
            hours > 0 && minutes > 0 -> "$hours hora${if (hours > 1) "s" else ""} $minutes minutos"
            hours > 0 -> "$hours hora${if (hours > 1) "s" else ""}"
            else -> "$minutes minutos"
        }
    }
}

sealed interface CreateTrainDetailsUiState {
    data object Loading : CreateTrainDetailsUiState

    data class Error(val throwable: Throwable) : CreateTrainDetailsUiState

    data class Success(
        val trainInfo: TrainInfo,
        val listOfTasks: List<TaskUI>,
        val listOfConcepts: List<String>,
        val isSaving: Boolean = false,
        val trainingSaved: Boolean = false
    ) : CreateTrainDetailsUiState
}

data class TrainInfo(
    val date: String = "",
    val hour: String = "",
    val duration: String = "",
    val team: String = ""
)

@AssistedFactory
interface CreateTrainDetailsViewModelFactory {
    fun create(teamId: Long): CreateTrainDetailsViewModel
}