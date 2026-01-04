package com.sedilant.yambol.ui.createTrain.stepsScreens.trainDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.draftTrain.TrainingDraftRepository
import com.sedilant.yambol.data.team.concept.ConceptRepository
import com.sedilant.yambol.domain.get.GetTeamsUseCase
import com.sedilant.yambol.domain.insert.CreateTrainTaskUseCase
import com.sedilant.yambol.domain.insert.CreateTrainUseCase
import com.sedilant.yambol.ui.createTrain.stepsScreens.concepts.Concept
import com.sedilant.yambol.ui.createTrain.stepsScreens.tasks.TaskUI
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel(assistedFactory = CreateTrainDetailsViewModelFactory::class)
class CreateTrainDetailsViewModel @AssistedInject constructor(
    @Assisted private val teamId: Long,
    private val draftRepository: TrainingDraftRepository,
    private val conceptRepository: ConceptRepository,
    private val createTrainUseCase: CreateTrainUseCase,
    private val createTrainTaskUseCase: CreateTrainTaskUseCase,
    private val getTeamsUseCase: GetTeamsUseCase // TODO create a getTeamByIdUseCase
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
     * Carga los detalles del entrenamiento desde el borrador activo.
     */
    private fun loadTrainingDetails() {
        viewModelScope.launch {
            try {
                val teamName = getTeamsUseCase().first().first { it.id == teamId }.name
                val id = draftRepository.getOrCreateActiveDraft()
                draftId = id
                draftId?.let { currentId ->
                    draftRepository.observeDraft(currentId).collect { draft ->
                        if (draft != null) {
                            // Cálculo de duración: (Fin - Inicio) * 60 para obtener minutos
                            val durationInMinutes = (draft.endTime - draft.startTime) * 60

                            val trainInfo = TrainInfo(
                                date = formatDate(draft.date),
                                hour = formatHour(draft.startTime),
                                duration = formatDuration(durationInMinutes),
                                team = teamName
                            )

                            // Convertir tareas del dominio a TaskUI para la vista
                            val tasksUI = draft.tasks.map { task ->
                                TaskUI(
                                    id = task.id,
                                    name = task.name,
                                    concepts = conceptRepository.getListOfConcepts(task.concepts)
                                        .map { concept ->
                                            Concept(
                                                id = concept.id,
                                                conceptName = concept.name
                                            )
                                        },
                                    description = task.description,
                                    variation = task.variation,
                                    duration = ""
                                )
                            }

                            // Extraer lista única de nombres de conceptos para los chips de resumen
                            val listOfConcepts = tasksUI
                                .flatMap { task -> task.concepts.map { it.conceptName } }
                                .distinct()

                            _uiState.value = CreateTrainDetailsUiState.Success(
                                trainInfo = trainInfo,
                                listOfTasks = tasksUI,
                                listOfConcepts = listOfConcepts
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
     * Finaliza y guarda el entrenamiento en la base de datos definitiva.
     */
    fun saveTraining() {
        val id = draftId ?: return

        viewModelScope.launch {
            try {
                _uiState.update { state ->
                    if (state is CreateTrainDetailsUiState.Success) {
                        state.copy(isSaving = true)
                    } else state
                }

                val draft = draftRepository.getDraft(id)

                // 1. Crear el entrenamiento principal
                val trainId = createTrainUseCase(
                    date = draft?.date ?: Date(),
                    time = draft?.startTime ?: 0f,
                    concepts = draft?.concepts ?: emptyList(),
                    teamId = teamId
                )

                // 2. Guardar cada tarea asociada al entrenamiento creado
                draft?.tasks?.forEach { task ->
                    createTrainTaskUseCase(
                        trainId = trainId,
                        name = task.name,
                        numberOfPlayer = 0,
                        concept = task.concepts,
                        description = task.description,
                        variables = task.variation.split(",")
                    )
                }

                // 3. Marcar el borrador como finalizado
                draftRepository.finalizeDraft(id)

                _uiState.update { state ->
                    if (state is CreateTrainDetailsUiState.Success) {
                        state.copy(isSaving = false, trainingSaved = true)
                    } else state
                }
            } catch (e: Exception) {
                _uiState.value = CreateTrainDetailsUiState.Error(e)
            }
        }
    }

    // --- Métodos de formateo ---

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        return format.format(date)
    }

    private fun formatHour(hour: Float): String {
        val totalMinutes = (hour * 60).toInt()
        val h = totalMinutes / 60
        val m = totalMinutes % 60
        return String.format("%02d:%02d", h, m)
    }

    private fun formatDuration(durationInMinutes: Float): String {
        val totalMin = Math.max(0f, durationInMinutes).toInt()
        val hours = totalMin / 60
        val minutes = totalMin % 60

        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}min"
            hours > 0 -> "${hours}h"
            else -> "${minutes}min"
        }
    }
}

// --- Estados de la UI ---

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