package com.sedilant.yambol.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.DataStoreManager
import com.sedilant.yambol.domain.DeleteTeamObjectiveUseCase
import com.sedilant.yambol.domain.ToggleTeamObjectiveUseCase
import com.sedilant.yambol.domain.UpdateTeamObjectiveUseCase
import com.sedilant.yambol.domain.UpdateTeamUseCase
import com.sedilant.yambol.domain.get.GetLastTrainOfTeamUseCase
import com.sedilant.yambol.domain.get.GetPlayersByTeamIdUseCase
import com.sedilant.yambol.domain.get.GetTeamObjectivesUseCase
import com.sedilant.yambol.domain.get.GetTeamsUseCase
import com.sedilant.yambol.domain.insert.InsertTeamObjectiveUseCase
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import com.sedilant.yambol.ui.home.models.TeamObjectivesUiModel
import com.sedilant.yambol.ui.home.models.TeamUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getTeamsUseCase: GetTeamsUseCase,
    private val getPlayersByTeamIdUseCase: GetPlayersByTeamIdUseCase,
    private val getTeamObjectivesUseCase: GetTeamObjectivesUseCase,
    private val insertTeamObjectiveUseCase: InsertTeamObjectiveUseCase,
    private val updateTeamObjectiveUseCase: UpdateTeamObjectiveUseCase,
    private val toggleTeamObjectiveUseCase: ToggleTeamObjectiveUseCase,
    private val deleteTeamObjectiveUseCase: DeleteTeamObjectiveUseCase,
    private val dataStoreManager: DataStoreManager,
    private val getLastTrainOfTeamUseCase: GetLastTrainOfTeamUseCase,
    private val updateTeamUseCase: UpdateTeamUseCase,
) : ViewModel() {
    // MeanWhile trigger
    private val trigger = MutableSharedFlow<Unit>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val currentTeamFlow = MutableStateFlow<String?>(null)
    private var lastTrainId: String? = null

    private val _editTeamState = MutableStateFlow<EditTeamState>(EditTeamState.Hidden)
    val editTeamState: StateFlow<EditTeamState> = _editTeamState.asStateFlow()

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadSavedTeam()
        setupUiStateFlow()
    }

    fun onTeamChange(newTeamId: String) {
        viewModelScope.launch {
            currentTeamFlow.update { newTeamId }
            dataStoreManager.saveCurrentTeam(newTeamId)
            lastTrainId = getLastTrainOfTeamUseCase(newTeamId)
            refreshData()
        }
    }

    fun onSaveNewObjective(input: String) {
        viewModelScope.launch {
            val teamId = currentTeamFlow.value ?: return@launch
            insertTeamObjectiveUseCase(input, teamId)
        }
    }

    fun onToggleObjectiveStatus(objectiveId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            toggleTeamObjectiveUseCase(objectiveId, !isCompleted)
        }
    }

    fun onUpdateObjective(objectiveId: String, newDescription: String, isCompleted: Boolean) {
        viewModelScope.launch {
            updateTeamObjectiveUseCase(
                objectiveId = objectiveId,
                newDescription = newDescription,
                isCompleted = isCompleted,
                teamId = currentTeamFlow.value ?: return@launch
            )
        }
    }

    fun onDeleteObjective(objectiveId: String) {
        viewModelScope.launch {
            deleteTeamObjectiveUseCase(objectiveId)
        }
    }

    fun showEditTeamDialog(teamName: String) {
        _editTeamState.value =
            EditTeamState.Visible(teamName, isLoading = false, errorMessage = null)
    }

    fun hideEditTeamDialog() {
        _editTeamState.value = EditTeamState.Hidden
    }

    fun updateTeamName(newName: String) {
        val currentState = _editTeamState.value as? EditTeamState.Visible ?: return

        viewModelScope.launch {
            try {
                _editTeamState.value = currentState.copy(isLoading = true, errorMessage = null)

                val currentTeamId = currentTeamFlow.value ?: return@launch

                // Check if name already exists (exclude current team)
                val teams = getTeamsUseCase().first()
                val nameExists = teams.any {
                    it.name.equals(newName, ignoreCase = true) && it.id != currentTeamId
                }

                if (nameExists) {
                    _editTeamState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = "Team name already exists"
                    )
                    return@launch
                }

                updateTeamUseCase(currentTeamId, newName)

                _editTeamState.value = EditTeamState.Hidden
                refreshData()

            } catch (exception: Exception) {
                _editTeamState.value = currentState.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Failed to update team name"
                )
            }
        }
    }


    private fun setupUiStateFlow() {
        viewModelScope.launch {
            trigger.flatMapLatest {
                combine(
                    getTeamsUseCase(),
                    currentTeamFlow
                ) { teams, requestedTeamId ->
                    teams to requestedTeamId
                }.flatMapLatest { (teams, requestedTeamId) ->
                    if (teams.isEmpty()) {
                        return@flatMapLatest flowOf(HomeUiState.CreateTeam)
                    }

                    val listOfTeams = teams.map { team ->
                        TeamUiModel(
                            name = team.name.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                            },
                            id = team.id,
                        )
                    }

                    val resolvedTeamId = teams.firstOrNull { it.id == requestedTeamId }?.id
                        ?: teams.first().id

                    flow {
                        // Keep in-memory and persisted selection aligned to a valid team.
                        if (requestedTeamId != resolvedTeamId) {
                            currentTeamFlow.update { resolvedTeamId }
                            dataStoreManager.saveCurrentTeam(resolvedTeamId)
                        }
                        lastTrainId = getLastTrainOfTeamUseCase(resolvedTeamId)
                        emit(resolvedTeamId to listOfTeams)
                    }.flatMapLatest { (teamId, uiTeams) ->
                        combine(
                            getPlayersByTeamIdUseCase(teamId),
                            getTeamObjectivesUseCase(teamId)
                        ) { teamPlayerList, teamObjectivesList ->
                            HomeUiState.Success(
                                listOfTeams = uiTeams,
                                currentTeam = uiTeams.first { it.id == teamId },
                                listOfPlayer = teamPlayerList,
                                listOfObjectives = teamObjectivesList.map {
                                    TeamObjectivesUiModel(
                                        description = it.description,
                                        isFinish = it.isFinish,
                                        id = it.id,
                                    )
                                },
                                lastTrainId = lastTrainId
                            )
                        }
                    }
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

                val teams = getTeamsUseCase().first()
                if (teams.isEmpty()) {
                    currentTeamFlow.update { null }
                    dataStoreManager.saveCurrentTeam(null)
                    _uiState.value = HomeUiState.CreateTeam
                    return@launch
                }

                val savedTeamId = dataStoreManager.currentTeam.first()
                val resolvedTeamId = teams.firstOrNull { it.id == savedTeamId }?.id ?: teams.first().id

                currentTeamFlow.update { resolvedTeamId }
                if (savedTeamId != resolvedTeamId) {
                    dataStoreManager.saveCurrentTeam(resolvedTeamId)
                }
                lastTrainId = getLastTrainOfTeamUseCase(resolvedTeamId)
            } finally {
                refreshData()
            }
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
        val lastTrainId: String? = null
    ) : HomeUiState

    data object Loading : HomeUiState
    data object CreateTeam : HomeUiState
}

sealed interface EditTeamState {
    data object Hidden : EditTeamState
    data class Visible(
        val currentTeamName: String,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) : EditTeamState
}
