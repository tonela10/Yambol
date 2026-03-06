package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TaskDto
import com.sedilant.yambol.data.firestore.TaskRepository
import com.sedilant.yambol.data.firestore.TrainRepository
import javax.inject.Inject

class CreateTrainTaskUseCaseImpl @Inject constructor(
    private val taskRepository: TaskRepository,
    private val trainRepository: TrainRepository,
    private val authRepository: AuthRepository
) : CreateTrainTaskUseCase {
    override suspend fun invoke(
        taskId: String,
        trainId: String?,
        name: String,
        concept: List<String>,
        description: String,
        variables: List<String>
    ) {
        val userId = authRepository.currentUser?.uid ?: return

        // create task
        val finalTaskId = taskRepository.upsert(
            TaskDto(
                id = taskId,
                name = name,
                description = description,
                variables = variables,
                conceptIds = concept,
                userId = userId
            )
        )

        // link to train
        if (!trainId.isNullOrBlank()) {
            trainRepository.addTaskToTrain(trainId, finalTaskId)
        }
    }
}
