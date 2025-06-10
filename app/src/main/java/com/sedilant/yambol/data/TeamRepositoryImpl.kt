package com.sedilant.yambol.data

import com.sedilant.yambol.data.entities.PlayerEntity
import com.sedilant.yambol.data.entities.TeamEntity
import com.sedilant.yambol.data.entities.TeamObjectivesEntity
import com.sedilant.yambol.data.entities.TrainCrossTrainTaskEntity
import com.sedilant.yambol.data.entities.TrainEntity
import com.sedilant.yambol.data.entities.TrainTaskEntity
import com.sedilant.yambol.data.queries.TrainWithTrainTask
import com.sedilant.yambol.domain.mapToDomain
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TeamRepositoryImpl @Inject constructor(
    private val playerDao: PlayerDao,
    private val teamObjectivesDao: TeamObjectivesDao,
    private val trainingDao: TrainingDao,
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

    override suspend fun updatePlayer(playerId: Int, newName: String, newNumber: Int, newPosition: Int) {
        withContext(Dispatchers.IO) {
            playerDao.updatePlayer(playerId, newName, newNumber, newPosition)
        }
    }

    override suspend fun getPlayerTeamId(playerId: Int): Int {
        return withContext(Dispatchers.IO) {
            playerDao.getPlayerTeamId(playerId)
        }
    }

    override suspend fun isJerseyNumberTaken(teamId: Int, jerseyNumber: Int, excludePlayerId: Int?): Boolean {
        return withContext(Dispatchers.IO) {
            playerDao.isJerseyNumberTaken(teamId, jerseyNumber, excludePlayerId)
        }
    }

    override suspend fun deletePlayer(playerId: Int) {
        withContext(Dispatchers.IO) {
            playerDao.deletePlayerById(playerId)
        }
    }
    // TEAM OBJECTIVES DAO METHODS
    override suspend fun insertTeamObjective(teamObjectivesEntity: TeamObjectivesEntity) {
        teamObjectivesDao.insertTeamObjective(teamObjectivesEntity)
    }

    override suspend fun getTeamObjectives(teamId: Int): Flow<List<TeamObjectivesEntity>> {
        return teamObjectivesDao.getTeamObjectives(teamId)
    }

    override suspend fun updateTeamObjective(teamObjectivesEntity: TeamObjectivesEntity) {
        withContext(Dispatchers.IO) {
            teamObjectivesDao.updateTeamObjective(teamObjectivesEntity)
        }
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

    override suspend fun getPlayer(id: Int): PlayerUiModel {
        return withContext(Dispatchers.IO) {
            playerDao.getPlayer(id).mapToDomain()
        }
    }

    override suspend fun updateTeam(teamId: Int, newName: String) {
        withContext(Dispatchers.IO) {
            playerDao.updateTeam(teamId, newName)
        }
    }

    // TRAIN DAO METHODS
    override suspend fun getAllTrainingsByTeamId(teamId: Int): List<TrainEntity> {
        return withContext(Dispatchers.IO) {
            trainingDao.getAllTrainsByTeamId(teamId)
        }
    }

    override suspend fun getTrainWithTrainTaskByTrainId(trainId: Int): TrainWithTrainTask {
        return withContext(Dispatchers.IO) {
            trainingDao.getTrainWithTrainTaskByTrainId(trainId)
        }
    }

    override suspend fun insertTrain(trainEntity: TrainEntity): Int {
        return withContext(Dispatchers.IO) {
            trainingDao.insertTrain(trainEntity).toInt() // TODO undo this ñapa
        }
    }

    override suspend fun insertTrainTask(trainTaskEntity: TrainTaskEntity): Int {
        return withContext(Dispatchers.IO) {
            trainingDao.insertTrainTask(trainTaskEntity).toInt() // TODO undo this ñapa
        }
    }

    override suspend fun insertTrainCrossTrainTask(trainCrossTrainTaskEntity: TrainCrossTrainTaskEntity) {
        withContext(Dispatchers.IO) {
            trainingDao.insertTrainCrossTrainTask(trainCrossTrainTaskEntity)
        }
    }

    override suspend fun getLastTrainWithTrainTaskByTeamId(teamId: Int): Long {
        return withContext(Dispatchers.IO) {
            trainingDao.getLastTrainWithTrainTaskByTeamId(teamId)
        }
    }
}
