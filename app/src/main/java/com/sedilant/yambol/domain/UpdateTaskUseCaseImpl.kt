package com.sedilant.yambol.domain

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TaskDto
import com.sedilant.yambol.data.firestore.TaskRepository
import javax.inject.Inject

class UpdateTaskUseCaseImpl @Inject constructor(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository,
) : UpdateTaskUseCase {
    override suspend fun invoke(
        taskId: String,
        name: String,
        description: String,
        variables: List<String>,
        conceptIds: List<String>,
    ) {
        val userId = authRepository.currentUser?.uid ?: return

        taskRepository.upsert(
            TaskDto(
                id = taskId,
                name = name.trim(),
                description = description.trim(),
                variables = variables.map { it.trim() }.filter { it.isNotBlank() },
                conceptIds = conceptIds.distinct().filter { it.isNotBlank() },
                userId = userId,
            )
        )
    }
}

