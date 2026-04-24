package com.sedilant.yambol.domain.models

import kotlinx.datetime.Instant

data class TrainDomainModel(
    val id: String,
    val date: Instant,
    val time: Float,
    val concepts: List<String>,
    val teamId: String
)
