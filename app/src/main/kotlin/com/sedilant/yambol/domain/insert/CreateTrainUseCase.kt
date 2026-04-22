package com.sedilant.yambol.domain.insert

import kotlinx.datetime.Instant

interface CreateTrainUseCase {
    suspend operator fun invoke(date: Instant, startTime: Float, endTime: Float, concepts: List<String>, teamId: String): String
}
