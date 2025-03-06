package com.sedilant.yambol.domain

import com.sedilant.yambol.ui.home.models.PlayerUiModel
import kotlinx.coroutines.flow.Flow

interface GetPlayersUseCase {
    suspend operator fun invoke(teamId: Int): Flow<List<PlayerUiModel>>
}
