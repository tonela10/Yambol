package com.sedilant.yambol.data.team

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.sedilant.yambol.data.queries.TrainWithTrainTask

@Dao
interface TrainingDao {

    @Query("SELECT * FROM train WHERE teamId = :teamId")
    suspend fun getAllTrainsByTeamId(teamId: Long): List<TrainEntity>

    @Transaction
    @Query("SELECT * FROM train WHERE id = :trainId")
    suspend fun getTrainWithTrainTaskByTrainId(trainId: Long): TrainWithTrainTask

    @Query("SELECT id FROM train WHERE teamId = :teamId ORDER BY id DESC LIMIT 1")
    suspend fun getLastTrainWithTrainTaskByTeamId(teamId: Long): Long

    @Insert
    suspend fun insertTrain(trainEntity: TrainEntity): Long

    @Insert
    suspend fun insertTrainTask(taskEntity: TaskEntity): Long

    @Insert
    suspend fun insertTrainCrossTrainTask(trainCrossTrainTaskEntity: TrainCrossTrainTaskEntity)
}
