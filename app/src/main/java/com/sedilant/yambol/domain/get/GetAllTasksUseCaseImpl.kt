package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.TaskRepository
import com.sedilant.yambol.domain.models.TaskDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllTasksUseCaseImpl @Inject constructor(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : GetAllTaskUseCase {
    override fun invoke(): Flow<List<TaskDomain>> {
        val userId = authRepository.currentUser?.uid ?: return flowOf(emptyList())

        return taskRepository.listByUserFlow(userId).map { list ->
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
