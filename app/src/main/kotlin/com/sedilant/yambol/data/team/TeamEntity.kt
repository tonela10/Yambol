package com.sedilant.yambol.data.team

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "team")
data class TeamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
)
