package com.sedilant.yambol.domain.get

interface IsTaskUsedByAnyTrainUseCase {
    suspend operator fun invoke(taskId: String): Boolean
}

