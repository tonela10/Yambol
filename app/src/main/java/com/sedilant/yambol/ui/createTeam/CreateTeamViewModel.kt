package com.sedilant.yambol.ui.createTeam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.domain.GetTeamIdUseCase
import com.sedilant.yambol.domain.InsertPlayerUseCase
import com.sedilant.yambol.domain.InsertTeamUseCase
import com.sedilant.yambol.domain.Position
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class CreateTeamViewModel @Inject constructor(
    private val getTeamIdUseCase: GetTeamIdUseCase,
    private val insertPlayerUseCase: InsertPlayerUseCase,
    private val insertTeamUseCase: InsertTeamUseCase,
) : ViewModel() {

    private var teamId = 0

    //MeanWhile trigger
    private val trigger = MutableSharedFlow<Unit>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val formFlow = MutableStateFlow<CreateTeamUiState>(CreateTeamUiState.AddTeamName)
    val uiState =
        trigger.flatMapLatest { formFlow }

    fun onCreateTeam(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // create team
                insertTeamUseCase(name)
                teamId = getTeamIdUseCase(name)
                formFlow.update { CreateTeamUiState.AddPlayer }
                trigger.emit(Unit)
            } catch (e: Exception) {
                // manage errors
            }
        }
    }

    fun onNextPlayer(name: String, number: String, position: Position) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                insertPlayerUseCase(PlayerUiModel(name, number, position), teamId)
            } catch (e: Exception) {
                // manage errors
            }
        }
    }
}

sealed interface CreateTeamUiState {
    data object AddTeamName : CreateTeamUiState
    data object AddPlayer : CreateTeamUiState
    data object Loading : CreateTeamUiState
}
