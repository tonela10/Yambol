package com.sedilant.yambol.domain

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.PlayerRepository
import javax.inject.Inject

class CheckJerseyNumberUseCaseImpl @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val authRepository: AuthRepository
) : CheckJerseyNumberUseCase {
    override suspend fun invoke(teamId: String, jerseyNumber: Int, excludePlayerId: String?): Boolean {
        val userId = authRepository.currentUser?.uid ?: return false
        return playerRepository.isJerseyNumberTaken(userId, teamId, jerseyNumber, excludePlayerId)
    }
}
