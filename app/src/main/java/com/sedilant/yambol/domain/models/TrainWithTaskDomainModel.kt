package com.sedilant.yambol.domain.models

import java.util.Date

data class TrainWithTaskDomainModel(
    val trainId: Int,
    val date: Date,
    val time: Float,
    val concepts: List<String>,
    val teamId: Int,
    val trains: List<TrainTaskDomainModel>
)
