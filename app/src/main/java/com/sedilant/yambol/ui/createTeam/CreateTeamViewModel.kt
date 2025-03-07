package com.sedilant.yambol.ui.createTeam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.domain.GetTeamIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val getTeamIdUseCase: GetTeamIdUseCase
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

    // private val _uiState  = MutableStateFlow(CreateTeamUiState.AddTeamName)
    //val uiState: StateFlow<CreateTeamUiState> = _uiState.asStateFlow()

    fun onCreateTeam(name: String) {
        viewModelScope.launch {
            formFlow.update { CreateTeamUiState.AddPlayer }
            // create team
            // teamId = ...
        }

        fun onNextPlayer() {

        }

    }
}

sealed interface CreateTeamUiState {
    data object AddTeamName : CreateTeamUiState
    data object AddPlayer : CreateTeamUiState
    data object Loading : CreateTeamUiState
}
