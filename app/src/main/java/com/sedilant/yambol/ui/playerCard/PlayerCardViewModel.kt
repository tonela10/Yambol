package com.sedilant.yambol.ui.playerCard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.domain.GetPlayerByIdUseCase
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PlayerCardViewModelFactory::class)
class PlayerCardViewModel @AssistedInject constructor(
    @Assisted private val playerId: Int?,
    getPlayerByIdUseCase: GetPlayerByIdUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<PlayerCardDetailsUiState>(PlayerCardDetailsUiState.Loading)
    val uiState: StateFlow<PlayerCardDetailsUiState> = _uiState

    init {
        viewModelScope.launch {
            val player = playerId?.let { getPlayerByIdUseCase(it) }
            _uiState.update { PlayerCardDetailsUiState.Success(player!!) } // TODO remove the !!
        }
    }
}


sealed interface PlayerCardDetailsUiState {
    data object Loading : PlayerCardDetailsUiState
    data class Success(val player: PlayerUiModel) : PlayerCardDetailsUiState
}

@AssistedFactory
interface PlayerCardViewModelFactory {
    fun create(playerId: Int?): PlayerCardViewModel
}