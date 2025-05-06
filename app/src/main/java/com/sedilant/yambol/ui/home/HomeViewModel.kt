package com.sedilant.yambol.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.DataStoreManager
import com.sedilant.yambol.domain.DeleteTeamObjectiveUseCase
import com.sedilant.yambol.domain.GetPlayersUseCase
import com.sedilant.yambol.domain.GetTeamObjectivesUseCase
import com.sedilant.yambol.domain.GetTeamsUseCase
import com.sedilant.yambol.domain.InsertTeamObjectiveUseCase
import com.sedilant.yambol.domain.ToggleTeamObjectiveUseCase
import com.sedilant.yambol.domain.UpdateTeamObjectiveUseCase
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import com.sedilant.yambol.ui.home.models.TeamObjectivesUiModel
import com.sedilant.yambol.ui.home.models.TeamUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

// TODO navigate to CreateTeam if there is no team [YAM 12] https://trello.com/c/QAlawJMa

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel @Inject constructor(
    private val getTeamsUseCase: GetTeamsUseCase,
    private val getPlayersUseCase: GetPlayersUseCase,
    private val getTeamObjectivesUseCase: GetTeamObjectivesUseCase,
    private val insertTeamObjectiveUseCase: InsertTeamObjectiveUseCase,
    private val updateTeamObjectiveUseCase: UpdateTeamObjectiveUseCase,
    private val toggleTeamObjectiveUseCase: ToggleTeamObjectiveUseCase,
    private val deleteTeamObjectiveUseCase: DeleteTeamObjectiveUseCase,
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {
    //MeanWhile trigger
    private val trigger = MutableSharedFlow<Unit>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val currentTeamFlow = MutableStateFlow(1)

    // Home UI state
    // TODO the initial value of the uiState should be the last teams used [YAM-5]
    // Save into a DataStorage or SharedPreference the last team used

    val uiState = trigger.flatMapLatest { _ ->

        val teamsFlow = getTeamsUseCase()
        val combinedFlow = combine(
            teamsFlow,
            currentTeamFlow.flatMapLatest { teamId -> getPlayersUseCase(teamId) },
            currentTeamFlow.flatMapLatest { teamId -> getTeamObjectivesUseCase(teamId) },
            currentTeamFlow,
        ) { teams, teamPlayerList, teamObjectivesList, currentTeamId ->

            val listOfTeams = teams.map { team ->
                TeamUiModel(
                    name = team.name.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                    },
                    id = team.id,
                )
            }

            HomeUiState.Success(
                listOfTeams = listOfTeams,
                currentTeam = listOfTeams.find { it.id == currentTeamId },
                listOfPlayer = teamPlayerList,
                listOfObjectives = teamObjectivesList.map {
                    TeamObjectivesUiModel(
                        description = it.description,
                        isFinish = it.isFinish,
                        id = it.id,
                    )
                },
            )
        }
        combinedFlow
    }

    init {
        viewModelScope.launch {
            // currentTeamFlow.update { dataStoreManager.currentTeam.firstOrNull() }
            trigger.emit(Unit)
        }
    }

    fun onTeamChange(newTeamIndex: Int) {
        viewModelScope.launch {
            currentTeamFlow.update { newTeamIndex }
            //   dataStoreManager.saveCurrentTeam(newTeamIndex)
        }
    }

    fun onSaveNewObjective(input: String) {
        viewModelScope.launch {
            val teamId = currentTeamFlow.value
            insertTeamObjectiveUseCase(input, teamId)
        }
    }

    fun onToggleObjectiveStatus(objectiveId: Int) {
        viewModelScope.launch {
            toggleTeamObjectiveUseCase(objectiveId)
        }
    }

    fun onUpdateObjective(objectiveId: Int, newDescription: String) {
        viewModelScope.launch {
            updateTeamObjectiveUseCase(objectiveId, description = newDescription)
        }
    }

    fun onDeleteObjective(objectiveId: Int, description: String, isFinished: Boolean) {
        viewModelScope.launch {
            val teamId = currentTeamFlow.value
            deleteTeamObjectiveUseCase(
                objectiveId,
                description = description,
                isFinish = isFinished,
                teamId = teamId,
            )
        }
    }
}

sealed interface HomeUiState {
    data class Success(
        val listOfTeams: List<TeamUiModel>,
        val listOfPlayer: List<PlayerUiModel>,
        val currentTeam: TeamUiModel?, // TODO remove team is nullable YAM-5
        val listOfObjectives: List<TeamObjectivesUiModel>,
    ) : HomeUiState

    data object Loading : HomeUiState
}
