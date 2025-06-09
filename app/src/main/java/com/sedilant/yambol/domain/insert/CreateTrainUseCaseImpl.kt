package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.TeamRepository
import com.sedilant.yambol.data.entities.TrainEntity
import java.util.Date
import javax.inject.Inject

class CreateTrainUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : CreateTrainUseCase {
    override suspend fun invoke(date: Date, time: Float, concepts: List<String>, teamId: Int): Int {
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
