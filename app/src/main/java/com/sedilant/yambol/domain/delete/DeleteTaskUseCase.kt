package com.sedilant.yambol.domain.delete

interface DeleteTaskUseCase {
    suspend operator fun invoke(taskId: String)
}

