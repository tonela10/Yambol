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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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
    private var listOfPlayers: MutableList<PlayerUiModel> = mutableListOf()

    private val teamNameFlow = MutableStateFlow("")
    private val playerNameFlow = MutableStateFlow("")
    private val playerNumberFlow = MutableStateFlow("")
    private val formFlow = MutableStateFlow(Form.ADD_TEAM)

    //MeanWhile trigger
    private val trigger = MutableSharedFlow<Unit>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val uiState =
        trigger.flatMapLatest { _ ->

            val combinedFlow = combine(
                teamNameFlow,
                playerNameFlow,
                playerNumberFlow,
                formFlow,
            ) { teamName, playerName, playerNumber, form ->
                when (form) {
                    Form.ADD_PLAYER ->
                        CreateTeamUiState.AddPlayer(
                            playerName = playerName,
                            playerNumber = playerNumber
                        )

                    Form.ADD_TEAM -> CreateTeamUiState.AddTeamName(
                        teamName = teamName
                    )
                }
            }
            combinedFlow
        }

    init {
        viewModelScope.launch {
            trigger.emit(Unit)
        }
    }

    fun onCreateTeam() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                formFlow.update { Form.ADD_PLAYER }
            } catch (e: Exception) {
                // manage errors
            }
        }
    }

    fun onNextPlayer() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val name = playerNameFlow.first()
                val number = playerNumberFlow.first()
                listOfPlayers.add(PlayerUiModel(name, number, Position.POINT_GUARD))

                playerNameFlow.update { "" }
                playerNumberFlow.update { "" }

            } catch (e: Exception) {
                // manage errors
            }
        }
    }

    fun onFinish() {
        viewModelScope.launch(Dispatchers.IO) {
            // save the last player too
            try {
                // Save the team and get the id
                insertTeamUseCase(teamNameFlow.value)
                teamId = getTeamIdUseCase(teamNameFlow.value)

                listOfPlayers.forEach {
                    insertPlayerUseCase(PlayerUiModel(it.name, it.number, it.position), teamId)
                }

            } catch (e: Exception) {
                //  manage error
            }
        }
    }

    /**
     * We should make the necessary checks here about the input
     */
    // TODO Check if the team exist if  does exist return false
    fun updateTeamName(name: String): Boolean {
        teamNameFlow.update { name }
        return true
    }

    fun updatePlayerName(name: String) {
        playerNameFlow.update { name }
    }

    fun updatePlayerNumber(number: String) {
        playerNumberFlow.update { number }
    }

    private enum class Form {
        ADD_TEAM,
        ADD_PLAYER,
    }
}

sealed interface CreateTeamUiState {
    data class AddTeamName(val teamName: String) : CreateTeamUiState
    data class AddPlayer(val playerName: String, val playerNumber: String) : CreateTeamUiState
    data object Loading : CreateTeamUiState
}
