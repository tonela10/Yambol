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

    // Crear nuevo borrador
    override suspend fun createDraft(training: Training): String {
        val entity = training.toEntity()
        trainingDraftDao.insertTrainingDraft(entity)

        val taskEntities = training.tasks.mapIndexed { index, task ->
            task.toEntity(entity.id, index)
        }
        trainingDraftDao.insertTasks(taskEntities)

        return entity.id
    }

    // Obtener o crear el borrador activo (solo puede haber uno)
    override suspend fun getOrCreateActiveDraft(): String {
        // Buscar si existe algún borrador
        return try {
            // Intentar obtener el primer borrador
            val drafts = mutableListOf<TrainingDraftEntity>()
            trainingDraftDao.getAllDrafts().first().also { drafts.addAll(it) }

            if (drafts.isNotEmpty()) {
                drafts.first().id
            } else {
                // Crear un nuevo borrador
                val newTraining = Training(
                    date = Date(),
                    duration = 90f,
                    hour = getCurrentHourAsFloat(),
                    concepts = emptyList(),
                    tasks = emptyList()
                )
                createDraft(newTraining)
            }
        } catch (e: Exception) {
            // Si hay error, crear un nuevo borrador
            val newTraining = Training(
                date = Date(),
                duration = 90f,
                hour = getCurrentHourAsFloat(),
                concepts = emptyList(),
                tasks = emptyList()
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

    // Actualizar datos del training (sin tasks)
    override suspend fun updateTrainingData(
        id: String,
        date: Date? ,
        duration: Float?,
        hour: Float?,
        concepts: List<String>?
    ) {
        val current = trainingDraftDao.getTrainingDraftById(id) ?: return

        val updated = current.copy(
            date = date?.time ?: current.date,
            duration = duration ?: current.duration,
            hour = hour ?: current.hour,
            concepts = concepts?.joinToString(",") ?: current.concepts,
            updatedAt = System.currentTimeMillis()
        )

        trainingDraftDao.updateTrainingDraft(updated)
    }

    // Añadir una tarea
    override suspend fun addTask(trainingId: String, task: Task) {
        val existingTasks = trainingDraftDao.getTasksForTraining(trainingId)
        val newOrderIndex = existingTasks.size

        trainingDraftDao.insertTask(task.toEntity(trainingId, newOrderIndex))
        trainingDraftDao.updateTimestamp(trainingId)
    }

    // Actualizar una tarea
    override suspend fun updateTask(trainingId: String, task: Task) {
        val existingTask = trainingDraftDao.getTasksForTraining(trainingId)
            .find { it.id == task.id } ?: return

        val updated = task.toEntity(trainingId, existingTask.orderIndex)
        trainingDraftDao.updateTask(updated)
        trainingDraftDao.updateTimestamp(trainingId)
    }

    // Eliminar una tarea
    override suspend fun removeTask(trainingId: String, taskId: String) {
        trainingDraftDao.deleteTask(taskId)
        trainingDraftDao.updateTimestamp(trainingId)
    }

    // Obtener borrador completo
    override suspend fun getDraft(id: String): Training? {
        return trainingDraftDao.getTrainingWithTasks(id)?.toDomain()
    }

    // Observar borrador
    override fun observeDraft(id: String): Flow<Training?> {
        return trainingDraftDao.getTasksForTrainingFlow(id).map { tasks ->
            val trainingEntity = trainingDraftDao.getTrainingDraftById(id) ?: return@map null
            trainingEntity.toDomain(tasks)
        }
    }

    // Finalizar borrador (marcarlo como completado)
    override suspend fun finalizeDraft(id: String) {
        val current = trainingDraftDao.getTrainingDraftById(id) ?: return
        trainingDraftDao.updateTrainingDraft(
            current.copy(
                isCompleted = true,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    // Eliminar borrador
    override suspend fun deleteDraft(id: String) {
        trainingDraftDao.deleteTrainingDraft(id)
    }

    // Obtener todos los borradores
    override fun getAllDrafts(): Flow<List<Training>> {
        return trainingDraftDao.getAllDraftsWithTasks().map { list ->
            list.map { it.toDomain() }
        }
    }

    // Obtener todos los entrenamientos finalizados
    override fun getAllCompletedTrainings(): Flow<List<Training>> {
        return trainingDraftDao.getAllCompletedTrainings().map { trainings ->
            trainings.map { training ->
                val tasks = trainingDraftDao.getTasksForTraining(training.id)
                training.toDomain(tasks)
            }
        }
    }

    // Limpiar borradores antiguos (ej: más de 7 días)
    override suspend fun cleanOldDrafts(daysOld: Int) {
        val cutoffTime = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L)
        trainingDraftDao.deleteOldDrafts(cutoffTime)
    }
}