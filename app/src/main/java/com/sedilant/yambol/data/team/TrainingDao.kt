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
    @Query("SELECT * FROM train WHERE trainId = :trainId")
    fun getTrainWithTrainTaskByTrainId(trainId: Int): TrainWithTrainTask

    @Query("SELECT trainId FROM train WHERE teamId = :teamId ORDER BY trainId DESC LIMIT 1")
    fun getLastTrainWithTrainTaskByTeamId(teamId: Int): Long

    @Insert
    fun insertTrain(trainEntity: TrainEntity): Long

    @Insert
    fun insertTrainTask(trainTaskEntity: TrainTaskEntity): Long

    @Insert
    fun insertTrainCrossTrainTask(trainCrossTrainTaskEntity: TrainCrossTrainTaskEntity)
}
