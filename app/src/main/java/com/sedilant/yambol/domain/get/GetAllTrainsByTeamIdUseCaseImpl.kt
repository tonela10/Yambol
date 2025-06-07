package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.TeamRepository
import com.sedilant.yambol.domain.models.TrainDomainModel
import java.util.Date
import javax.inject.Inject

class GetAllTrainsByTeamIdUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : GetAllTrainsByTeamIdUseCase {
    override suspend fun invoke(teamId: Int): List<TrainDomainModel> {
        return teamRepository.getAllTrainingsByTeamId(teamId).map {
            TrainDomainModel(
                id = it.trainId,
                date = Date(it.date),
                time = it.time,
                concepts = it.concepts,
                teamId = it.teamId
            )
        }
    }
}
