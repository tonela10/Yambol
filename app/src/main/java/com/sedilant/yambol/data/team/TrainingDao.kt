package com.sedilant.yambol.data.team

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.sedilant.yambol.data.queries.TrainWithTrainTask

@Dao
interface TrainingDao {

    @Query("SELECT * FROM train WHERE teamId = :teamId")
    fun getAllTrainsByTeamId(teamId: Int): List<TrainEntity>

    @Transaction
    @Query("SELECT * FROM train WHERE id = :trainId")
    fun getTrainWithTrainTaskByTrainId(trainId: Int): TrainWithTrainTask

    @Query("SELECT id FROM train WHERE teamId = :teamId ORDER BY id DESC LIMIT 1")
    fun getLastTrainWithTrainTaskByTeamId(teamId: Int): Long

    @Insert
    fun insertTrain(trainEntity: TrainEntity): Long

    @Insert
    fun insertTrainTask(taskEntity: TaskEntity): Long

    @Insert
    fun insertTrainCrossTrainTask(trainCrossTrainTaskEntity: TrainCrossTrainTaskEntity)
}
