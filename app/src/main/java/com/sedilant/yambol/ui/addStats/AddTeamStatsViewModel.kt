package com.sedilant.yambol.ui.addStats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.domain.get.GetPlayersByTeamIdUseCase
import com.sedilant.yambol.domain.get.GetStatByIdUseCase
import com.sedilant.yambol.domain.insert.SavePlayerStatUseCase
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import com.sedilant.yambol.ui.playerCard.StatUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AddTeamStatsViewModelFactory::class)
class AddTeamStatsViewModel @AssistedInject constructor(
    @Assisted private val teamId: Int,
    @Assisted private val statIds: List<Int>,
    private val getPlayersByTeamIdUseCase: GetPlayersByTeamIdUseCase,
    private val getStatByIdUseCase: GetStatByIdUseCase,
    private val savePlayerStatUseCase: SavePlayerStatUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddTeamStatsUiState>(AddTeamStatsUiState.Loading)
    val uiState: StateFlow<AddTeamStatsUiState> = _uiState.asStateFlow()

    private var currentStatIndex = 0
    private val playerRatings =
        mutableMapOf<Int, MutableMap<Int, Int>>() // playerId -> (statId -> rating)

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        if (statIds.isEmpty()) {
            _uiState.value = AddTeamStatsUiState.Error("No stats provided for rating")
            return
        }

        viewModelScope.launch {
            try {
                val currentStat = getStatByIdUseCase(statIds[currentStatIndex])

                // Collect players from Flow and update UI state
                getPlayersByTeamIdUseCase(teamId)
                    .catch { exception ->
                        _uiState.value = AddTeamStatsUiState.Error(
                            exception.message ?: "Failed to load players"
                        )
                    }
                    .collect { players ->
                        // Initialize ratings map for new players
                        players.forEach { player ->
                            if (!playerRatings.containsKey(player.id)) {
                                playerRatings[player.id] = mutableMapOf()
                            }
                        }

                        _uiState.value = AddTeamStatsUiState.Success(
                            players = players,
                            currentStat = currentStat,
                            currentStatIndex = currentStatIndex,
                            totalStats = statIds.size,
                            playerRatings = getCurrentRatings()
                        )
                    }
            } catch (exception: Exception) {
                _uiState.value = AddTeamStatsUiState.Error(
                    exception.message ?: "Failed to load data"
                )
            }
        }
    }

    fun updatePlayerRating(playerId: Int, rating: Int) {
        val currentStatId = statIds[currentStatIndex]
        playerRatings[playerId]?.set(currentStatId, rating)

        // Update UI state with new ratings
        val currentState = _uiState.value
        if (currentState is AddTeamStatsUiState.Success) {
            _uiState.value = currentState.copy(
                playerRatings = getCurrentRatings()
            )
        }
    }

    fun proceedToNext() {
        viewModelScope.launch {
            try {
                saveCurrentRatings()

                if (hasNextStat()) {
                    currentStatIndex++
                    val nextStat = getStatByIdUseCase(statIds[currentStatIndex])

                    // Update UI state with next stat info
                    val currentState = _uiState.value
                    if (currentState is AddTeamStatsUiState.Success) {
                        _uiState.value = currentState.copy(
                            currentStat = nextStat,
                            currentStatIndex = currentStatIndex,
                            playerRatings = getCurrentRatings()
                        )
                    }
                } else {
                    // All stats completed
                    _uiState.value = AddTeamStatsUiState.Completed
                }
            } catch (exception: Exception) {
                _uiState.value = AddTeamStatsUiState.Error(
                    exception.message ?: "Failed to save ratings"
                )
            }
        }
    }

    fun goToPrevious() {
        if (currentStatIndex > 0) {
            viewModelScope.launch {
                try {
                    currentStatIndex--
                    val previousStat = getStatByIdUseCase(statIds[currentStatIndex])

                    // Update UI state with previous stat info
                    val currentState = _uiState.value
                    if (currentState is AddTeamStatsUiState.Success) {
                        _uiState.value = currentState.copy(
                            currentStat = previousStat,
                            currentStatIndex = currentStatIndex,
                            playerRatings = getCurrentRatings()
                        )
                    }
                } catch (exception: Exception) {
                    _uiState.value = AddTeamStatsUiState.Error(
                        exception.message ?: "Failed to load previous stat"
                    )
                }
            }
        }
    }

    private suspend fun saveCurrentRatings() {
        val currentStatId = statIds[currentStatIndex]
        playerRatings.forEach { (playerId, ratings) ->
            ratings[currentStatId]?.let { rating ->
                savePlayerStatUseCase(
                    playerId = playerId,
                    statId = currentStatId,
                    rating = rating
                )
            }
        }
    }

    private fun getCurrentRatings(): Map<Int, Int> {
        val currentStatId = statIds[currentStatIndex]
        return playerRatings.mapNotNull { (playerId, ratings) ->
            ratings[currentStatId]?.let { rating ->
                playerId to rating
            }
        }.toMap()
    }

    private fun hasNextStat(): Boolean = currentStatIndex < statIds.size - 1

    fun canProceed(): Boolean {
        val currentState = _uiState.value
        if (currentState is AddTeamStatsUiState.Success) {
            // Check if all players have ratings for current stat
            return currentState.players.all { player ->
                getCurrentRatings().containsKey(player.id)
            }
        }
        return false
    }

    fun retry() {
        _uiState.value = AddTeamStatsUiState.Loading
        loadInitialData()
    }
}

sealed interface AddTeamStatsUiState {
    data object Loading : AddTeamStatsUiState

    data class Success(
        val players: List<PlayerUiModel>,
        val currentStat: StatUiModel,
        val currentStatIndex: Int,
        val totalStats: Int,
        val playerRatings: Map<Int, Int> // playerId -> rating for current stat
    ) : AddTeamStatsUiState

    data object Completed : AddTeamStatsUiState
    data class Error(val message: String) : AddTeamStatsUiState
}

@AssistedFactory
interface AddTeamStatsViewModelFactory {
    fun create(teamId: Int, statIds: List<Int>): AddTeamStatsViewModel
}
