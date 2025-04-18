package com.sedilant.yambol.domain

import com.sedilant.yambol.ui.home.models.PlayerUiModel

interface InsertPlayerUseCase {
    suspend operator fun invoke(playerUiModel: PlayerUiModel, teamId: Int)
}
