package com.sedilant.yambol.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sedilant.yambol.data.entities.TeamObjectivesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamObjectivesDao {

    @Insert
    fun insertTeamObjective(teamObjectivesEntity: TeamObjectivesEntity)

    @Query("SELECT * FROM team_objectives WHERE team_id = :teamId")
    fun getTeamObjectives(teamId: Int): Flow<List<TeamObjectivesEntity>>

    @Update
    fun updateTeamObjective(teamObjectivesEntity: TeamObjectivesEntity)

    @Query("SELECT * FROM team_objectives WHERE id = :objectiveId")
    suspend fun getTeamObjectiveById(objectiveId: Int): TeamObjectivesEntity?
}
