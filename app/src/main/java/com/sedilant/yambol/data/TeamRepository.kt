package com.sedilant.yambol.data

import com.sedilant.yambol.data.entities.PlayerEntity
import com.sedilant.yambol.data.entities.TeamEntity
import com.sedilant.yambol.data.entities.TeamObjectivesEntity
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import kotlinx.coroutines.flow.Flow

interface TeamRepository {

    // team information related methods
    suspend fun getAllTeams(): Flow<List<TeamEntity>>
    suspend fun getTeamPlayers(id: Int): Flow<List<PlayerEntity>>
    suspend fun getTeamId(teamName: String): Int?
    suspend fun insertTeam(teamEntity: TeamEntity)
    suspend fun insertPlayer(playerEntity: PlayerEntity)

    // team objectives related methods
    suspend fun getTeamObjectives(teamId: Int): Flow<List<TeamObjectivesEntity>>
    suspend fun insertTeamObjective(teamObjectivesEntity: TeamObjectivesEntity)
    suspend fun updateTeamObjective(teamObjectivesEntity: TeamObjectivesEntity)
    suspend fun getTeamObjectiveById(objectiveId: Int): TeamObjectivesEntity?
    suspend fun deleteTeamObjective(teamObjectivesEntity: TeamObjectivesEntity)

    // player information related methods
    suspend fun getPlayer(id: Int): PlayerUiModel // TODO create a PlayerDomainModel
}
