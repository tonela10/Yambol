package com.sedilant.yambol.domain.insert

interface CreateTrainTaskUseCase {
    suspend operator fun invoke(
        trainId: Int,
        name: String,
        numberOfPlayer: Int,
        concept: String,
        description: String,
        variables: List<String>
    )
}
