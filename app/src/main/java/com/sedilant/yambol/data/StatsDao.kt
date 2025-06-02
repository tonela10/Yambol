package com.sedilant.yambol.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.sedilant.yambol.data.entities.AbilityNameEntity
import com.sedilant.yambol.data.entities.AbilityRecordEntity
import com.sedilant.yambol.data.queries.AbilityRecordWithName
import kotlinx.coroutines.flow.Flow

@Dao
interface StatsDao {
    @Transaction
    @Query("SELECT * FROM ability_record WHERE player_id = :playerId ORDER BY timestamp ASC")
    fun getAbilityRecordsWithName(playerId: Int): Flow<List<AbilityRecordWithName>>

    @Query("SELECT * FROM ability_name WHERE id = :statId")
    fun getStatById(statId: Int): AbilityNameEntity

    @Query("SELECT * FROM ability_name WHERE name = :statName")
    fun getStatByName(statName: String): AbilityNameEntity

    @Insert
    fun insertStatRecord(abilityRecordEntity: AbilityRecordEntity)
}
