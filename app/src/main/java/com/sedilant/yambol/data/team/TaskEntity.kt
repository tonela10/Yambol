package com.sedilant.yambol.data.team

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "train_task",
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val concept: String, // TODO check if list is needed
    val description: String,
    val corrections: String?,
    val variables: List<String>?,
)
