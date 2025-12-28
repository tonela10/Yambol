package com.sedilant.yambol.data.team

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamObjectivesDao {

    @Insert
    fun insertTeamObjective(teamObjectivesEntity: TeamObjectivesEntity)

    @Query("SELECT * FROM team_objectives WHERE team_id = :teamId")
    fun getTeamObjectives(teamId: Long): Flow<List<TeamObjectivesEntity>>

    @Update
    suspend fun updateTeamObjective(teamObjectivesEntity: TeamObjectivesEntity)

    @Query("SELECT * FROM team_objectives WHERE id = :objectiveId")
    suspend fun getTeamObjectiveById(objectiveId: Int): TeamObjectivesEntity?

    @Delete
    suspend fun deleteTeamObjective(teamObjectivesEntity: TeamObjectivesEntity)
}
