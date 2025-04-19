package com.sedilant.yambol.data

import com.sedilant.yambol.data.entities.PlayerEntity
import com.sedilant.yambol.data.entities.TeamEntity
import com.sedilant.yambol.data.entities.TeamObjectivesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TeamRepositoryImpl @Inject constructor(
    private val playerDao: PlayerDao,
    private val teamObjectivesDao: TeamObjectivesDao,
) : TeamRepository {
    // PLAYER DAO METHODS
    override suspend fun getAllTeams(): Flow<List<TeamEntity>> {
        return playerDao.getAllTeams()
    }

    override suspend fun getTeamPlayers(id: Int): Flow<List<PlayerEntity>> {
        return playerDao.getTeamPlayers(id)
    }

    override suspend fun getTeamId(teamName: String): Int? {
        return playerDao.getTeamId(teamName)
    }

    override suspend fun insertTeam(teamEntity: TeamEntity) {
        playerDao.insertTeam(teamEntity)
    }

    override suspend fun insertPlayer(playerEntity: PlayerEntity) {
        playerDao.insertPlayer(playerEntity)
    }

    // TEAM OBJECTIVES DAO METHODS
    override suspend fun insertTeamObjective(teamObjectivesEntity: TeamObjectivesEntity) {
        teamObjectivesDao.insertTeamObjective(teamObjectivesEntity)
    }

    override suspend fun getTeamObjectives(teamId: Int): Flow<List<TeamObjectivesEntity>> {
        return teamObjectivesDao.getTeamObjectives(teamId)
    }

}
