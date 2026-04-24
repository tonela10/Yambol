package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TrainDto
import com.sedilant.yambol.data.firestore.TrainRepository
import kotlinx.datetime.Instant

class CreateTrainUseCaseImpl(
    private val trainRepository: TrainRepository,
    private val authRepository: AuthRepository
) : CreateTrainUseCase {
    override suspend fun invoke(
        date: Instant,
        startTime: Float,
        endTime: Float,
        concepts: List<String>,
        teamId: String
    ): String {
        val userId = authRepository.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val train = TrainDto(
            dateMillis = date.toEpochMilliseconds(),
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
