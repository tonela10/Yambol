package com.sedilant.yambol.domain.models

import kotlinx.datetime.Instant

data class TrainWithTaskDomainModel(
    val trainId: String,
    val date: Instant,
    val time: Float,
    val conceptIds: List<String>,
    val teamId: String,
    val trains: List<TaskDomain>
)
