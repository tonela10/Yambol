package com.sedilant.yambol.data.draftTrain

import androidx.room.Embedded
import androidx.room.Relation

data class TrainingWithTasks(
    @Embedded val training: TrainingDraftEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "trainingId"
    )
    val tasks: List<TrainingTaskEntity>
)
