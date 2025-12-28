package com.sedilant.yambol.domain.insert

interface CreateTrainTaskUseCase {
    suspend operator fun invoke(
        trainId: Long,
        name: String,
        numberOfPlayer: Int,
        concept: String,
        description: String,
        variables: List<String>
    )
}
