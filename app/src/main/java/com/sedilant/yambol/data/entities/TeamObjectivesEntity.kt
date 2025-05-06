package com.sedilant.yambol.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "team_objectives",
    foreignKeys = [
        ForeignKey(
            entity = TeamEntity::class,
            parentColumns = ["id"],
            childColumns = ["team_id"],
            onDelete = ForeignKey.CASCADE // O RESTRICT it depends
        )
    ],
    indices = [Index("team_id")])
data class TeamObjectivesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String,
    val isFinish: Boolean,
    @ColumnInfo(name = "team_id")
    val teamId: Int,
)
