package com.sedilant.yambol.data.firestore

import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.DocumentReference
import dev.gitlive.firebase.firestore.FieldPath
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

object FirestoreCollections {
    const val TEAMS = "teams"
    const val PLAYERS = "players"
    const val TASKS = "tasks"
    const val TRAINS = "trains"
    const val CONCEPTS = "concepts"
    const val TEAM_OBJECTIVES = "team_objectives"
}

interface TeamRepository {
    suspend fun upsert(team: TeamDto): String
    suspend fun updateName(teamId: String, name: String)
    fun listByUserFlow(userId: String): Flow<List<TeamDto>>
}

interface PlayerRepository {
    suspend fun upsert(player: PlayerDto): String
    suspend fun update(playerId: String, name: String, number: Int)
    suspend fun delete(playerId: String)
    fun listByTeamFlow(userId: String, teamId: String): Flow<List<PlayerDto>>
    suspend fun getPlayerById(playerId: String): PlayerDto?
    suspend fun isJerseyNumberTaken(
        userId: String,
        teamId: String,
        jerseyNumber: Int,
        excludePlayerId: String? = null
    ): Boolean
}

interface TaskRepository {
    suspend fun upsert(task: TaskDto): String
    fun listByUserFlow(userId: String): Flow<List<TaskDto>>
    suspend fun getTasksByIds(userId: String, ids: List<String>): List<TaskDto>
}

interface TrainRepository {
    suspend fun upsert(train: TrainDto): String
    suspend fun addTaskToTrain(trainId: String, taskId: String)
    fun listByTeamFlow(userId: String, teamId: String): Flow<List<TrainDto>>
    suspend fun getLastTrainId(userId: String, teamId: String): String?
    suspend fun getTrainById(trainId: String): TrainDto?
}

interface ConceptRepository {
    suspend fun upsert(concept: ConceptDto): String
    fun listByUserFlow(userId: String): Flow<List<ConceptDto>>
    fun getByIdsFlow(userId: String, ids: List<String>): Flow<List<ConceptDto>>
    suspend fun getConceptsByIds(userId: String, ids: List<String>): List<ConceptDto>
}

interface TeamObjectiveRepository {
    suspend fun upsert(objective: TeamObjectiveDto): String
    suspend fun delete(objectiveId: String)
    fun listByTeamFlow(userId: String, teamId: String): Flow<List<TeamObjectiveDto>>
    suspend fun toggleCompletion(objectiveId: String, isCompleted: Boolean)
}

abstract class BaseFirestoreRepository<T : FirestoreEntityMeta>(
    protected val collection: CollectionReference
) {
    protected fun docRef(id: String): DocumentReference =
        if (id.isBlank()) collection.document else collection.document(id)
}

class FirestoreTeamRepository(
    private val db: FirebaseFirestore
) : TeamRepository, BaseFirestoreRepository<TeamDto>(db.collection(FirestoreCollections.TEAMS)) {

    override suspend fun upsert(team: TeamDto): String {
        val doc = docRef(team.id)
        val now = Timestamp.now()
        val payload = team.copy(id = doc.id, createdAt = team.createdAt ?: now, updatedAt = now)
        doc.set(payload)
        return doc.id
    }

    override suspend fun updateName(teamId: String, name: String) {
        if (teamId.isNotBlank()) {
            db.collection(FirestoreCollections.TEAMS).document(teamId)
                .update(TeamDto::name.name to name)
        }
    }

    override fun listByUserFlow(userId: String): Flow<List<TeamDto>> =
        db.collection(FirestoreCollections.TEAMS)
            .where { TeamDto::userId.name equalTo userId }
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try { doc.data<TeamDto>().copy(id = doc.id) } catch (_: Exception) { null }
                }
            }
}

class FirestorePlayerRepository(
    private val db: FirebaseFirestore
) : PlayerRepository, BaseFirestoreRepository<PlayerDto>(db.collection(FirestoreCollections.PLAYERS)) {

    override suspend fun upsert(player: PlayerDto): String {
        val doc = docRef(player.id)
        val now = Timestamp.now()
        val payload = player.copy(id = doc.id, createdAt = player.createdAt ?: now, updatedAt = now)
        doc.set(payload)
        return doc.id
    }

    override suspend fun update(playerId: String, name: String, number: Int) {
        if (playerId.isNotBlank()) {
            db.collection(FirestoreCollections.PLAYERS).document(playerId)
                .update(
                    PlayerDto::name.name to name,
                    PlayerDto::number.name to number,
                    PlayerDto::updatedAt.name to Timestamp.now()
                )
        }
    }

    override suspend fun delete(playerId: String) {
        if (playerId.isNotBlank()) {
            db.collection(FirestoreCollections.PLAYERS).document(playerId).delete()
        }
    }

    override fun listByTeamFlow(userId: String, teamId: String): Flow<List<PlayerDto>> =
        db.collection(FirestoreCollections.PLAYERS)
            .where {
                all(
                    PlayerDto::userId.name equalTo userId,
                    PlayerDto::teamId.name equalTo teamId
                )
            }
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try { doc.data<PlayerDto>().copy(id = doc.id) } catch (_: Exception) { null }
                }
            }

    override suspend fun getPlayerById(playerId: String): PlayerDto? {
        val snapshot = db.collection(FirestoreCollections.PLAYERS).document(playerId).get()
        return try { snapshot.data<PlayerDto>().copy(id = snapshot.id) } catch (_: Exception) { null }
    }

    override suspend fun isJerseyNumberTaken(
        userId: String,
        teamId: String,
        jerseyNumber: Int,
        excludePlayerId: String?
    ): Boolean {
        return try {
            val snapshot = db.collection(FirestoreCollections.PLAYERS)
                .where {
                    all(
                        PlayerDto::userId.name equalTo userId,
                        PlayerDto::teamId.name equalTo teamId,
                        PlayerDto::number.name equalTo jerseyNumber
                    )
                }
                .get()
            if (snapshot.documents.isEmpty()) return false
            if (excludePlayerId != null) snapshot.documents.any { it.id != excludePlayerId }
            else true
        } catch (_: Exception) {
            false
        }
    }
}

class FirestoreTaskRepository(
    private val db: FirebaseFirestore
) : TaskRepository, BaseFirestoreRepository<TaskDto>(db.collection(FirestoreCollections.TASKS)) {

    override suspend fun upsert(task: TaskDto): String {
        val doc = docRef(task.id)
        val now = Timestamp.now()
        val payload = task.copy(id = doc.id, createdAt = task.createdAt ?: now, updatedAt = now)
        doc.set(payload)
        return doc.id
    }

    override fun listByUserFlow(userId: String): Flow<List<TaskDto>> =
        db.collection(FirestoreCollections.TASKS)
            .where { TaskDto::userId.name equalTo userId }
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try { doc.data<TaskDto>().copy(id = doc.id) } catch (_: Exception) { null }
                }
            }

    override suspend fun getTasksByIds(userId: String, ids: List<String>): List<TaskDto> {
        if (ids.isEmpty()) return emptyList()
        return try {
            val result = mutableListOf<TaskDto>()
            ids.distinct().filter { it.isNotBlank() }.chunked(10).forEach { chunkIds ->
                val snapshot = db.collection(FirestoreCollections.TASKS)
                    .where {
                        all(
                            TaskDto::userId.name equalTo userId,
                            FieldPath.documentId inArray chunkIds
                        )
                    }
                    .get()
                snapshot.documents.mapNotNullTo(result) { doc ->
                    try { doc.data<TaskDto>().copy(id = doc.id) } catch (_: Exception) { null }
                }
            }
            result
        } catch (_: Exception) {
            emptyList()
        }
    }
}

class FirestoreTrainRepository(
    private val db: FirebaseFirestore
) : TrainRepository, BaseFirestoreRepository<TrainDto>(db.collection(FirestoreCollections.TRAINS)) {

    override suspend fun upsert(train: TrainDto): String {
        val doc = docRef(train.id)
        val now = Timestamp.now()
        val payload = train.copy(id = doc.id, createdAt = train.createdAt ?: now, updatedAt = now)
        doc.set(payload)
        return doc.id
    }

    override suspend fun addTaskToTrain(trainId: String, taskId: String) {
        if (trainId.isNotBlank() && taskId.isNotBlank()) {
            db.collection(FirestoreCollections.TRAINS).document(trainId)
                .update(TrainDto::taskIds.name to FieldValue.arrayUnion(taskId))
        }
    }

    override fun listByTeamFlow(userId: String, teamId: String): Flow<List<TrainDto>> =
        db.collection(FirestoreCollections.TRAINS)
            .where {
                all(
                    TrainDto::userId.name equalTo userId,
                    TrainDto::teamId.name equalTo teamId
                )
            }
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try { doc.data<TrainDto>().copy(id = doc.id) } catch (_: Exception) { null }
                }
            }

    override suspend fun getLastTrainId(userId: String, teamId: String): String? {
        return try {
            val snapshot = db.collection(FirestoreCollections.TRAINS)
                .where {
                    all(
                        TrainDto::userId.name equalTo userId,
                        TrainDto::teamId.name equalTo teamId
                    )
                }
                .get()
            snapshot.documents
                .mapNotNull { doc ->
                    try { doc.data<TrainDto>().copy(id = doc.id) } catch (_: Exception) { null }
                }
                .maxByOrNull { it.dateMillis ?: 0L }
                ?.id
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun getTrainById(trainId: String): TrainDto? {
        if (trainId.isBlank()) return null
        val snapshot = db.collection(FirestoreCollections.TRAINS).document(trainId).get()
        return try { snapshot.data<TrainDto>().copy(id = snapshot.id) } catch (_: Exception) { null }
    }
}

class FirestoreConceptRepository(
    private val db: FirebaseFirestore
) : ConceptRepository,
    BaseFirestoreRepository<ConceptDto>(db.collection(FirestoreCollections.CONCEPTS)) {

    override suspend fun upsert(concept: ConceptDto): String {
        val doc = docRef(concept.id)
        val now = Timestamp.now()
        val payload = concept.copy(id = doc.id, createdAt = concept.createdAt ?: now, updatedAt = now)
        doc.set(payload)
        return doc.id
    }

    override fun listByUserFlow(userId: String): Flow<List<ConceptDto>> =
        db.collection(FirestoreCollections.CONCEPTS)
            .where { ConceptDto::userId.name equalTo userId }
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try { doc.data<ConceptDto>().copy(id = doc.id) } catch (_: Exception) { null }
                }
            }

    override fun getByIdsFlow(userId: String, ids: List<String>): Flow<List<ConceptDto>> {
        if (ids.isEmpty()) return flowOf(emptyList())
        return db.collection(FirestoreCollections.CONCEPTS)
            .where {
                all(
                    ConceptDto::userId.name equalTo userId,
                    FieldPath.documentId inArray ids.take(10)
                )
            }
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try { doc.data<ConceptDto>().copy(id = doc.id) } catch (_: Exception) { null }
                }
            }
    }

    override suspend fun getConceptsByIds(userId: String, ids: List<String>): List<ConceptDto> {
        if (ids.isEmpty()) return emptyList()
        val orderedIds = ids.distinct().filter { it.isNotBlank() }
        if (orderedIds.isEmpty()) return emptyList()
        return try {
            val conceptsById = mutableMapOf<String, ConceptDto>()
            orderedIds.chunked(10).forEach { chunkIds ->
                val snapshot = db.collection(FirestoreCollections.CONCEPTS)
                    .where {
                        all(
                            ConceptDto::userId.name equalTo userId,
                            FieldPath.documentId inArray chunkIds
                        )
                    }
                    .get()
                snapshot.documents.mapNotNull { doc ->
                    try { doc.data<ConceptDto>().copy(id = doc.id) } catch (_: Exception) { null }
                }.forEach { concept -> conceptsById[concept.id] = concept }
            }
            orderedIds.mapNotNull { conceptsById[it] }
        } catch (_: Exception) {
            emptyList()
        }
    }
}

class FirestoreTeamObjectiveRepository(
    private val db: FirebaseFirestore
) : TeamObjectiveRepository,
    BaseFirestoreRepository<TeamObjectiveDto>(db.collection(FirestoreCollections.TEAM_OBJECTIVES)) {

    override suspend fun upsert(objective: TeamObjectiveDto): String {
        val doc = docRef(objective.id)
        val now = Timestamp.now()
        val payload = objective.copy(id = doc.id, createdAt = objective.createdAt ?: now, updatedAt = now)
        doc.set(payload)
        return doc.id
    }

    override suspend fun delete(objectiveId: String) {
        if (objectiveId.isNotBlank()) {
            db.collection(FirestoreCollections.TEAM_OBJECTIVES).document(objectiveId).delete()
        }
    }

    override fun listByTeamFlow(userId: String, teamId: String): Flow<List<TeamObjectiveDto>> =
        db.collection(FirestoreCollections.TEAM_OBJECTIVES)
            .where {
                all(
                    TeamObjectiveDto::userId.name equalTo userId,
                    TeamObjectiveDto::teamId.name equalTo teamId
                )
            }
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try { doc.data<TeamObjectiveDto>().copy(id = doc.id) } catch (_: Exception) { null }
                }
            }

    override suspend fun toggleCompletion(objectiveId: String, isCompleted: Boolean) {
        if (objectiveId.isNotBlank()) {
            db.collection(FirestoreCollections.TEAM_OBJECTIVES).document(objectiveId)
                .update(TeamObjectiveDto::completed.name to isCompleted)
        }
    }
}
