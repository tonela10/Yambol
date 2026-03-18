package com.sedilant.yambol.domain.delete

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TaskRepository
import com.sedilant.yambol.data.firestore.TrainRepository
import javax.inject.Inject

class DeleteTaskUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository,
    private val trainRepository: TrainRepository,
) : DeleteTaskUseCase {
    override suspend fun invoke(taskId: String) {
        val userId = authRepository.currentUser?.uid ?: return

        // Keep training references consistent before deleting the task document.
        trainRepository.removeTaskFromAllTrains(userId, taskId)
        taskRepository.delete(taskId)
    }
}

