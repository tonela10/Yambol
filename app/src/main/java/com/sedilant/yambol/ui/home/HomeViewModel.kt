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
import kotlinx.coroutines.flow.asStateFlow
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
    private val getLastTrainOfTeamUseCase: GetLastTrainOfTeamUseCase,
    private val updateTeamUseCase: UpdateTeamUseCase,
) : ViewModel() {
    // MeanWhile trigger
    private val trigger = MutableSharedFlow<Unit>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val currentTeamFlow = MutableStateFlow<Int?>(null)
    private var lastTrainId: Int? = null

    private val _editTeamState = MutableStateFlow<EditTeamState>(EditTeamState.Hidden)
    val editTeamState: StateFlow<EditTeamState> = _editTeamState.asStateFlow()

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadSavedTeam()
        setupUiStateFlow()
    }

    fun onTeamChange(newTeamId: Int) {
        viewModelScope.launch {
            currentTeamFlow.update { newTeamId }
            dataStoreManager.saveCurrentTeam(newTeamId)
            lastTrainId = getLastTrainOfTeamUseCase(newTeamId)?.toInt()
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

                    // TODO remove this part because we already do it in the loadSavedTeam
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
                        lastTrainId = lastTrainId
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
                    lastTrainId = getLastTrainOfTeamUseCase(savedTeamId)?.toInt()
                } else {
                    val teams = getTeamsUseCase().first()

                    if (teams.isEmpty()) {
                        _uiState.value = HomeUiState.CreateTeam
                        return@launch
                    } else {
                        val firstTeamId = teams.first().id
                        currentTeamFlow.update { firstTeamId }
                        lastTrainId = getLastTrainOfTeamUseCase(savedTeamId)?.toInt()
                        dataStoreManager.saveCurrentTeam(firstTeamId)
                    }
                }
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
        val statIds: List<Int>,
        val lastTrainId: Int? = null
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
