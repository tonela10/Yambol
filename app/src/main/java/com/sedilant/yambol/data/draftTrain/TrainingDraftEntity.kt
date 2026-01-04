package com.sedilant.yambol.data.draftTrain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "training_drafts")
data class TrainingDraftEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val date: Long,
    val endTime: Float,
    val startTime: Float,
    val concepts: List<Long>,
    val isCompleted: Boolean = false, // false = draft, true = finalize
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "team_id")
    val teamId: Long
)