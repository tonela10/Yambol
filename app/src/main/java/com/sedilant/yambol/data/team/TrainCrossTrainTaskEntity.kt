package com.sedilant.yambol.data.team

import androidx.room.Entity

@Entity(primaryKeys = ["trainId", "trainingTaskId"])
data class TrainCrossTrainTaskEntity(
    val trainId: Int,
    val trainingTaskId: Int
)
