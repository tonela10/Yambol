package com.sedilant.yambol.domain

import com.sedilant.yambol.data.TeamRepository
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import javax.inject.Inject

class GetPlayerByIdUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository,
) : GetPlayerByIdUseCase {
    override suspend fun invoke(id: Int): PlayerUiModel {
        return teamRepository.getPlayer(id)
    }
}