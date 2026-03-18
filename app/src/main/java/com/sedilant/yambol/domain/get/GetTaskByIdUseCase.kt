package com.sedilant.yambol.domain.get

import com.sedilant.yambol.domain.models.TaskDomain

interface GetTaskByIdUseCase {
    suspend operator fun invoke(taskId: String): TaskDomain?
}

