package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.team.TeamRepository
import com.sedilant.yambol.data.team.TrainEntity
import java.util.Date
import javax.inject.Inject

class CreateTrainUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : CreateTrainUseCase {
    override suspend fun invoke(date: Date, time: Float, concepts: List<Long>, teamId: Long): Long {
        return teamRepository.insertTrain(
            TrainEntity(
                date = date.time,
                time = time,
                concepts = concepts,
                teamId = teamId
            )
        )
    }
}
