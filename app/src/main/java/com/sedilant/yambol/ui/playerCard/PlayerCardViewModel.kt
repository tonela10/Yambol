package com.sedilant.yambol.ui.playerCard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.domain.CheckJerseyNumberUseCase
import com.sedilant.yambol.domain.UpdatePlayerUseCase
import com.sedilant.yambol.domain.get.GetPlayerByIdUseCase
import com.sedilant.yambol.domain.get.GetPlayerStatsUseCase
import com.sedilant.yambol.domain.get.GetPlayerTeamIdUseCase
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import com.sedilant.yambol.ui.playerCard.composables.EditPlayerData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PlayerCardViewModelFactory::class)
class PlayerCardViewModel @AssistedInject constructor(
    @Assisted private val playerId: Int?,
    private val getPlayerByIdUseCase: GetPlayerByIdUseCase,
    private val getPlayerStatsUseCase: GetPlayerStatsUseCase,
    private val updatePlayerUseCase: UpdatePlayerUseCase,
    private val getPlayerTeamIdUseCase: GetPlayerTeamIdUseCase,
    private val checkJerseyNumberUseCase: CheckJerseyNumberUseCase,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<PlayerCardDetailsUiState>(PlayerCardDetailsUiState.Loading)
    val uiState: StateFlow<PlayerCardDetailsUiState> = _uiState.asStateFlow()

    init {
        loadPlayerData()
    }

    private fun loadPlayerData() {
        if (playerId == null) {
            _uiState.value = PlayerCardDetailsUiState.Error("Invalid player ID")
            return
        }

        viewModelScope.launch {
            try {
                // Get player data (assuming this is a suspend function)
                val player = getPlayerByIdUseCase(playerId)

                // Get player stats flow and combine with player data
                getPlayerStatsUseCase(playerId)
                    .catch { exception ->
                        _uiState.value = PlayerCardDetailsUiState.Error(
                            exception.message ?: "Failed to load player stats"
                        )
                    }
                    .collect { playerStatsList ->
                        _uiState.value = PlayerCardDetailsUiState.Success(
                            player = player,
                            abilityList = playerStatsList.map { it.toUi() },
                            isEditBottomSheetVisible = false,
                            editBottomSheetLoading = false,
                            editBottomSheetError = null
                        )
                    }
            } catch (exception: Exception) {
                _uiState.value = PlayerCardDetailsUiState.Error(
                    exception.message ?: "Failed to load player data"
                )
            }
        }
    }

    fun showEditPlayerDialog() {
        val currentState = _uiState.value
        if (currentState is PlayerCardDetailsUiState.Success) {
            _uiState.value = currentState.copy(
                isEditBottomSheetVisible = true,
                editBottomSheetLoading = false,
                editBottomSheetError = null
            )
        }
    }

    fun hideEditPlayerDialog() {
        val currentState = _uiState.value
        if (currentState is PlayerCardDetailsUiState.Success) {
            _uiState.value = currentState.copy(
                isEditBottomSheetVisible = false,
                editBottomSheetLoading = false,
                editBottomSheetError = null
            )
        }
    }

    fun updatePlayer(editPlayerData: EditPlayerData) {
        val currentState = _uiState.value as? PlayerCardDetailsUiState.Success ?: return

        viewModelScope.launch {
            try {
                // Show loading state
                _uiState.value = currentState.copy(
                    editBottomSheetLoading = true,
                    editBottomSheetError = null
                )

                // Get player team ID for validation
                val teamId = getPlayerTeamIdUseCase(currentState.player.id)

                // Check if jersey number is taken (excluding current player)
                val numberInt = editPlayerData.number.toInt()
                val isNumberTaken =
                    checkJerseyNumberUseCase(teamId, numberInt, currentState.player.id)

                if (isNumberTaken) {
                    _uiState.value = currentState.copy(
                        editBottomSheetLoading = false,
                        editBottomSheetError = "Jersey number ${editPlayerData.number} is already taken"
                    )
                    return@launch
                }

                // Update player
                updatePlayerUseCase(
                    playerId = currentState.player.id,
                    newName = editPlayerData.name,
                    newNumber = numberInt,
                    newPosition = editPlayerData.position
                )

                // Hide bottom sheet and reload data
                _uiState.value = currentState.copy(
                    isEditBottomSheetVisible = false,
                    editBottomSheetLoading = false,
                    editBottomSheetError = null
                )
                loadPlayerData()

            } catch (exception: Exception) {
                _uiState.value = currentState.copy(
                    editBottomSheetLoading = false,
                    editBottomSheetError = exception.message ?: "Failed to update player"
                )
            }
        }
    }

    fun retry() {
        _uiState.value = PlayerCardDetailsUiState.Loading
        loadPlayerData()
    }
}

sealed interface PlayerCardDetailsUiState {
    data object Loading : PlayerCardDetailsUiState

    data class Success(
        val player: PlayerUiModel,
        val abilityList: List<StatUiModel>,
        val isEditBottomSheetVisible: Boolean = false,
        val editBottomSheetLoading: Boolean = false,
        val editBottomSheetError: String? = null
    ) : PlayerCardDetailsUiState

    data class Error(val message: String) : PlayerCardDetailsUiState
}

@AssistedFactory
interface PlayerCardViewModelFactory {
    fun create(playerId: Int?): PlayerCardViewModel
}
