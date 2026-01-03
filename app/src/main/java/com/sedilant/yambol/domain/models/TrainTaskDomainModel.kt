package com.sedilant.yambol.domain.models

data class TrainTaskDomainModel(
    val trainingTaskId: Long,
    val name: String,
    val concepts: List<Long>,
    val description: String,
    val variables: List<String>?
)
