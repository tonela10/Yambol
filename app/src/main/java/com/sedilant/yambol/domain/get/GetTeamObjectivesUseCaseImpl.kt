package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TeamObjectiveRepository
import com.sedilant.yambol.domain.models.TeamObjectivesDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTeamObjectivesUseCaseImpl @Inject constructor(
    private val teamObjectiveRepository: TeamObjectiveRepository,
    private val authRepository: AuthRepository
) : GetTeamObjectivesUseCase {
    override suspend fun invoke(teamId: String): Flow<List<TeamObjectivesDomainModel>> {
        val userId = authRepository.currentUser?.uid ?: return flowOf(emptyList())

        return teamObjectiveRepository.listByTeamFlow(userId, teamId).map { list ->
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
