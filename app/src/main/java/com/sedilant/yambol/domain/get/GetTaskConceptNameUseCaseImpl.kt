package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.ConceptRepository
import javax.inject.Inject

class GetTaskConceptNameUseCaseImpl @Inject constructor(
    private val conceptRepository: ConceptRepository,
    private val authRepository: AuthRepository,
) : GetTaskConceptNameUseCase {
    override suspend fun invoke(conceptIds: List<String>): Map<String, String> {
        val userId = authRepository.currentUser?.uid ?: return emptyMap()
        val sanitizedIds = conceptIds.distinct().filter { it.isNotBlank() }
        if (sanitizedIds.isEmpty()) return emptyMap()

        return conceptRepository
            .getConceptsByIds(userId = userId, ids = sanitizedIds)
            .mapNotNull { concept ->
                val name = concept.name.trim()
                if (name.isBlank()) null else concept.id to name
            }
            .toMap()
    }
}

