package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.ui.home.models.PlayerUiModel

interface InsertPlayersUseCase {
    suspend operator fun invoke(players: List<PlayerUiModel>)
}