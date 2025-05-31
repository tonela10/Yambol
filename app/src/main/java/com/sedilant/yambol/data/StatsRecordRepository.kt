package com.sedilant.yambol.data

import com.sedilant.yambol.data.queries.AbilityRecordWithName

interface StatsRecordRepository {

    // CRUD one player stats functions
    suspend fun getPlayerStats(playerId: Int): List<AbilityRecordWithName>
}
