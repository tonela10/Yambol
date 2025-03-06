package com.sedilant.yambol.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TeamRepositoryImpl @Inject constructor(
    private val playerDao: PlayerDao
) : TeamRepository {
    override suspend fun getAllTeams(): Flow<List<TeamEntity>> {
        return playerDao.getAllTeams()
    }

    override suspend fun getTeamPlayers(id: Int): Flow<List<PlayerEntity>> {
        return playerDao.getTeamPlayers(id)
    }
}
