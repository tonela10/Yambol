package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TaskRepository
import com.sedilant.yambol.data.firestore.TrainRepository
import com.sedilant.yambol.domain.models.TaskDomain
import com.sedilant.yambol.domain.models.TrainWithTaskDomainModel
import java.util.Date
import javax.inject.Inject

class GetTrainWithTrainTaskByTrainIdUseCaseImpl @Inject constructor(
    private val trainRepository: TrainRepository,
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : GetTrainWithTrainTaskByTrainIdUseCase {
    override suspend fun invoke(trainId: String): TrainWithTaskDomainModel {
        // Fetch train
        val train = trainRepository.getTrainById(trainId)
            ?: throw IllegalArgumentException("Train with id $trainId not found")

        val userId = authRepository.currentUser?.uid ?: ""

        // Fetch associated tasks
        val tasks = if (train.taskIds.isNotEmpty() && userId.isNotEmpty()) {
            val taskDtos = taskRepository.getTasksByIds(userId, train.taskIds)
            taskDtos.map { dto ->
                TaskDomain(
                    trainingTaskId = dto.id, // DTO ID is String now, Domain expects String
                    name = dto.name,
                    concepts = dto.conceptIds,
                    description = dto.description,
                    variables = dto.variables
                )
            }
        } else {
            emptyList()
        }

        val duration = if (train.endTime != null && train.startTime != null) {
            train.endTime - train.startTime
        } else {
            0f
        }

        return TrainWithTaskDomainModel(
            trainId = train.id,
            date = Date(train.dateMillis ?: 0),
            time = duration,
            conceptIds = train.conceptIds,
            teamId = train.teamId,
            trains = tasks
        )
    }
}
