package com.sedilant.yambol.data.team

import com.sedilant.yambol.data.queries.TrainWithTrainTask
import kotlinx.coroutines.flow.Flow

interface TeamRepository {

    // team information related methods
    suspend fun getAllTeams(): Flow<List<TeamEntity>>
    suspend fun insertTeam(teamEntity: TeamEntity): Long
    suspend fun updateTeam(teamId: Long, newName: String)

    // team objectives related methods
    suspend fun getTeamObjectives(teamId: Long): Flow<List<TeamObjectivesEntity>>
    suspend fun insertTeamObjective(teamObjectivesEntity: TeamObjectivesEntity)
    suspend fun updateTeamObjective(teamObjectivesEntity: TeamObjectivesEntity)
    suspend fun getTeamObjectiveById(objectiveId: Int): TeamObjectivesEntity?
    suspend fun deleteTeamObjective(teamObjectivesEntity: TeamObjectivesEntity)

    // team trainings related info
    suspend fun getAllTrainingsByTeamId(teamId: Long): List<TrainEntity>
    suspend fun getTrainWithTrainTaskByTrainId(trainId: Long): TrainWithTrainTask
    suspend fun insertTrain(trainEntity: TrainEntity): Long
    suspend fun insertTrainTask(trainTaskEntity: TaskEntity): Long
    suspend fun insertTrainCrossTrainTask(trainCrossTrainTaskEntity: TrainCrossTrainTaskEntity)
    suspend fun getLastTrainWithTrainTaskByTeamId(teamId: Long): Long?
    fun getAllTasks(): Flow<List<TaskEntity>>
}
