package com.sedilant.yambol.domain.get

import com.sedilant.yambol.ui.home.models.PlayerUiModel

interface GetPlayerByIdUseCase{
    suspend operator fun invoke (id: Int): PlayerUiModel
}