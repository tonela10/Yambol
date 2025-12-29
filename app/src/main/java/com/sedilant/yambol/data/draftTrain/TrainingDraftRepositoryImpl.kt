package com.sedilant.yambol.data.draftTrain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

// TODO Change to English
class TrainingDraftRepositoryImpl @Inject constructor(
    private val trainingDraftDao: TrainingDraftDao
) : TrainingDraftRepository {
    override suspend fun createDraft(training: Training): String {
        val entity = training.toEntity()
        trainingDraftDao.insertTrainingDraft(entity)

        val taskEntities = training.tasks.mapIndexed { index, task ->
            task.toEntity(entity.id, index)
        }
        trainingDraftDao.insertTasks(taskEntities)
        return entity.id
    }

    override suspend fun getOrCreateActiveDraft(): String {
        return try {
            // Try to get the first draft
            val drafts = mutableListOf<TrainingDraftEntity>()
            trainingDraftDao.getAllDrafts().first().also { drafts.addAll(it) }

            if (drafts.isNotEmpty()) {
                drafts.first().id
            } else {
                // Create a new draft
                val newTraining = Training(
                    date = Date(),
                    duration = 90f,
                    hour = getCurrentHourAsFloat(),
                    concepts = emptyList(),
                    tasks = emptyList(),
                    teamId = 0
                )
                createDraft(newTraining)
            }
        } catch (e: Exception) {
            val newTraining = Training(
                date = Date(),
                duration = 90f,
                hour = getCurrentHourAsFloat(),
                concepts = emptyList(),
                tasks = emptyList(),
                teamId = 0
            )
            createDraft(newTraining)
        }
    }

    private fun getCurrentHourAsFloat(): Float {
        val calendar = java.util.Calendar.getInstance()
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = calendar.get(java.util.Calendar.MINUTE)
        return hour + (minute / 60f)
    }

    override suspend fun updateTrainingData(
        id: String,
        date: Date?,
        endTime: Float?,
        startTime: Float?,
        concepts: List<String>?,
        teamId: Long?
    ) {
        val current = trainingDraftDao.getTrainingDraftById(id)
            ?: return // TODO ADD EXCEPTION OIE CARALHO ESTO NO EXISTE POS POR ALGUNA RAZÓN QUE NO COMMPRENDO

        val updated = current.copy(
            date = date?.time ?: current.date,
            endTime = endTime ?: current.endTime,
            startTime = startTime ?: current.startTime,
            concepts = concepts?.joinToString(",") ?: current.concepts,
            updatedAt = System.currentTimeMillis(),
            teamId = teamId ?: current.teamId
        )
        trainingDraftDao.updateTrainingDraft(updated)
    }

    override suspend fun addTask(trainingId: String, task: Task) {
        val existingTasks = trainingDraftDao.getTasksForTraining(trainingId)
        val newOrderIndex = existingTasks.size

        trainingDraftDao.insertTask(task.toEntity(trainingId, newOrderIndex))
        trainingDraftDao.updateTimestamp(trainingId)
    }

    override suspend fun updateTask(trainingId: String, task: Task) {
        val existingTask = trainingDraftDao.getTasksForTraining(trainingId)
            .find { it.id == task.id } ?: return

        val updated = task.toEntity(trainingId, existingTask.orderIndex)
        trainingDraftDao.updateTask(updated)
        trainingDraftDao.updateTimestamp(trainingId)
    }

    override suspend fun removeTask(trainingId: String, taskId: String) {
        trainingDraftDao.deleteTask(taskId)
        trainingDraftDao.updateTimestamp(trainingId)
    }

    override suspend fun getDraft(id: String): Training? {
        return trainingDraftDao.getTrainingWithTasks(id)?.toDomain()
    }

    override fun observeDraft(id: String): Flow<Training?> {
        return trainingDraftDao.getTasksForTrainingFlow(id).map { tasks ->
            val trainingEntity = trainingDraftDao.getTrainingDraftById(id) ?: return@map null
            trainingEntity.toDomain(tasks)
        }
    }

    override suspend fun finalizeDraft(id: String) {
        val current = trainingDraftDao.getTrainingDraftById(id) ?: return
        trainingDraftDao.updateTrainingDraft(
            current.copy(
                isCompleted = true,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun deleteDraft(id: String) {
        trainingDraftDao.deleteTrainingDraft(id)
    }

    override fun getAllDrafts(): Flow<List<Training>> {
        return trainingDraftDao.getAllDraftsWithTasks().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAllCompletedTrainings(): Flow<List<Training>> {
        return trainingDraftDao.getAllCompletedTrainings().map { trainings ->
            trainings.map { training ->
                val tasks = trainingDraftDao.getTasksForTraining(training.id)
                training.toDomain(tasks)
            }
        }
    }

    override suspend fun cleanOldDrafts(daysOld: Int) {
        val cutoffTime = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L)
        trainingDraftDao.deleteOldDrafts(cutoffTime)
    }
}