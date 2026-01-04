package com.sedilant.yambol.domain.models

data class TaskDomain(
    val trainingTaskId: Long,
    val name: String,
    val concepts: List<Long>,
    val description: String,
    val variables: List<String>?
)
