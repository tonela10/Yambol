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

    private var listOfPlayers: MutableList<PlayerUiModel> = mutableListOf()

    private val teamNameFlow = MutableStateFlow(ValueAndValidation())
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
                    Form.ADD_PLAYER -> {
                        val nextButtonValidation =
                            playerName.isNotEmpty() && playerNumber.isNotEmpty()
                        val finishButtonValidation = listOfPlayers.size >= 5

                        CreateTeamUiState.AddPlayer(
                            playerName = playerName,
                            playerNumber = playerNumber,
                            isNextButtonEnabled = nextButtonValidation,
                            isFinishButtonEnabled = finishButtonValidation
                        )
                    }

                    Form.ADD_TEAM -> {

                        CreateTeamUiState.AddTeamName(
                            teamName = teamName.input,
                            isErrorMessageShow = !teamName.isValid,
                        )
                    }
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
            val teamExists = getTeamIdUseCase(teamNameFlow.value.input)
            if (teamExists != null) {
                teamNameFlow.update { ValueAndValidation(it.input, false) }
            } else {
                formFlow.update { Form.ADD_PLAYER }
            }
        }
    }

    fun onNextPlayer() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val name = playerNameFlow.first()
                val number = playerNumberFlow.first()
                listOfPlayers.add(
                    PlayerUiModel(
                        name, number, Position.POINT_GUARD,
                    )
                )

                playerNameFlow.update { "" }
                playerNumberFlow.update { "" }

            } catch (e: Exception) {
                // manage errors
            }
        }
    }

    fun onFinish() {
        viewModelScope.launch(Dispatchers.IO) {
            insertTeamUseCase(teamNameFlow.value.input)
            val teamId = getTeamIdUseCase(teamNameFlow.value.input)
            teamId?.let {
                listOfPlayers.forEach {
                    insertPlayerUseCase(PlayerUiModel(it.name, it.number, it.position), teamId)
                }
            }
        }
    }

    /**
     * We should make the necessary checks here about the input
     */
    fun updateTeamName(name: String) {
        teamNameFlow.update { ValueAndValidation(name) }
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
    data class AddTeamName(val teamName: String, val isErrorMessageShow: Boolean = false) :
        CreateTeamUiState

    data class AddPlayer(
        val playerName: String,
        val playerNumber: String,
        val isNextButtonEnabled: Boolean = false,
        val isFinishButtonEnabled: Boolean = false
    ) : CreateTeamUiState

    data object Loading : CreateTeamUiState
}
