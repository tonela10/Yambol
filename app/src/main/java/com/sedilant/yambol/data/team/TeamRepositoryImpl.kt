package com.sedilant.yambol.data.team

import com.sedilant.yambol.data.queries.TrainWithTrainTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TeamRepositoryImpl @Inject constructor(
    private val teamDao: TeamDao,
    private val teamObjectivesDao: TeamObjectivesDao,
    private val trainingDao: TrainingDao,
) : TeamRepository {
    // TEAM DAO METHODS
    override suspend fun getAllTeams(): Flow<List<TeamEntity>> {
        return teamDao.getAllTeams()
    }

    override suspend fun insertTeam(teamEntity: TeamEntity): Long {
        return teamDao.insertTeam(teamEntity)
    }

    override suspend fun updateTeam(teamId: Long, newName: String) {
        withContext(Dispatchers.IO) {
            teamDao.updateTeam(TeamEntity(teamId, newName))
        }
    }

    // TEAM OBJECTIVES DAO METHODS
    override suspend fun insertTeamObjective(teamObjectivesEntity: TeamObjectivesEntity) {
        teamObjectivesDao.insertTeamObjective(teamObjectivesEntity)
    }

    override suspend fun getTeamObjectives(teamId: Long): Flow<List<TeamObjectivesEntity>> {
        return teamObjectivesDao.getTeamObjectives(teamId)
    }

    override suspend fun updateTeamObjective(teamObjectivesEntity: TeamObjectivesEntity) {
        teamObjectivesDao.updateTeamObjective(teamObjectivesEntity)
    }

    override suspend fun getTeamObjectiveById(objectiveId: Int): TeamObjectivesEntity? {
        return withContext(Dispatchers.IO) {
            teamObjectivesDao.getTeamObjectiveById(objectiveId)
        }
    }

    override suspend fun deleteTeamObjective(teamObjectivesEntity: TeamObjectivesEntity) {
        return withContext(Dispatchers.IO) {
            teamObjectivesDao.deleteTeamObjective(teamObjectivesEntity)
        }
    }

    // TRAIN DAO METHODS
    override suspend fun getAllTrainingsByTeamId(teamId: Long): List<TrainEntity> {
        return withContext(Dispatchers.IO) {
            trainingDao.getAllTrainsByTeamId(teamId)
        }
    }

    override suspend fun getTrainWithTrainTaskByTrainId(trainId: Long): TrainWithTrainTask {
        return withContext(Dispatchers.IO) {
            trainingDao.getTrainWithTrainTaskByTrainId(trainId)
        }
    }

    override suspend fun insertTrain(trainEntity: TrainEntity): Long {
        return withContext(Dispatchers.IO) {
            trainingDao.insertTrain(trainEntity)
        }
    }

    override suspend fun insertTrainTask(trainTaskEntity: TaskEntity): Long {
        return withContext(Dispatchers.IO) {
            trainingDao.insertTrainTask(trainTaskEntity)
        }
    }

    // TODO Name it like, add task to train
    override suspend fun insertTrainCrossTrainTask(trainCrossTrainTaskEntity: TrainCrossTrainTaskEntity) {
        withContext(Dispatchers.IO) {
            trainingDao.insertTrainCrossTrainTask(trainCrossTrainTaskEntity)
        }
    }

    override suspend fun getLastTrainWithTrainTaskByTeamId(teamId: Long): Long {
        return withContext(Dispatchers.IO) {
            trainingDao.getLastTrainWithTrainTaskByTeamId(teamId)
        }
    }
}
