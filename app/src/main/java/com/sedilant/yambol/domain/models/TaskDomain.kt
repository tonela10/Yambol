package com.sedilant.yambol.domain.models

data class TaskDomain(
    val trainingTaskId: String,
    val name: String,
    val concepts: List<String>,
    val description: String,
    val variables: List<String>
)
