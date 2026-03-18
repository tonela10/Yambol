package com.sedilant.yambol.data.team

import androidx.room.Entity
import androidx.room.Index

@Entity(
    primaryKeys = ["trainId", "taskId"],
    indices = [Index(value = ["trainId"])]
)
data class TrainCrossTrainTaskEntity(
    val trainId: Long,
    val taskId: Long
)
