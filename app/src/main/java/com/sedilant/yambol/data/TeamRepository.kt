package com.sedilant.yambol.data

import kotlinx.coroutines.flow.Flow

interface TeamRepository {
    suspend fun getAllTeams(): Flow<List<TeamEntity>>
    suspend fun getTeamPlayers(id: Int): Flow<List<PlayerEntity>>

    suspend fun getTeamId(teamName: String): Int

    suspend fun insertTeam(teamEntity: TeamEntity)
    suspend fun insertPlayer(playerEntity: PlayerEntity)
}
