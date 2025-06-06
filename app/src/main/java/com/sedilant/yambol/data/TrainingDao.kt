package com.sedilant.yambol.data

import androidx.room.Dao
import androidx.room.Query
import com.sedilant.yambol.data.entities.TrainEntity

@Dao
interface TrainingDao {

    @Query("SELECT * FROM train WHERE teamId = :teamId")
    fun getAllTrainsByTeamId(teamId: Int): List<TrainEntity>


//    @Insert
//    fun insertTrain(trainWithTrainTask: TrainWithTrainTask)
}
