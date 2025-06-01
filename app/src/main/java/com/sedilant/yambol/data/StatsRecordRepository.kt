package com.sedilant.yambol.data

import com.sedilant.yambol.data.queries.AbilityRecordWithName
import kotlinx.coroutines.flow.Flow

interface StatsRecordRepository {

    // CRUD one player stats functions
    suspend fun getPlayerStats(playerId: Int): Flow<List<AbilityRecordWithName>>
}
