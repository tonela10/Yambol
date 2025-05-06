package com.sedilant.yambol.data

import com.sedilant.yambol.data.entities.PlayerEntity
import com.sedilant.yambol.data.entities.TeamEntity
import com.sedilant.yambol.data.entities.TeamObjectivesEntity
import kotlinx.coroutines.flow.Flow

interface TeamRepository {

    suspend fun getAllTeams(): Flow<List<TeamEntity>>
    suspend fun getTeamPlayers(id: Int): Flow<List<PlayerEntity>>
    suspend fun getTeamId(teamName: String): Int?
    suspend fun insertTeam(teamEntity: TeamEntity)
    suspend fun insertPlayer(playerEntity: PlayerEntity)

    suspend fun getTeamObjectives(teamId: Int): Flow<List<TeamObjectivesEntity>>
    suspend fun insertTeamObjective(teamObjectivesEntity: TeamObjectivesEntity)
    suspend fun updateTeamObjective(teamObjectivesEntity: TeamObjectivesEntity)
    suspend fun getTeamObjectiveById(objectiveId: Int): TeamObjectivesEntity?
    suspend fun deleteTeamObjective(teamObjectivesEntity: TeamObjectivesEntity)
}
