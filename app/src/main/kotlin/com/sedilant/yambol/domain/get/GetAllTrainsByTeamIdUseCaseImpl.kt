package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TrainRepository
import com.sedilant.yambol.domain.models.TrainDomainModel
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Instant

class GetAllTrainsByTeamIdUseCaseImpl(
    private val trainRepository: TrainRepository,
    private val authRepository: AuthRepository
) : GetAllTrainsByTeamIdUseCase {
    override suspend fun invoke(teamId: String): List<TrainDomainModel> {
        val userId = authRepository.currentUser?.uid ?: return emptyList()
        // listByTeamFlow returns Flow, but use case returns List.
        // Assuming we want snapshot. converting Flow to List by taking first element or changing UseCase to return Flow.
        // Given original was suspend returning List, it implies a one-shot fetch.
        // I will use .first() on the flow to get current state.

        return trainRepository.listByTeamFlow(userId, teamId).first().map {
            val duration = if (it.endTime != null && it.startTime != null) {
                it.endTime - it.startTime
            } else {
                0f
            }
            TrainDomainModel(
                id = it.id,
                date = Instant.fromEpochMilliseconds(it.dateMillis ?: 0),
                time = duration,
                concepts = it.conceptIds,
                teamId = it.teamId
            )
        }
    }
}
