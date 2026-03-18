package com.sedilant.yambol.domain

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TeamObjectiveDto
import com.sedilant.yambol.data.firestore.TeamObjectiveRepository
import javax.inject.Inject

class UpdateTeamObjectiveUseCaseImpl @Inject constructor(
    private val repository: TeamObjectiveRepository,
    private val authRepository: AuthRepository
) : UpdateTeamObjectiveUseCase {
    override suspend fun invoke(
        objectiveId: String,
        newDescription: String,
        isCompleted: Boolean,
        teamId: String
    ) {
        val userId = authRepository.currentUser?.uid ?: return
        repository.upsert(
            TeamObjectiveDto(
                id = objectiveId,
                title = newDescription,
                userId = userId,
                completed = isCompleted,
                teamId = teamId
            )
        )
    }
}
