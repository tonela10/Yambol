package com.sedilant.yambol.data.draftTrain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "training_drafts")
data class TrainingDraftEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val date: Long, // Timestamp en milisegundos
    val duration: Float,
    val hour: Float,
    val concepts: String, // Lista serializada como "concepto1,concepto2,concepto3"
    val isCompleted: Boolean = false, // false = borrador, true = finalizado
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)