package com.sedilant.yambol.domain.get

import com.sedilant.yambol.domain.models.TrainDomainModel

interface GetAllTrainsByTeamIdUseCase {
    suspend operator fun invoke(teamId: Int): List<TrainDomainModel>
}
