package com.sedilant.yambol.domain.get

import com.sedilant.yambol.domain.models.TrainWithTaskDomainModel

interface GetTrainWithTrainTaskByTrainIdUseCase {
    suspend operator fun invoke(trainId: Int): TrainWithTaskDomainModel
}
