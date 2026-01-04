package com.sedilant.yambol.domain.get

import com.sedilant.yambol.domain.models.TaskDomain
import kotlinx.coroutines.flow.Flow

fun interface GetAllTaskUseCase {
    operator fun invoke(): Flow<List<TaskDomain>>
}
