package com.sedilant.yambol.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ability_record",
    foreignKeys = [
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["player_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AbilityNameEntity::class,
            parentColumns = ["id"],
            childColumns = ["ability_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("player_id"), Index("ability_id")]
)
data class AbilityRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "player_id")
    val playerId: Int,

    @ColumnInfo(name = "ability_id")
    val abilityId: Int,

    val value: Int,

    val timestamp: Long = System.currentTimeMillis()
)


@Entity(tableName = "ability_name")
data class AbilityNameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String // e.g., "shooting", "defense", etc.
)
