package com.sedilant.yambol.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.DataStoreManager
import com.sedilant.yambol.domain.DeleteTeamObjectiveUseCase
import com.sedilant.yambol.domain.ToggleTeamObjectiveUseCase
import com.sedilant.yambol.domain.UpdateTeamObjectiveUseCase
import com.sedilant.yambol.domain.get.GetPlayersByTeamIdUseCase
import com.sedilant.yambol.domain.get.GetStatByNameUseCase
import com.sedilant.yambol.domain.get.GetTeamObjectivesUseCase
import com.sedilant.yambol.domain.get.GetTeamsUseCase
import com.sedilant.yambol.domain.insert.InsertTeamObjectiveUseCase
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import com.sedilant.yambol.ui.home.models.TeamObjectivesUiModel
import com.sedilant.yambol.ui.home.models.TeamUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel @Inject constructor(
    private val getTeamsUseCase: GetTeamsUseCase,
    private val getPlayersByTeamIdUseCase: GetPlayersByTeamIdUseCase,
    private val getTeamObjectivesUseCase: GetTeamObjectivesUseCase,
    private val insertTeamObjectiveUseCase: InsertTeamObjectiveUseCase,
    private val updateTeamObjectiveUseCase: UpdateTeamObjectiveUseCase,
    private val toggleTeamObjectiveUseCase: ToggleTeamObjectiveUseCase,
    private val deleteTeamObjectiveUseCase: DeleteTeamObjectiveUseCase,
    private val dataStoreManager: DataStoreManager,
    private val getStatByNameUseCase: GetStatByNameUseCase,
) : ViewModel() {
    // MeanWhile trigger
    private val trigger = MutableSharedFlow<Unit>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val currentTeamFlow = MutableStateFlow<Int?>(null)

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadSavedTeam()
        setupUiStateFlow()
    }

    private fun setupUiStateFlow() {
        viewModelScope.launch {

            val statIds =
                listOf(
                    getStatByNameUseCase("physical_state"),
                    getStatByNameUseCase("mental_state")
                ).map { it.id }
            trigger.flatMapLatest { _ ->
                val teamsFlow = getTeamsUseCase()
                combine(
                    teamsFlow,
                    currentTeamFlow.flatMapLatest { teamId ->
                        if (teamId != null) {
                            getPlayersByTeamIdUseCase(teamId)
                        } else {
                            MutableStateFlow(emptyList())
                        }
                    },
                    currentTeamFlow.flatMapLatest { teamId ->
                        if (teamId != null) {
                            getTeamObjectivesUseCase(teamId)
                        } else {
                            MutableStateFlow(emptyList())
                        }
                    },
                    currentTeamFlow,
                ) { teams, teamPlayerList, teamObjectivesList, currentTeamId ->

                    // If there are no teams, navigate to CreateTeamScreen
                    if (teams.isEmpty()) {
                        return@combine HomeUiState.CreateTeam
                    }

                    val listOfTeams = teams.map { team ->
                        TeamUiModel(
                            name = team.name.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                            },
                            id = team.id,
                        )
                    }

                    // If currentTeamId is null but there are teams, use the first one
                    val safeCurrentTeamId = currentTeamId ?: if (listOfTeams.isNotEmpty()) {
                        val firstTeamId = listOfTeams.first().id
                        // Update the new actual team
                        viewModelScope.launch {
                            currentTeamFlow.update { firstTeamId }
                            dataStoreManager.saveCurrentTeam(firstTeamId)
                        }
                        firstTeamId
                    } else null

                    if (safeCurrentTeamId == null) {
                        return@combine HomeUiState.Loading
                    }

                    HomeUiState.Success(
                        listOfTeams = listOfTeams,
                        currentTeam = listOfTeams.find { it.id == safeCurrentTeamId },
                        listOfPlayer = teamPlayerList,
                        listOfObjectives = teamObjectivesList.map {
                            TeamObjectivesUiModel(
                                description = it.description,
                                isFinish = it.isFinish,
                                id = it.id,
                            )
                        },
                        statIds = statIds,
                    )
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun loadSavedTeam() {
        viewModelScope.launch {
            try {
                _uiState.value = HomeUiState.Loading

                val savedTeamId = dataStoreManager.currentTeam.first()
                if (savedTeamId != null) {
                    currentTeamFlow.update { savedTeamId }
                } else {
                    val teams = getTeamsUseCase().first()

                    if (teams.isEmpty()) {
                        _uiState.value = HomeUiState.CreateTeam
                        return@launch
                    } else {
                        val firstTeamId = teams.first().id
                        currentTeamFlow.update { firstTeamId }
                        dataStoreManager.saveCurrentTeam(firstTeamId)
                    }
                }
            } finally {
                refreshData()
            }
        }
    }

    fun onTeamChange(newTeamId: Int) {
        viewModelScope.launch {
            currentTeamFlow.update { newTeamId }
            dataStoreManager.saveCurrentTeam(newTeamId)
        }
    }

    fun onSaveNewObjective(input: String) {
        viewModelScope.launch {
            val teamId = currentTeamFlow.value ?: return@launch
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
            val teamId = currentTeamFlow.value ?: return@launch
            deleteTeamObjectiveUseCase(
                objectiveId,
                description = description,
                isFinish = isFinished,
                teamId = teamId,
            )
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            trigger.emit(Unit)
        }
    }
}

sealed interface HomeUiState {
    data class Success(
        val listOfTeams: List<TeamUiModel>,
        val listOfPlayer: List<PlayerUiModel>,
        val currentTeam: TeamUiModel?,
        val listOfObjectives: List<TeamObjectivesUiModel>,
        val statIds: List<Int>
    ) : HomeUiState

    data object Loading : HomeUiState
    data object CreateTeam : HomeUiState
}