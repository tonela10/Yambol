package com.sedilant.yambol.feature.createTrain

import kotlinx.serialization.Serializable

@Serializable
data class TaskData(
    val name: String,
    val numberOfPlayer: Int,
    val concept: String,
    val description: String,
    val variables: List<String>
)
