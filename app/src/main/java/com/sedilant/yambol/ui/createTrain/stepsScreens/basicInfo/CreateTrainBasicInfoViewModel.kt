package com.sedilant.yambol.ui.createTrain.stepsScreens.basicInfo

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CreateTrainBasicInfoViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    public val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    public fun onTeamSelected(team: String) {
        TODO()
    }

    public fun onSaveInfo(day: String, time: String, duration: String) {
        TODO()
    }

    data class UiState(
        val teamsList: List<String> = emptyList(),
        val selectedTeam: String = "",
    )
}