package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TeamObjectiveRepository
import com.sedilant.yambol.domain.models.TeamObjectivesDomainModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class GetTeamObjectivesUseCaseImpl(
    private val teamObjectiveRepository: TeamObjectiveRepository,
    private val authRepository: AuthRepository
) : GetTeamObjectivesUseCase {
    override suspend fun invoke(teamId: String): Flow<List<TeamObjectivesDomainModel>> {
        return authRepository.getAuthStateFlow().flatMapLatest { user ->
            val userId = user?.uid
            if (userId == null) {
                flowOf(emptyList())
            } else {
                teamObjectiveRepository.listByTeamFlow(userId, teamId).map { list ->
                    list.map {
                        TeamObjectivesDomainModel(
                            description = it.title,
                            isFinish = it.completed,
                            id = it.id
                        )
                    }
                }
            }
        }
    }
}
