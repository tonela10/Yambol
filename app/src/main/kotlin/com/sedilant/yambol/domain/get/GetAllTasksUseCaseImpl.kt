package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TaskRepository
import com.sedilant.yambol.domain.models.TaskDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class GetAllTasksUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : GetAllTaskUseCase {
    override fun invoke(): Flow<List<TaskDomain>> {
        return authRepository.getAuthStateFlow().flatMapLatest { user ->
            val userId = user?.uid
            if (userId == null) {
                flowOf(emptyList())
            } else {
                taskRepository.listByUserFlow(userId).map { list ->
                    list.map {
                        TaskDomain(
                            trainingTaskId = it.id,
                            name = it.name,
                            concepts = it.conceptIds,
                            description = it.description,
                            variables = it.variables
                        )
                    }
                }
            }
        }
    }
}
