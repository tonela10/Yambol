package com.sedilant.yambol.feature.createTrain.stepsScreens.basicInfo

import com.sedilant.yambol.domain.models.TeamDomainModel
import kotlinx.datetime.Instant

sealed class BasicInfoUiState {
    data object Loading : BasicInfoUiState()
    data class Success(
        val selectedDate: Instant,
        val startHour: Float,
        val endHour: Float,
        val teamsList: List<TeamDomainModel>,
        val selectedTeamId: String
    ) : BasicInfoUiState()
    data class Error(val message: String) : BasicInfoUiState()
}
