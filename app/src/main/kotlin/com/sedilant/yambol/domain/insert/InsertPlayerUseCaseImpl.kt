package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.PlayerDto
import com.sedilant.yambol.data.firestore.PlayerRepository
import com.sedilant.yambol.ui.home.models.PlayerUiModel

class InsertPlayerUseCaseImpl(
    private val playerRepository: PlayerRepository,
    private val authRepository: AuthRepository
) : InsertPlayerUseCase {
    override suspend fun invoke(playerUiModel: PlayerUiModel) {
        val userId = authRepository.currentUser?.uid ?: return
        playerRepository.upsert(
            PlayerDto(
                name = playerUiModel.name.lowercase(),
                number = playerUiModel.number.toIntOrNull(),
                teamId = playerUiModel.teamId,
                userId = userId
            )
        )
    }
}
