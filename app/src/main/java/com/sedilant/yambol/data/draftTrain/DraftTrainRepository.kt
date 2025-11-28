package com.sedilant.yambol.data.draftTrain

import kotlinx.coroutines.flow.Flow
import java.util.Date

// TODO change to English
interface TrainingDraftRepository {

    // Crear nuevo borrador
    suspend fun createDraft(training: Training): String

    // Obtener o crear el borrador activo (solo puede haber uno)
    suspend fun getOrCreateActiveDraft(): String

    // Actualizar datos del training (sin tasks)
    suspend fun updateTrainingData(
        id: String,
        date: Date? = null,
        duration: Float? = null,
        hour: Float? = null,
        concepts: List<String>? = null
    )

    // Añadir una tarea
    suspend fun addTask(trainingId: String, task: Task)

    // Actualizar una tarea
    suspend fun updateTask(trainingId: String, task: Task)

    // Eliminar una tarea
    suspend fun removeTask(trainingId: String, taskId: String)

    // Obtener borrador completo
    suspend fun getDraft(id: String): Training?

    // Observar borrador
    fun observeDraft(id: String): Flow<Training?>

    // Finalizar borrador (marcarlo como completado)
    suspend fun finalizeDraft(id: String)

    // Eliminar borrador
    suspend fun deleteDraft(id: String)

    // Obtener todos los borradores
    fun getAllDrafts(): Flow<List<Training>>

    // Obtener todos los entrenamientos finalizados
    fun getAllCompletedTrainings(): Flow<List<Training>>

    // Limpiar borradores antiguos
    suspend fun cleanOldDrafts(daysOld: Int = 7)
}