package com.sedilant.yambol.domain.insert

interface CreateTrainTaskUseCase {
    suspend operator fun invoke(
        taskId: String,
        trainId: String? = null,
        name: String,
        concept: List<String>,
        description: String,
        variables: List<String>
    )
}
