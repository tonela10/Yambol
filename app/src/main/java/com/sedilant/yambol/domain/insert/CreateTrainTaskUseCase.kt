package com.sedilant.yambol.domain.insert

interface CreateTrainTaskUseCase {
    suspend operator fun invoke(
        trainId: Long,
        name: String,
        numberOfPlayer: Int,
        concept: List<Long>,
        description: String,
        variables: List<String>
    )
}
