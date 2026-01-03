package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.team.TeamRepository
import com.sedilant.yambol.data.team.TrainCrossTrainTaskEntity
import com.sedilant.yambol.data.team.TaskEntity
import javax.inject.Inject

class CreateTrainTaskUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : CreateTrainTaskUseCase {
    override suspend fun invoke(
        trainId: Long,
        name: String,
        numberOfPlayer: Int,
        concept: List<Long>,
        description: String,
        variables: List<String>
    ) {
        val taskId = teamRepository.insertTrainTask(
            TaskEntity(
                name = name,
                conceptsId = concept,
                description = description,
                variables = variables,
                corrections = null
            )
        )
        teamRepository.insertTrainCrossTrainTask(
            TrainCrossTrainTaskEntity(
                trainId = trainId,
                taskId = taskId
            )
        )
    }
}
