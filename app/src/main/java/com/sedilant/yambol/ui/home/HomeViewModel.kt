package com.sedilant.yambol.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.DataStoreManager
import com.sedilant.yambol.domain.GetPlayersUseCase
import com.sedilant.yambol.domain.GetTeamObjectivesUseCase
import com.sedilant.yambol.domain.GetTeamsUseCase
import com.sedilant.yambol.domain.InsertTeamObjectiveUseCase
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

// TODO navigate to CreateTeam if there is no team

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel @Inject constructor(
    private val getTeamsUseCase: GetTeamsUseCase,
    private val getPlayersUseCase: GetPlayersUseCase,
    private val getTeamObjectivesUseCase: GetTeamObjectivesUseCase,
    private val insertTeamObjectiveUseCase: InsertTeamObjectiveUseCase,
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {
    //MeanWhile trigger
    private val trigger = MutableSharedFlow<Unit>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val currentTeamFlow = MutableStateFlow(1)
    private val isObjectiveDialogShowFlow = MutableStateFlow(false)

    // Home UI state
    // TODO the initial value of the uiState should be the last teams used [YAM-5]
    // Save into a DataStorage or SharedPreference the last team used

    val uiState = trigger.flatMapLatest { _ ->
        val teamsFlow = getTeamsUseCase()
        val combinedFlow = combine(
            teamsFlow,
            currentTeamFlow,
            isObjectiveDialogShowFlow,
        ) { teams, currentTeamId, isObjectiveDialogShow ->
            val currentTeam = teams.find { it.id == currentTeamId }
                ?: teams.first()

            val listOfTeams = teams.map { team ->
                TeamUiModel(
                    name = team.name.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                    },
                    id = team.id,
                )
            }

            Triple(listOfTeams, currentTeam.id, isObjectiveDialogShow)
        }.flatMapLatest { (listOfTeams, currentTeamId, isObjectiveDialogShow) ->
            combine(
                getPlayersUseCase(currentTeamId),
                getTeamObjectivesUseCase(currentTeamId).map { list ->
                    list.map {
                        TeamObjectivesUiModel(
                            description = it.description,
                            isFinish = it.isFinish,
                        )
                    }
                }
            ) { players, objectives ->
                HomeUiState.Success(
                    listOfTeams = listOfTeams,
                    currentTeam = listOfTeams.find { it.id == currentTeamId },
                    listOfPlayer = players,
                    listOfObjectives = objectives,
                    isObjectiveDialogShow = isObjectiveDialogShow,
                )
            }
            // TODO get the task of the current team [YAM-5] https://trello.com/c/ZBtayiO9
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
            trigger.emit(Unit)
            //   dataStoreManager.saveCurrentTeam(newTeamIndex)
        }
    }

    fun onAddTeamObjective() {
        isObjectiveDialogShowFlow.update { true }
    }

    fun onDismissObjectiveDialog() {
        isObjectiveDialogShowFlow.update { false }
    }

    fun onSaveNewObjective(input: String) {
        viewModelScope.launch {
            val teamId = currentTeamFlow.value
            insertTeamObjectiveUseCase(input, teamId)
        }
    }
}

sealed interface HomeUiState {
    data class Success(
        val listOfTeams: List<TeamUiModel>,
        val listOfPlayer: List<PlayerUiModel>,
        val currentTeam: TeamUiModel?,
        val listOfObjectives: List<TeamObjectivesUiModel>,
        val isObjectiveDialogShow: Boolean = false,
    ) : HomeUiState

    data object Loading : HomeUiState
}
