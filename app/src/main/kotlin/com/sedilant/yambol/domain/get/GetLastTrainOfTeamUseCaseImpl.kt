package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TrainRepository

class GetLastTrainOfTeamUseCaseImpl(
    private val trainRepository: TrainRepository,
    private val authRepository: AuthRepository
) : GetLastTrainOfTeamUseCase {
    override suspend fun invoke(teamId: String?): String? {
        val userId = authRepository.currentUser?.uid
        return if (teamId != null && userId != null) {
            trainRepository.getLastTrainId(userId, teamId)
        } else null
    }
}
