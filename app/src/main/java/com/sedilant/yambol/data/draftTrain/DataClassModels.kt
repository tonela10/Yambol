package com.sedilant.yambol.data.draftTrain

import java.util.Date
import java.util.UUID

data class Training(
    val id: String = UUID.randomUUID().toString(),
    val date: Date,
    val endTime: Float,
    val startTime: Float,
    val concepts: List<String>,
    val tasks: List<Task>,
    val teamId: String
)

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val concepts: List<String>,
    val description: String,
    val variation: String
)

// Mappers
fun TrainingDraftEntity.toDomain(tasks: List<TrainingTaskEntity>): Training {
    return Training(
        id = this.id,
        date = Date(this.date),
        endTime = this.endTime,
        startTime = this.startTime,
        concepts = this.concepts,
        tasks = tasks.map { it.toDomain() },
        teamId = teamId
    )
}

fun Training.toEntity(): TrainingDraftEntity {
    return TrainingDraftEntity(
        id = this.id,
        date = this.date.time,
        endTime = this.endTime,
        startTime = this.startTime,
        concepts = this.concepts,
        teamId = teamId
    )
}

fun TrainingTaskEntity.toDomain(): Task {
    return Task(
        id = this.id,
        name = this.name,
        concepts = this.conceptIds,
        description = this.description,
        variation = this.variation
    )
}

fun Task.toEntity(trainingId: String, orderIndex: Int): TrainingTaskEntity {
    return TrainingTaskEntity(
        id = this.id,
        trainingId = trainingId,
        name = this.name,
        conceptIds = this.concepts,
        description = this.description,
        variation = this.variation,
        orderIndex = orderIndex
    )
}

// In TrainingDraftEntity.kt

fun TrainingWithTasks.toDomain(): Training {
    return Training(
        id = this.training.id,
        date = java.util.Date(this.training.date), // Converting Long to Date
        endTime = this.training.endTime,
        startTime = this.training.startTime,
        concepts = this.training.concepts,
        tasks = this.tasks.map { taskEntity ->
            Task(
                id = taskEntity.id,
                name = taskEntity.name,
                concepts = taskEntity.conceptIds,
                description = taskEntity.description,
                variation = taskEntity.variation
            )
        },
        teamId = this.training.teamId
    )
}
