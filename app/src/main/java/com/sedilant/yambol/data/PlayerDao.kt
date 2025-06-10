package com.sedilant.yambol.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.sedilant.yambol.data.entities.PlayerEntity
import com.sedilant.yambol.data.entities.TeamEntity
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

    @Query("SELECT * FROM player WHERE id = :playerId")
    fun getPlayer(playerId: Int): PlayerEntity

    @Query("UPDATE team SET name = :newName WHERE id = :teamId")
    suspend fun updateTeam(teamId: Int, newName: String)

    @Query("UPDATE player SET name = :newName, number = :newNumber, position = :newPosition WHERE id = :playerId")
    suspend fun updatePlayer(playerId: Int, newName: String, newNumber: Int, newPosition: Int)

    @Query("SELECT team_id FROM player WHERE id = :playerId")
    suspend fun getPlayerTeamId(playerId: Int): Int

    @Query(
        """
    SELECT COUNT(*) > 0 FROM player 
    WHERE team_id = :teamId 
    AND number = :jerseyNumber 
    AND (:excludePlayerId IS NULL OR id != :excludePlayerId)
"""
    )
    suspend fun isJerseyNumberTaken(teamId: Int, jerseyNumber: Int, excludePlayerId: Int?): Boolean

    @Query("DELETE FROM player WHERE id = :playerId")
    suspend fun deletePlayerById(playerId: Int)
}
