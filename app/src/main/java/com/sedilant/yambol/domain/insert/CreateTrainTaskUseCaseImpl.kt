package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.TeamRepository
import com.sedilant.yambol.data.entities.TrainCrossTrainTaskEntity
import com.sedilant.yambol.data.entities.TrainTaskEntity
import javax.inject.Inject

class CreateTrainTaskUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : CreateTrainTaskUseCase {
    override suspend fun invoke(
        trainId: Int,
        name: String,
        numberOfPlayer: Int,
        concept: String,
        description: String,
        variables: List<String>
    ) {
        val trainTaskId = teamRepository.insertTrainTask(
            TrainTaskEntity(
                name = name,
                numberOfPlayers = numberOfPlayer,
                concept = concept,
                description = description,
                variables = variables
            )
        )
        teamRepository.insertTrainCrossTrainTask(
            TrainCrossTrainTaskEntity(
                trainId = trainId,
                trainingTaskId = trainTaskId
            )
        )
    }
}
