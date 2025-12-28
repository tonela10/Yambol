package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.team.TeamRepository
import com.sedilant.yambol.domain.models.TrainDomainModel
import java.util.Date
import javax.inject.Inject

class GetAllTrainsByTeamIdUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : GetAllTrainsByTeamIdUseCase {
    override suspend fun invoke(teamId: Long): List<TrainDomainModel> {
        return teamRepository.getAllTrainingsByTeamId(teamId).map {
            TrainDomainModel(
                id = it.id,
                date = Date(it.date),
                time = it.time,
                concepts = it.concepts,
                teamId = it.teamId
            )
        }
    }
}
