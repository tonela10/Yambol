package com.sedilant.yambol.domain.insert

import java.util.Date

interface CreateTrainUseCase {
    suspend operator fun invoke(date: Date, time: Float, concepts: List<String>, teamId: Int) : Int
}
