package com.sedilant.yambol.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val number: Int,
    val position: Int,
    @ColumnInfo(name = "team_id")
    val teamId: Int,
)

// TODO move this to domain
enum class Position(name: String, number: Int) {
    POINT_GUARD(name = "point guard", number = 1),
    SHOOTING_GUARD(name = "shooting guard", number = 2),
    SMALL_FORWARD(name = "small forward", number = 3),
    POWER_FORWARD(name = "power forward", number = 4),
    CENTER(name = "center", number = 5)
}
