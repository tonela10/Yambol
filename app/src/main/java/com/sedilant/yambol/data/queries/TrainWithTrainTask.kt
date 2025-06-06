package com.sedilant.yambol.data.queries

import androidx.room.Embedded
import androidx.room.Relation
import com.sedilant.yambol.data.entities.TrainEntity
import com.sedilant.yambol.data.entities.TrainTaskEntity

data class TrainWithTrainTask(
    @Embedded val train: TrainEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "trainTaskId"
    )
    val tasks: List<TrainTaskEntity>
)

data class TrainTaskWithTrain(
    @Embedded val trainTask: TrainTaskEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "trainId"
    )
    val trains: List<TrainEntity>
)
