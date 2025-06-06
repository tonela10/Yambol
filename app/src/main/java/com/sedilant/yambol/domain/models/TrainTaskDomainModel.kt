package com.sedilant.yambol.domain.models

data class TrainTaskDomainModel(
    val trainingTaskId: Int,
    val name: String,
    val numberOfPlayer: Int,
    val concept: String,
    val description: String,
    val variables: List<String>?
)
