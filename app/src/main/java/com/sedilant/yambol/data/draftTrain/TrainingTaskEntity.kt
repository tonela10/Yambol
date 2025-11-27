package com.sedilant.yambol.data.draftTrain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "training_tasks",
    foreignKeys = [
        ForeignKey(
            entity = TrainingDraftEntity::class,
            parentColumns = ["id"],
            childColumns = ["trainingId"],
            onDelete = ForeignKey.CASCADE // Si se borra el training, se borran sus tasks
        )
    ],
    indices = [Index(value = ["trainingId"])] // Índice para mejorar queries
)
data class TrainingTaskEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val trainingId: String, // FK hacia training_drafts
    val name: String,
    val concepts: String, // Lista serializada como "concepto1,concepto2,concepto3"
    val description: String,
    val variation: String, // Asumo que "var" es "variation"
    val orderIndex: Int = 0 // Para mantener el orden de las tareas
)
