package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TeamDto
import com.sedilant.yambol.data.firestore.TeamRepository

class InsertTeamUseCaseImpl(
    private val teamRepository: TeamRepository,
    private val authRepository: AuthRepository
) : InsertTeamUseCase {
    override suspend fun invoke(name: String): String {
        val userId = authRepository.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        return teamRepository.upsert(
            TeamDto(
                name = name.lowercase(),
                userId = userId
            )
        )
    }
}
