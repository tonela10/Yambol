package com.sedilant.yambol.data.draftTrain

import java.util.Date
import java.util.UUID

data class Training(
    val id: String = UUID.randomUUID().toString(),
    val date: Date,
    val duration: Float,
    val hour: Float,
    val concepts: List<String>,
    val tasks: List<Task>
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
        duration = this.duration,
        hour = this.hour,
        concepts = this.concepts.split(",").filter { it.isNotBlank() },
        tasks = tasks.map { it.toDomain() }
    )
}

fun Training.toEntity(): TrainingDraftEntity {
    return TrainingDraftEntity(
        id = this.id,
        date = this.date.time,
        duration = this.duration,
        hour = this.hour,
        concepts = this.concepts.joinToString(",")
    )
}

fun TrainingTaskEntity.toDomain(): Task {
    return Task(
        id = this.id,
        name = this.name,
        concepts = this.concepts.split(",").filter { it.isNotBlank() },
        description = this.description,
        variation = this.variation
    )
}

fun Task.toEntity(trainingId: String, orderIndex: Int): TrainingTaskEntity {
    return TrainingTaskEntity(
        id = this.id,
        trainingId = trainingId,
        name = this.name,
        concepts = this.concepts.joinToString(","),
        description = this.description,
        variation = this.variation,
        orderIndex = orderIndex
    )
}

fun TrainingWithTasks.toDomain(): Training {
    return this.training.toDomain(this.tasks)
}