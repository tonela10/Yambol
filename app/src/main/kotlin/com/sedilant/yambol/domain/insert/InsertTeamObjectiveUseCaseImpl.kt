package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TeamObjectiveDto
import com.sedilant.yambol.data.firestore.TeamObjectiveRepository

class InsertTeamObjectiveUseCaseImpl(
    private val teamObjectiveRepository: TeamObjectiveRepository,
    private val authRepository: AuthRepository
) : InsertTeamObjectiveUseCase {
    override suspend fun invoke(description: String, teamId: String) {
        val userId = authRepository.currentUser?.uid ?: return
        teamObjectiveRepository.upsert(
            TeamObjectiveDto(
                title = description,
                completed = false,
                teamId = teamId,
                userId = userId
            )
        )
    }
}
