package com.sedilant.yambol.domain.models

import java.util.Date

data class TrainDomainModel(
    val id: Long,
    val date: Date,
    val time: Float,
    val concepts: List<String>,
    val teamId: Int
)
