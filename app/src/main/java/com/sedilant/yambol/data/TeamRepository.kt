package com.sedilant.yambol.data

import kotlinx.coroutines.flow.Flow

interface TeamRepository {
    suspend fun getAllTeams(): Flow<List<TeamEntity>>

    suspend fun getTeamPlayers(id: Int): Flow<List<PlayerEntity>>
}
