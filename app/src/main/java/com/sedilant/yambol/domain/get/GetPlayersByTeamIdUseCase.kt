package com.sedilant.yambol.domain.get

import com.sedilant.yambol.ui.home.models.PlayerUiModel
import kotlinx.coroutines.flow.Flow

interface GetPlayersByTeamIdUseCase {
    suspend operator fun invoke(teamId: Int): Flow<List<PlayerUiModel>>
}
