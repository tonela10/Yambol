package com.sedilant.yambol.domain.insert

interface SavePlayerStatUseCase {
    suspend operator fun invoke(playerId:Int, statId: Int, rating: Int)
}