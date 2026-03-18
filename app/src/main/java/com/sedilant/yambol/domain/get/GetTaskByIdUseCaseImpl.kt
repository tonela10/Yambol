package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TaskRepository
import com.sedilant.yambol.domain.models.TaskDomain
import javax.inject.Inject

class GetTaskByIdUseCaseImpl @Inject constructor(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository,
) : GetTaskByIdUseCase {
    override suspend fun invoke(taskId: String): TaskDomain? {
        val userId = authRepository.currentUser?.uid ?: return null
        val dto = taskRepository.getTasksByIds(userId, listOf(taskId)).firstOrNull() ?: return null

        return TaskDomain(
            trainingTaskId = dto.id,
            name = dto.name,
            concepts = dto.conceptIds,
            description = dto.description,
            variables = dto.variables,
        )
    }
}

