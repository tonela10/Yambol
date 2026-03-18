package com.sedilant.yambol.domain

interface UpdateTaskUseCase {
    suspend operator fun invoke(
        taskId: String,
        name: String,
        description: String,
        variables: List<String>,
        conceptIds: List<String>,
    )
}

