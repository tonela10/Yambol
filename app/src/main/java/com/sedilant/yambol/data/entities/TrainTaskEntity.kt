package com.sedilant.yambol.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "train_task",
)
data class TrainTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val trainingTaskId: Int = 0,
    val name: String,
    val numberOfPlayers: Int,
    val concept: String,
    val description: String,
    val variables: List<String>?,
)
