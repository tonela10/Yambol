package com.sedilant.yambol.data.draftTrain

import kotlinx.coroutines.flow.Flow
import java.util.Date

interface TrainingDraftRepository {

    suspend fun createDraft(training: Training): String
    suspend fun getOrCreateActiveDraft(): String
    suspend fun updateTrainingData(
        id: String,
        date: Date? = null,
        endTime: Float? = null,
        startTime: Float? = null,
        concepts: List<Long>? = null,
        teamId: Long? = null
    )
    suspend fun updateTasksList(trainingId: String, tasks: List<Task>)
    suspend fun addTask(trainingId: String, task: Task)
    suspend fun updateTask(trainingId: String, task: Task)
    suspend fun removeTask(trainingId: String, taskId: String)

    suspend fun getDraft(id: String): Training?
    fun observeDraft(id: String): Flow<Training?>
    suspend fun finalizeDraft(id: String)
    suspend fun deleteDraft(id: String)
    fun getAllDrafts(): Flow<List<Training>>
    fun getAllCompletedTrainings(): Flow<List<Training>>

    suspend fun cleanOldDrafts(daysOld: Int = 7)
}
