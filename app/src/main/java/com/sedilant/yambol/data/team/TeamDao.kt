package com.sedilant.yambol.data.team

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Query("SELECT * FROM team")
    fun getAllTeams(): Flow<List<TeamEntity>>

    // TODO should not use this method
    @Query("SELECT id FROM team WHERE name = :teamName")
    fun getTeamId(teamName: String): Int?

    @Update
    suspend fun updateTeam(teamEntity: TeamEntity)

    @Insert
    fun insertTeam(teamEntity: TeamEntity): Long
}