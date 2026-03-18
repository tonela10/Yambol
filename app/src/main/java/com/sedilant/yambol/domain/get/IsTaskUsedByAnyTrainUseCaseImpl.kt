package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TrainRepository
import javax.inject.Inject

class IsTaskUsedByAnyTrainUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val trainRepository: TrainRepository,
) : IsTaskUsedByAnyTrainUseCase {
    override suspend fun invoke(taskId: String): Boolean {
        val userId = authRepository.currentUser?.uid ?: return false
        return trainRepository.isTaskUsedByAnyTrain(userId, taskId)
    }
}

