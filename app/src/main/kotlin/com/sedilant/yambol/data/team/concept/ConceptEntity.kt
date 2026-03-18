package com.sedilant.yambol.data.team.concept

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "concepts",
)
data class ConceptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
)
