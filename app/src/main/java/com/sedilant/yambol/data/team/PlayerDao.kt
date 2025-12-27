package com.sedilant.yambol.data.team

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)

    // READ
    @Query("SELECT * FROM player WHERE id = :id")
    suspend fun getPlayerById(id: Long): PlayerEntity?

    @Query("SELECT * FROM player WHERE team_id = :teamId ORDER BY number ASC")
    fun getPlayersByTeam(teamId: Int): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM player ORDER BY name ASC")
    fun getAllPlayers(): Flow<List<PlayerEntity>>

    // UPDATE
    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    // DELETE
    @Delete
    suspend fun deletePlayer(player: PlayerEntity)

    @Query("DELETE FROM player WHERE team_id = :teamId")
    suspend fun deleteAllPlayersFromTeam(teamId: Int)

    // new block of player dao CRUD up TODO move create new teamDao to get the Team information

    @Query("SELECT * FROM team")
    fun getAllTeams(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM player WHERE team_id = :teamId")
    fun getTeamPlayers(teamId: Int): Flow<List<PlayerEntity>>

    @Query("SELECT id FROM team WHERE name = :teamName")
    fun getTeamId(teamName: String): Int?

    @Insert
    fun insertTeam(teamEntity: TeamEntity)

    @Query("SELECT * FROM player WHERE id = :playerId")
    fun getPlayer(playerId: Int): PlayerEntity

    @Query("UPDATE team SET name = :newName WHERE id = :teamId")
    suspend fun updateTeam(teamId: Int, newName: String)

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