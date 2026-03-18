package com.sedilant.yambol.domain.models

import java.util.Date

data class TrainWithTaskDomainModel(
    val trainId: String,
    val date: Date,
    val time: Float,
    val conceptIds: List<String>,
    val teamId: String,
    val trains: List<TaskDomain>
)
