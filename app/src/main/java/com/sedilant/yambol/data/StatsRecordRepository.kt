package com.sedilant.yambol.data

import com.sedilant.yambol.data.entities.AbilityNameEntity
import com.sedilant.yambol.data.entities.AbilityRecordEntity
import com.sedilant.yambol.data.queries.AbilityRecordWithName
import kotlinx.coroutines.flow.Flow

interface StatsRecordRepository {

    // CRUD one player stats functions
    suspend fun getPlayerStats(playerId: Int): Flow<List<AbilityRecordWithName>>

    suspend fun getStatById(statId: Int): AbilityNameEntity
    suspend fun getStatByName(statName:String): AbilityNameEntity

    suspend fun insertPlayerStatUseCase(abilityRecordEntity: AbilityRecordEntity)

}
