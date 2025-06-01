package com.sedilant.yambol.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.sedilant.yambol.data.queries.AbilityRecordWithName
import kotlinx.coroutines.flow.Flow

@Dao
interface StatsDao {
    @Transaction
    @Query("SELECT * FROM ability_record WHERE player_id = :playerId ORDER BY timestamp ASC")
    fun getAbilityRecordsWithName(playerId: Int): Flow<List<AbilityRecordWithName>>
}
