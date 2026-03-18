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
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            TrainCrossTrainTaskEntity::class,
            parentColumn = "trainId",
            entityColumn = "taskId"
        )
    )
    val tasks: List<TaskEntity>
)