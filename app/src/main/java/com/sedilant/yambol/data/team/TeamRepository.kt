package com.sedilant.yambol.data.team

import com.sedilant.yambol.data.queries.TrainWithTrainTask
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import kotlinx.coroutines.flow.Flow

interface TeamRepository {

    // team information related methods
    suspend fun getAllTeams(): Flow<List<TeamEntity>>
    suspend fun getTeamId(teamName: String): Int?
    suspend fun insertTeam(teamEntity: TeamEntity)
    suspend fun updateTeam(teamId: Int, newName: String)

    // team objectives related methods
    suspend fun getTeamObjectives(teamId: Int): Flow<List<TeamObjectivesEntity>>
    suspend fun insertTeamObjective(teamObjectivesEntity: TeamObjectivesEntity)
    suspend fun updateTeamObjective(teamObjectivesEntity: TeamObjectivesEntity)
    suspend fun getTeamObjectiveById(objectiveId: Int): TeamObjectivesEntity?
    suspend fun deleteTeamObjective(teamObjectivesEntity: TeamObjectivesEntity)

    // team trainings related info
    suspend fun getAllTrainingsByTeamId(teamId: Int): List<TrainEntity>
    suspend fun getTrainWithTrainTaskByTrainId(trainId: Int): TrainWithTrainTask
    suspend fun insertTrain(trainEntity: TrainEntity): Int
    suspend fun insertTrainTask(trainTaskEntity: TaskEntity): Int
    suspend fun insertTrainCrossTrainTask(trainCrossTrainTaskEntity: TrainCrossTrainTaskEntity)
    suspend fun getLastTrainWithTrainTaskByTeamId(teamId: Int): Long?
}
