package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TrainDto
import com.sedilant.yambol.data.firestore.TrainRepository
import java.util.Date
import javax.inject.Inject

class CreateTrainUseCaseImpl @Inject constructor(
    private val trainRepository: TrainRepository,
    private val authRepository: AuthRepository
) : CreateTrainUseCase {
    override suspend fun invoke(
        date: Date,
        startTime: Float,
        endTime: Float,
        concepts: List<String>,
        teamId: String
    ): String {
        val userId = authRepository.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val train = TrainDto(
            dateMillis = date.time,
            startTime = startTime,
            endTime = endTime,
            teamId = teamId,
            userId = userId,
            conceptIds = concepts,
            taskIds = emptyList()
        )
        return trainRepository.upsert(train) // Returns ID
    }
}
