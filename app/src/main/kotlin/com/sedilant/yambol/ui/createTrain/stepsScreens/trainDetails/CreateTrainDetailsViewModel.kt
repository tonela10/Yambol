package com.sedilant.yambol.ui.createTrain.stepsScreens.trainDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.draftTrain.TrainingDraftRepository
import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.ConceptRepository
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
    @Assisted private val teamId: String,
    private val draftRepository: TrainingDraftRepository,
    private val conceptRepository: ConceptRepository,
    private val createTrainUseCase: CreateTrainUseCase,
    private val createTrainTaskUseCase: CreateTrainTaskUseCase,
    private val getTeamsUseCase: GetTeamsUseCase, // TODO create a getTeamByIdUseCase
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<CreateTrainDetailsUiState>(CreateTrainDetailsUiState.Loading)
    val uiState: StateFlow<CreateTrainDetailsUiState> = _uiState.asStateFlow()

    private var draftId: String? = null

    init {
        loadTrainingDetails()
    }

    /**
     * Loads training details from the active draft.
     */
    private fun loadTrainingDetails() {
        viewModelScope.launch {
            try {
                // Get userId
                // Ideally injected AuthRepository or maybe userId is provided in some UseCase or Manager.
                // Assuming we can get userId somehow or I need to inject AuthRepository.
                // CreateTrainDetailsViewModel does NOT inject AuthRepository yet.
                // I will add it to the constructor.
                val userId = authRepository.currentUser?.uid
                    ?: throw IllegalStateException("User not logged in")

                val teamName = getTeamsUseCase().first().first { it.id == teamId }.name
                val id = draftRepository.getOrCreateActiveDraft()
                draftId = id
                draftId?.let { currentId ->
                    draftRepository.observeDraft(currentId).collect { draft ->
                        if (draft != null) {
                            // Calculate duration: (End - Start) * 60 to get minutes
                            val durationInMinutes = (draft.endTime - draft.startTime) * 60

                            val trainInfo = TrainInfo(
                                date = formatDate(draft.date),
                                hour = formatHour(draft.startTime),
                                duration = formatDuration(durationInMinutes),
                                team = teamName
                            )

                            // Convertir tareas del dominio a TaskUI para la vista
                            val tasksUI = draft.tasks.map { task ->
                                // Get concept names from concept IDs
                                val conceptNames = conceptRepository.getConceptsByIds(
                                    userId,
                                    task.concepts
                                ).map { concept -> concept.name }

                                TaskUI(
                                    id = task.id,
                                    name = task.name,
                                    concepts = conceptNames.map { conceptName ->
                                        Concept(
                                            id = conceptName, // Assuming concept ID is the same as conceptName here or we just want to display it
                                            conceptName = conceptName
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
                    startTime = draft?.startTime ?: 0f,
                    endTime = draft?.endTime ?: 0f,
                    concepts = draft?.conceptIds ?: emptyList(),
                    teamId = teamId,
                )

                // 2. Guardar cada tarea asociada al entrenamiento creado
                draft?.tasks?.forEach { task ->
                    createTrainTaskUseCase(
                        taskId = task.id,
                        trainId = trainId,
                        name = task.name,
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

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
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
    fun create(teamId: String): CreateTrainDetailsViewModel
}