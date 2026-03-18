package com.sedilant.yambol.ui.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.DataStoreManager
import com.sedilant.yambol.domain.get.GetAllTaskUseCase
import com.sedilant.yambol.domain.get.GetAllTrainsByTeamIdUseCase
import com.sedilant.yambol.domain.get.GetTaskConceptNameUseCase
import com.sedilant.yambol.domain.get.GetTeamsUseCase
import com.sedilant.yambol.domain.models.TaskDomain
import com.sedilant.yambol.domain.models.TrainDomainModel
import com.sedilant.yambol.ui.home.models.TeamUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val getAllTrainsByTeamIdUseCase: GetAllTrainsByTeamIdUseCase,
    private val getAllTaskUseCase: GetAllTaskUseCase,
    private val getTaskConceptNameUseCase: GetTaskConceptNameUseCase,
    private val dataStoreManager: DataStoreManager,
    private val getTeamsUseCase: GetTeamsUseCase,
) : ViewModel() {

    private val teamsFlow = flow {
        emit(getTeamsUseCase())
    }.flatMapLatest { it }

    private val tasksWithConceptNamesFlow = getAllTaskUseCase()
        .mapLatest { tasks ->
            val conceptIds = tasks.flatMap { it.concepts }
            val conceptNameById = getTaskConceptNameUseCase(conceptIds)

            tasks.map { task ->
                task.copy(concepts = task.concepts.mapNotNull { conceptNameById[it] })
            }
        }
        .catch { emit(emptyList()) }

    val uiState: StateFlow<TrainingUiState> = combine(
        dataStoreManager.currentTeam,
        teamsFlow,
        tasksWithConceptNamesFlow,
    ) { currentTeamId, teamDomainList, taskList ->

        val teamUiList = teamDomainList.map { team ->
            TeamUiModel(
                name = team.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                },
                id = team.id,
            )
        }

        Triple(currentTeamId, teamUiList, taskList)
    }.flatMapLatest { (currentTeamId, teamList, taskList) ->
        if (currentTeamId == null) {
            flowOf(
                TrainingUiState.Success(
                    trainList = emptyList(),
                    teamList = teamList,
                    currentTeamId = "", // Empty string or similar for no team
                    taskList = taskList,
                )
            )
        } else {
            try {
                val trainList =
                    getAllTrainsByTeamIdUseCase(currentTeamId) // TODO change with a flow
                flowOf(
                    TrainingUiState.Success(
                        trainList = trainList,
                        teamList = teamList,
                        currentTeamId = currentTeamId,
                        taskList = taskList,
                    )
                )
            } catch (e: Exception) {
                flowOf(TrainingUiState.Error(e.message ?: "Unknown error"))
            }
        }
    }.catch { exception ->
        emit(TrainingUiState.Error(exception.message ?: "Unknown error"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TrainingUiState.Loading
    )

    fun onTeamChange(newTeamId: String) {
        viewModelScope.launch {
            dataStoreManager.saveCurrentTeam(newTeamId)
        }
    }
}


sealed interface TrainingUiState {
    data object Loading : TrainingUiState
    data class Success(
        val trainList: List<TrainDomainModel>,
        val taskList: List<TaskDomain>,
        val teamList: List<TeamUiModel>,
        val currentTeamId: String
    ) : TrainingUiState

    data class Error(val message: String) : TrainingUiState
}