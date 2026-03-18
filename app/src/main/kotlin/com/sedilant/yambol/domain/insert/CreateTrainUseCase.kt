package com.sedilant.yambol.domain.insert

import java.util.Date

interface CreateTrainUseCase {
    suspend operator fun invoke(date: Date, startTime: Float, endTime: Float, concepts: List<String>, teamId: String): String
}
