package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TeamRepository
import com.sedilant.yambol.domain.models.TeamDomainModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetTeamsUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository,
    private val authRepository: AuthRepository
) : GetTeamsUseCase {
    override suspend fun invoke(): Flow<List<TeamDomainModel>> {
        return authRepository.getAuthStateFlow().flatMapLatest { user ->
            val userId = user?.uid
            if (userId == null) {
                flowOf(emptyList())
            } else {
                teamRepository.listByUserFlow(userId).map { list ->
                    list.map { dto ->
                        TeamDomainModel(
                            id = dto.id,
                            name = dto.name
                        )
                    }
                }
            }
        }
    }
}
