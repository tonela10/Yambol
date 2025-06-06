package com.sedilant.yambol.data.entities

import androidx.room.Entity

@Entity(primaryKeys = ["trainId", "trainTaskId"])
data class TrainCrossTrainTaskEntity(
    val trainId: Int,
    val trainTaskId: Int
)
