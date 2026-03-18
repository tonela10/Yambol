package com.sedilant.yambol.domain.get

fun interface GetTaskConceptNameUseCase {
    suspend operator fun invoke(conceptIds: List<String>): Map<String, String>
}

