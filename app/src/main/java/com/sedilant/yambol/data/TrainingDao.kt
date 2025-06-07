package com.sedilant.yambol.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.sedilant.yambol.data.entities.TrainCrossTrainTaskEntity
import com.sedilant.yambol.data.entities.TrainEntity
import com.sedilant.yambol.data.entities.TrainTaskEntity
import com.sedilant.yambol.data.queries.TrainWithTrainTask

@Dao
interface TrainingDao {

    @Query("SELECT * FROM train WHERE teamId = :teamId")
    fun getAllTrainsByTeamId(teamId: Int): List<TrainEntity>

    @Transaction
    @Query("SELECT * FROM train WHERE trainId = :trainId")
    fun getTrainWithTrainTaskByTrainId(trainId: Int): TrainWithTrainTask

    @Insert
    fun insertTrain(trainEntity: TrainEntity): Long

    @Insert
    fun insertTrainTask(trainTaskEntity: TrainTaskEntity): Long

    @Insert
    fun insertTrainCrossTrainTask(trainCrossTrainTaskEntity: TrainCrossTrainTaskEntity)
}
