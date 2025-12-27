package com.sedilant.yambol.data.team.player

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sedilant.yambol.data.team.TeamEntity

@Entity(
    tableName = "player",
    foreignKeys = [
        ForeignKey(
            entity = TeamEntity::class,
            parentColumns = ["id"],
            childColumns = ["team_id"],
            onDelete = ForeignKey.CASCADE // O RESTRICT it depends
        )
    ],
    indices = [Index("team_id")]
)
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val number: Int,
    @ColumnInfo(name = "team_id")
    val teamId: Long,
)
