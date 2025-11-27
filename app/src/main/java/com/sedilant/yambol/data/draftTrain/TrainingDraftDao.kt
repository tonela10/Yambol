package com.sedilant.yambol.data.draftTrain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDraftDao {

    // ---------- TRAINING DRAFTS ----------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingDraft(training: TrainingDraftEntity)

    @Update
    suspend fun updateTrainingDraft(training: TrainingDraftEntity)

    @Query("UPDATE training_drafts SET updatedAt = :timestamp WHERE id = :id")
    suspend fun updateTimestamp(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM training_drafts WHERE id = :id")
    suspend fun getTrainingDraftById(id: String): TrainingDraftEntity?

    @Query("SELECT * FROM training_drafts WHERE isCompleted = 0 ORDER BY updatedAt DESC")
    fun getAllDrafts(): Flow<List<TrainingDraftEntity>>

    @Query("SELECT * FROM training_drafts WHERE isCompleted = 1 ORDER BY date DESC")
    fun getAllCompletedTrainings(): Flow<List<TrainingDraftEntity>>

    @Query("DELETE FROM training_drafts WHERE id = :id")
    suspend fun deleteTrainingDraft(id: String)

    @Query("DELETE FROM training_drafts WHERE isCompleted = 0 AND updatedAt < :timestamp")
    suspend fun deleteOldDrafts(timestamp: Long)

    // ---------- TASKS ----------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TrainingTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TrainingTaskEntity>)

    @Update
    suspend fun updateTask(task: TrainingTaskEntity)

    @Query("SELECT * FROM training_tasks WHERE trainingId = :trainingId ORDER BY orderIndex ASC")
    suspend fun getTasksForTraining(trainingId: String): List<TrainingTaskEntity>

    @Query("SELECT * FROM training_tasks WHERE trainingId = :trainingId ORDER BY orderIndex ASC")
    fun getTasksForTrainingFlow(trainingId: String): Flow<List<TrainingTaskEntity>>

    @Query("DELETE FROM training_tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)

    @Query("DELETE FROM training_tasks WHERE trainingId = :trainingId")
    suspend fun deleteAllTasksForTraining(trainingId: String)

    // ---------- TRANSACCIONES COMBINADAS ----------

    @Transaction
    @Query("SELECT * FROM training_drafts WHERE id = :id")
    suspend fun getTrainingWithTasks(id: String): TrainingWithTasks?

    @Transaction
    @Query("SELECT * FROM training_drafts WHERE isCompleted = 0 ORDER BY updatedAt DESC")
    fun getAllDraftsWithTasks(): Flow<List<TrainingWithTasks>>
}