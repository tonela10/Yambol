package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.team.TeamRepository
import com.sedilant.yambol.data.team.TrainCrossTrainTaskEntity
import com.sedilant.yambol.data.team.TaskEntity
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
            TaskEntity(
                name = name,
                concept = concept,
                description = description,
                variables = variables,
                corrections = null
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
