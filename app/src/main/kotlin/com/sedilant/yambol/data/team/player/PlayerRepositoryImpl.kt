package com.sedilant.yambol.data.team.player

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val playerDao: PlayerDao,
) : PlayerRepository {
    override suspend fun insertPlayer(player: PlayerEntity): Long {
        return playerDao.insertPlayer(player)
    }

    override suspend fun insertPlayers(players: List<PlayerEntity>) {
        playerDao.insertPlayers(players)
    }

    override suspend fun getPlayerById(id: Long): PlayerEntity? {
        return playerDao.getPlayerById(id)
    }

    override suspend fun getPlayersByTeam(teamId: Long): Flow<List<PlayerEntity>> {
        return playerDao.getPlayersByTeam(teamId)
    }

    override suspend fun getAllPlayers(): Flow<List<PlayerEntity>> {
        return playerDao.getAllPlayers()
    }

    override suspend fun updatePlayer(player: PlayerEntity) {
        playerDao.updatePlayer(player)
    }

    override suspend fun deletePlayer(player: PlayerEntity) {
        playerDao.deletePlayer(player)
    }

    override suspend fun deleteAllPlayersFromTeam(teamId: Long) {
        playerDao.deleteAllPlayersFromTeam(teamId)
    }

    override suspend fun isJerseyNumberTaken(
        teamId: Long,
        jerseyNumber: Int,
        excludePlayerId: Long?
    ): Boolean {
        return playerDao.isJerseyNumberTaken(teamId, jerseyNumber, excludePlayerId)
    }
}