package com.sedilant.yambol.ui.createTeam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.domain.get.GetTeamsUseCase
import com.sedilant.yambol.domain.insert.InsertPlayersUseCase
import com.sedilant.yambol.domain.insert.InsertTeamUseCase
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

/*   TODO remove the commment
    Pedir nombre del equipo y guardarlo -> tengo el id del equipo.
    Creo jugadores para el equipo con el id del equipo
    Guardo Lista de jugadores con el ide del equipo

    Sin cancelo la operación elimino el equipo y en consecuencia se borran todos los jugadores
 */
// TODO create an onCancel method to remove the team created in case the user cancel the process
@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class CreateTeamViewModel @Inject constructor(
    private val insertPlayersUseCase: InsertPlayersUseCase,
    private val insertTeamUseCase: InsertTeamUseCase,
    private val getTeamsUseCase: GetTeamsUseCase,
) : ViewModel() {

    private lateinit var listOfTeams: List<String>
    var teamId: String = ""
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
                        val nextButtonValidation = isPlayerDataValid(playerName, playerNumber)
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
            listOfTeams = getTeamsUseCase().first().map { it.name }
        }
    }

    fun onCreateTeam() {
        viewModelScope.launch(Dispatchers.IO) {
            val teamName = teamNameFlow.value.input.trim()

            if (teamName.isEmpty()) {
                teamNameFlow.update { ValueAndValidation(it.input, false) }
                return@launch
            }

            val teamExists = listOfTeams.contains(teamName)
            if (teamExists) {
                teamNameFlow.update {
                    ValueAndValidation(
                        it.input,
                        false
                    )
                } // TODO display error message : The team name already exists
            } else {
                teamNameFlow.update { ValueAndValidation(teamName, true) }
                teamId = insertTeamUseCase(teamName)
                formFlow.update { Form.ADD_PLAYER }
            }
        }
    }

    fun onNextPlayer() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val name = playerNameFlow.first().trim()
                val number = playerNumberFlow.first()

                if (!isPlayerDataValid(name, number)) {
                    return@launch
                }

                if (isPlayerNumberTaken(number)) {

                    return@launch
                }

                listOfPlayers.add(
                    PlayerUiModel(
                        name = name,
                        number = number,
                        teamId = teamId,
                    )
                )

                playerNameFlow.update { "" }
                playerNumberFlow.update { "" }

            } catch (e: Exception) {
                // Handle errors - could emit error state
            }
        }
    }

    fun onFinish() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                insertPlayersUseCase(listOfPlayers)
            } catch (e: Exception) {
                // Handle errors
            }
        }
    }

    /**
     * Validates team name input
     */
    fun updateTeamName(name: String) {
        teamNameFlow.update { ValueAndValidation(name, true) }
    }

    /**
     * Updates player name with basic validation
     */
    fun updatePlayerName(name: String) {
        // Allow only reasonable name lengths
        if (name.length <= 50) {
            playerNameFlow.update { name }
        }
    }

    /**
     * Updates player number with validation (0-99)
     */
    fun updatePlayerNumber(number: String) {
        if (number.isEmpty()) {
            playerNumberFlow.update { number }
        } else {
            val numValue = number.toIntOrNull()
            if (numValue != null && numValue in 0..99) {
                playerNumberFlow.update { number }
            }
        }
    }

    /**
     * Returns the current number of players added
     */
    fun getPlayersCount(): Int = listOfPlayers.size

    /**
     * Validates if player data is complete and valid
     */
    private fun isPlayerDataValid(name: String, number: String): Boolean {
        val trimmedName = name.trim()
        val numValue = number.toIntOrNull()

        return trimmedName.isNotEmpty() &&
                trimmedName.length >= 2 &&
                numValue != null &&
                numValue in 0..99 &&
                !isPlayerNumberTaken(number)
    }

    /**
     * Checks if a player number is already taken
     */
    private fun isPlayerNumberTaken(number: String): Boolean {
        return listOfPlayers.any { it.number == number }
    }

    /**
     * Gets list of taken numbers for UI feedback if needed
     */
    fun getTakenNumbers(): List<String> =
        listOfPlayers.map { it.number } // TODO Add it to the ui checks

    private enum class Form {
        ADD_TEAM,
        ADD_PLAYER,
    }
}

sealed interface CreateTeamUiState {
    data class AddTeamName(
        val teamName: String,
        val isErrorMessageShow: Boolean = false
    ) : CreateTeamUiState

    data class AddPlayer(
        val playerName: String,
        val playerNumber: String,
        val isNextButtonEnabled: Boolean = false,
        val isFinishButtonEnabled: Boolean = false
    ) : CreateTeamUiState

    data object Loading : CreateTeamUiState
}
