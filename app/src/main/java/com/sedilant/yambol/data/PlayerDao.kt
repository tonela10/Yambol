package com.sedilant.yambol.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    @Query("SELECT * FROM team")
    fun getAllTeams(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM player WHERE team_id = :teamId")
    fun getTeamPlayers(teamId: Int): Flow<List<PlayerEntity>>

    @Query("SELECT id FROM team WHERE name = :teamName")
    fun getTeamId(teamName: String): Int?

    @Insert
    fun insertTeam(teamEntity: TeamEntity)

    @Insert
    fun insertPlayer(playerEntity: PlayerEntity)
}
