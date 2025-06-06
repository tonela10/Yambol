package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.TeamRepository
import com.sedilant.yambol.domain.mapToDomain
import com.sedilant.yambol.domain.models.TrainWithTaskDomainModel
import java.util.Date
import javax.inject.Inject

class GetTrainWithTrainTaskByTrainIdUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) :
    GetTrainWithTrainTaskByTrainIdUseCase {
    override suspend fun invoke(trainId: Int): TrainWithTaskDomainModel {
        val it = teamRepository.getTrainWithTrainTaskByTrainId(trainId)
        return TrainWithTaskDomainModel(
            trainId = it.train.trainId,
            date = Date(it.train.date),
            time = it.train.time,
            concepts = it.train.concepts,
            teamId = it.train.teamId,
            trains = it.tasks.map { it.mapToDomain() }
        )
    }
}
