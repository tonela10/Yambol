package com.sedilant.yambol.data.queries

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sedilant.yambol.data.team.TrainCrossTrainTaskEntity
import com.sedilant.yambol.data.team.TrainEntity
import com.sedilant.yambol.data.team.TaskEntity

data class TrainWithTrainTask(
    @Embedded val train: TrainEntity,
    @Relation(
        parentColumn = "trainId",
        entityColumn = "trainingTaskId",
        associateBy = Junction(
            TrainCrossTrainTaskEntity::class,
            parentColumn = "trainId",
            entityColumn = "trainingTaskId"
        )
    )
    val tasks: List<TaskEntity>
)

data class TrainTaskWithTrain(
    @Embedded val trainTask: TaskEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "trainId"
    )
    val trains: List<TrainEntity>
)
