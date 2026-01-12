package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.PlayerRepository
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPlayersByTeamIdUseCaseImpl @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val authRepository: AuthRepository
) : GetPlayersByTeamIdUseCase {
    override suspend fun invoke(teamId: String): Flow<List<PlayerUiModel>> {
        val userId = authRepository.currentUser?.uid ?: return flowOf(emptyList())

        return playerRepository.listByTeamFlow(userId, teamId).map { list ->
            list.map { dto ->
                PlayerUiModel(
                    name = dto.name,
                    number = dto.number.toString(),
                    id = dto.id,
                    teamId = dto.teamId,
                    position = "" // TODO: Add position to PlayerDto and here
                )
            }
        }
    }
}
