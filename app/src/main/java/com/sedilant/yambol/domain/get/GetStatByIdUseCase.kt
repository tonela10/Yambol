package com.sedilant.yambol.domain.get

import com.sedilant.yambol.ui.playerCard.StatUiModel

interface GetStatByIdUseCase {
    suspend operator fun invoke(statId: Int): StatUiModel
}