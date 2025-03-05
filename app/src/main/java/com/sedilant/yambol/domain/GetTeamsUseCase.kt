package com.sedilant.yambol.domain

import com.sedilant.yambol.ui.home.models.TeamUiModel
import kotlinx.coroutines.flow.Flow

interface GetTeamsUseCase {
    suspend operator fun invoke(): Flow<List<TeamUiModel>>
}
