package com.sedilant.yambol.domain

import com.sedilant.yambol.data.StatsRecordRepository
import com.sedilant.yambol.ui.playerCard.StatUiModel
import javax.inject.Inject

class GetStatByIdUseCaseImpl @Inject constructor(
    private val statsRecordRepository: StatsRecordRepository
) : GetStatByIdUseCase {
    override suspend fun invoke(statId: Int): StatUiModel {
        val stat = statsRecordRepository.getStatById(statId)
        return StatUiModel(
            id = stat.id,
            name = stat.name,
            value = 0f
        )
    }

}