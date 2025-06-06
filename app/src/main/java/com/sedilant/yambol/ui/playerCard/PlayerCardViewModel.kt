package com.sedilant.yambol.ui.playerCard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.domain.get.GetPlayerByIdUseCase
import com.sedilant.yambol.domain.get.GetPlayerStatsUseCase
import com.sedilant.yambol.ui.home.models.PlayerUiModel
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
                            abilityList = playerStatsList.map { it.toUi() }
                        )
                    }
            } catch (exception: Exception) {
                _uiState.value = PlayerCardDetailsUiState.Error(
                    exception.message ?: "Failed to load player data"
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
        val abilityList: List<StatUiModel>
    ) : PlayerCardDetailsUiState

    data class Error(val message: String) : PlayerCardDetailsUiState
}

@AssistedFactory
interface PlayerCardViewModelFactory {
    fun create(playerId: Int?): PlayerCardViewModel
}