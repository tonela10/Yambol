package com.sedilant.yambol.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    @Query("SELECT * FROM team")
    fun getAllTeams(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM player WHERE team_id = :teamId")
    fun getTeamPlayers(teamId: Int): Flow<List<PlayerEntity>>
}
