package com.sedilant.yambol.data.draftTrain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

// TODO Change to English
class TrainingDraftRepositoryImpl(
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
                    date = Clock.System.now(),
                    endTime = 90f,
                    startTime = getCurrentHourAsFloat(),
                    conceptIds = emptyList(),
                    tasks = emptyList(),
                    teamId = ""
                )
                createDraft(newTraining)
            }
        } catch (e: Exception) {
            val newTraining = Training(
                date = Clock.System.now(),
                endTime = 90f,
                startTime = getCurrentHourAsFloat(),
                conceptIds = emptyList(),
                tasks = emptyList(),
                teamId = ""
            )
            createDraft(newTraining)
        }
    }

    private fun getCurrentHourAsFloat(): Float {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return now.hour + (now.minute / 60f)
    }

    override suspend fun updateTrainingData(
        id: String,
        date: Instant?,
        endTime: Float?,
        startTime: Float?,
        concepts: List<String>?,
        teamId: String?
    ) {
        val current = trainingDraftDao.getTrainingDraftById(id)
            ?: return // TODO ADD EXCEPTION OIE CARALHO ESTO NO EXISTE POS POR ALGUNA RAZÓN QUE NO COMMPRENDO

        val updated = current.copy(
            date = date?.toEpochMilliseconds() ?: current.date,
            endTime = endTime ?: current.endTime,
            startTime = startTime ?: current.startTime,
            concepts = concepts ?: current.concepts,
            updatedAt = System.currentTimeMillis(),
            teamId = teamId ?: current.teamId
        )
        trainingDraftDao.updateTrainingDraft(updated)
    }

    // TODO check and improve how this is manage
    override suspend fun updateTasksList(trainingId: String, tasks: List<Task>) {
        // 1. Convert domain tasks to entities
        // 2. The mapIndexed ensures the orderIndex is exactly 0, 1, 2... based on list position
        val entities = tasks.mapIndexed { index, task ->
            task.toEntity(trainingId, index)
        }

        // 3. We insert/replace. This updates orderIndex for everyone.
        trainingDraftDao.insertTasks(entities)

        // 4. Important: If the 'tasks' list passed is smaller than the DB (item deleted),
        // you need to remove the ones not present in the new list.
        val existingTasks = trainingDraftDao.getTasksForTraining(trainingId)
        val newIds = entities.map { it.id }
        existingTasks.forEach { existing ->
            if (existing.id !in newIds) {
                trainingDraftDao.deleteTask(existing.id)
            }
        }

        trainingDraftDao.updateTimestamp(trainingId)
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
        return trainingDraftDao.observeDraftById(id).map {
            if (it == null) return@map null // throw an error or sommething when null
            it.toDomain()
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