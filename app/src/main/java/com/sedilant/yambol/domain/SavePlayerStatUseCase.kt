package com.sedilant.yambol.domain

interface SavePlayerStatUseCase {
    suspend operator fun invoke(playerId:Int, statId: Int, rating: Int)
}