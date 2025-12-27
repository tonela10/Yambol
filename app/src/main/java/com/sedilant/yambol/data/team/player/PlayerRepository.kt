package com.sedilant.yambol.data.team.player

import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    // Player CRUD Methods
    suspend fun insertPlayer(player: PlayerEntity): Long
    suspend fun insertPlayers(players: List<PlayerEntity>)
    suspend fun getPlayerById(id: Long): PlayerEntity?
    suspend fun getPlayersByTeam(teamId: Long): Flow<List<PlayerEntity>>
    suspend fun getAllPlayers(): Flow<List<PlayerEntity>>
    suspend fun updatePlayer(player: PlayerEntity)
    suspend fun deletePlayer(player: PlayerEntity)
    suspend fun deleteAllPlayersFromTeam(teamId: Long)
    suspend fun isJerseyNumberTaken(teamId: Long, jerseyNumber: Int, excludePlayerId: Long?): Boolean

}