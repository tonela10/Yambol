package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.team.TeamRepository
import com.sedilant.yambol.domain.models.TaskDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllTasksUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository // TODO create a train repository
) : GetAllTaskUseCase {
    override fun invoke(): Flow<List<TaskDomain>> {
        return teamRepository.getAllTasks().map { list ->
            list.map {
                TaskDomain(
                    trainingTaskId = it.id,
                    name = it.name,
                    concepts = it.conceptsId,
                    description = it.description,
                    variables = it.variables
                )
            }
        }
    }
}
