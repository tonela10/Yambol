package com.sedilant.yambol.domain

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.PlayerRepository
import javax.inject.Inject

class UpdatePlayerUseCaseImpl @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val authRepository: AuthRepository
) : UpdatePlayerUseCase {
    override suspend fun invoke(
        playerId: String,
        newName: String,
        newNumber: Int,
        teamId: String,
    ) {
        authRepository.currentUser?.uid ?: return
        playerRepository.update(playerId, newName, newNumber)
    }
}