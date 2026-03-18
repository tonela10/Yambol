package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.PlayerRepository
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetPlayersByTeamIdUseCaseImpl @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val authRepository: AuthRepository
) : GetPlayersByTeamIdUseCase {
    override suspend fun invoke(teamId: String): Flow<List<PlayerUiModel>> {
        return authRepository.getAuthStateFlow().flatMapLatest { user ->
            val userId = user?.uid
            if (userId == null) {
                flowOf(emptyList())
            } else {
                playerRepository.listByTeamFlow(userId, teamId).map { list ->
                    list.map { dto ->
                        PlayerUiModel(
                            name = dto.name,
                            number = dto.number.toString(),
                            id = dto.id,
                            teamId = dto.teamId,
                        )
                    }
                }
            }
        }
    }
}
