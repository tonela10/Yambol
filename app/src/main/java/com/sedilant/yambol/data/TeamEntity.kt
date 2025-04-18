package com.sedilant.yambol.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "team")
data class TeamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
