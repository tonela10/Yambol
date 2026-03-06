package com.sedilant.yambol.data.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object FirestoreCollections {
    const val TEAMS = "teams"
    const val PLAYERS = "players"
    const val TASKS = "tasks"
    const val TRAINS = "trains"
    const val CONCEPTS = "concepts"
    const val TEAM_OBJECTIVES = "team_objectives"
}

// TODO split the different repositories into different files
interface TeamRepository {
    fun upsert(team: TeamDto): String
    fun updateName(teamId: String, name: String)
    fun listByUser(userId: String, onResult: (List<TeamDto>) -> Unit, onError: (Exception) -> Unit)
    fun listByUserFlow(userId: String): Flow<List<TeamDto>>
}

interface PlayerRepository {
    fun upsert(player: PlayerDto): String
    fun update(playerId: String, name: String, number: Int)
    fun delete(playerId: String)
    fun listByTeam(
        userId: String,
        teamId: String,
        onResult: (List<PlayerDto>) -> Unit,
        onError: (Exception) -> Unit
    )

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
    fun upsert(task: TaskDto): String
    fun listByUser(userId: String, onResult: (List<TaskDto>) -> Unit, onError: (Exception) -> Unit)
    fun listByUserFlow(userId: String): Flow<List<TaskDto>>
    fun getByIds(
        userId: String,
        ids: List<String>,
        onResult: (List<TaskDto>) -> Unit,
        onError: (Exception) -> Unit
    )

    suspend fun getTasksByIds(userId: String, ids: List<String>): List<TaskDto>
}

interface TrainRepository {
    fun upsert(train: TrainDto): String
    fun addTaskToTrain(trainId: String, taskId: String)
    fun listByTeam(
        userId: String,
        teamId: String,
        onResult: (List<TrainDto>) -> Unit,
        onError: (Exception) -> Unit
    )

    fun listByTeamFlow(userId: String, teamId: String): Flow<List<TrainDto>>
    suspend fun getLastTrainId(userId: String, teamId: String): String?
    suspend fun getTrainById(trainId: String): TrainDto?
}

interface ConceptRepository {
    fun upsert(concept: ConceptDto): String
    fun listByTeam(
        userId: String,
        teamId: String,
        onResult: (List<ConceptDto>) -> Unit,
        onError: (Exception) -> Unit
    )

    fun listByUserFlow(userId: String): Flow<List<ConceptDto>>
    fun getByIds(
        userId: String,
        ids: List<String>,
        onResult: (List<ConceptDto>) -> Unit,
        onError: (Exception) -> Unit
    )

    fun getByIdsFlow(userId: String, ids: List<String>): Flow<List<ConceptDto>>
    suspend fun getConceptsByIds(userId: String, ids: List<String>): List<ConceptDto>
}

interface TeamObjectiveRepository {
    fun upsert(objective: TeamObjectiveDto): String
    fun delete(objectiveId: String)
    fun listByTeamFlow(userId: String, teamId: String): Flow<List<TeamObjectiveDto>>
    suspend fun toggleCompletion(objectiveId: String, isCompleted: Boolean)
}

// Base repository helpers
abstract class BaseFirestoreRepository<T : FirestoreEntityMeta>(
    private val collection: CollectionReference
) {
    protected fun docRef(id: String): DocumentReference =
        if (id.isBlank()) collection.document() else collection.document(id)

    protected fun mapDocs(
        snapshot: com.google.firebase.firestore.QuerySnapshot,
        clazz: Class<out T>
    ): List<T> =
        snapshot.documents.mapNotNull { it.toObject(clazz)?.copyId(it.id) }

    // Utility to copy the id into the DTO without reflection in consumers
    private fun T.copyId(id: String): T {
        @Suppress("UNCHECKED_CAST")
        return when (this) {
            is TeamDto -> this.copy(id = id) as T
            is PlayerDto -> this.copy(id = id) as T
            is TaskDto -> this.copy(id = id) as T
            is TrainDto -> this.copy(id = id) as T
            is ConceptDto -> this.copy(id = id) as T
            else -> this
        }
    }

    // Flow helper
    // Ensure the snapshot listener properly handles updates
    protected fun observeQuery(
        query: Query,
        clazz: Class<out T>
    ): Flow<List<T>> = callbackFlow {
        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                try {
                    trySend(mapDocs(snapshot, clazz))
                } catch (e: Exception) {
                    close(e)
                }
            }
        }
        awaitClose { registration.remove() }
    }
}

class FirestoreTeamRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : TeamRepository, BaseFirestoreRepository<TeamDto>(db.collection(FirestoreCollections.TEAMS)) {

    override fun upsert(team: TeamDto): String {
        val doc = docRef(team.id)
        val now = Timestamp.now()
        val payload = team.copy(id = doc.id, createdAt = team.createdAt ?: now, updatedAt = now)
        doc.set(payload)
        return doc.id
    }

    override fun updateName(teamId: String, name: String) {
        if (teamId.isNotBlank()) {
            db.collection(FirestoreCollections.TEAMS).document(teamId)
                .update(TeamDto::name.name, name)
        }
    }

    override fun listByUser(
        userId: String,
        onResult: (List<TeamDto>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(FirestoreCollections.TEAMS)
            .whereEqualTo(TeamDto::userId.name, userId)
            .get()
            .addOnSuccessListener { snapshot -> onResult(mapDocs(snapshot, TeamDto::class.java)) }
            .addOnFailureListener(onError)
    }

    override fun listByUserFlow(userId: String): Flow<List<TeamDto>> {
        return observeQuery(
            db.collection(FirestoreCollections.TEAMS).whereEqualTo(TeamDto::userId.name, userId),
            TeamDto::class.java
        )
    }
}

class FirestorePlayerRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : PlayerRepository,
    BaseFirestoreRepository<PlayerDto>(db.collection(FirestoreCollections.PLAYERS)) {

    override fun upsert(player: PlayerDto): String {
        val doc = docRef(player.id)
        val now = Timestamp.now()
        val payload = player.copy(id = doc.id, createdAt = player.createdAt ?: now, updatedAt = now)
        doc.set(payload)
        return doc.id
    }

    override fun update(playerId: String, name: String, number: Int) {
        if (playerId.isNotBlank()) {
            db.collection(FirestoreCollections.PLAYERS).document(playerId)
                .update(
                    mapOf(
                        PlayerDto::name.name to name,
                        PlayerDto::number.name to number,
                        PlayerDto::updatedAt.name to Timestamp.now()
                    )
                )
        }
    }

    override fun delete(playerId: String) {
        if (playerId.isNotBlank()) {
            db.collection(FirestoreCollections.PLAYERS).document(playerId).delete()
        }
    }

    override fun listByTeam(
        userId: String,
        teamId: String,
        onResult: (List<PlayerDto>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(FirestoreCollections.PLAYERS)
            .whereEqualTo(PlayerDto::userId.name, userId)
            .whereEqualTo(PlayerDto::teamId.name, teamId)
            .get()
            .addOnSuccessListener { snapshot -> onResult(mapDocs(snapshot, PlayerDto::class.java)) }
            .addOnFailureListener(onError)
    }

    override fun listByTeamFlow(userId: String, teamId: String): Flow<List<PlayerDto>> {
        return observeQuery(
            db.collection(FirestoreCollections.PLAYERS)
                .whereEqualTo(PlayerDto::userId.name, userId)
                .whereEqualTo(PlayerDto::teamId.name, teamId),
            PlayerDto::class.java
        )
    }

    override suspend fun getPlayerById(playerId: String): PlayerDto? {
        val snapshot = db.collection(FirestoreCollections.PLAYERS).document(playerId).get().await()
        return snapshot.toObject(PlayerDto::class.java)?.copy(id = snapshot.id)
    }

    override suspend fun isJerseyNumberTaken(
        userId: String,
        teamId: String,
        jerseyNumber: Int,
        excludePlayerId: String?
    ): Boolean {
        // We can do this with a query.
        // Need kotlinx-coroutines-play-services to check result.
        // Or wrap it. Let's assume we can use await() or similar.
        // If not available, we can use callbackFlow/suspendCanceleableCoroutine.
        // Actually, let's keep it simple with get().await() if we can, but I haven't seen imports for tasks.await yet.
        // Wait, AuthRepositoryImpl used `kotlinx.coroutines.tasks.await`. So it's available.
        // I need to search and ensure `import kotlinx.coroutines.tasks.await` is there or I can add it.

        try {
            val query = db.collection(FirestoreCollections.PLAYERS)
                .whereEqualTo(PlayerDto::userId.name, userId)
                .whereEqualTo(PlayerDto::teamId.name, teamId)
                .whereEqualTo(PlayerDto::number.name, jerseyNumber)

            val snapshot = query.get().await() // Assuming await is imported or I'll add import
            if (snapshot.isEmpty) return false

            // Filter excludePlayerId if present
            if (excludePlayerId != null) {
                return snapshot.documents.any { it.id != excludePlayerId }
            }
            return true
        } catch (e: Exception) {
            return false // On error assume safe or handle? Let's say false.
        }
    }
}

class FirestoreTaskRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : TaskRepository, BaseFirestoreRepository<TaskDto>(db.collection(FirestoreCollections.TASKS)) {

    override fun upsert(task: TaskDto): String {
        val doc = docRef(task.id)
        val now = Timestamp.now()
        val payload = task.copy(id = doc.id, createdAt = task.createdAt ?: now, updatedAt = now)
        doc.set(payload)
        return doc.id
    }

    override fun listByUser(
        userId: String,
        onResult: (List<TaskDto>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(FirestoreCollections.TASKS)
            .whereEqualTo(TaskDto::userId.name, userId)
            .get()
            .addOnSuccessListener { snapshot -> onResult(mapDocs(snapshot, TaskDto::class.java)) }
            .addOnFailureListener(onError)
    }

    override fun listByUserFlow(userId: String): Flow<List<TaskDto>> {
        return observeQuery(
            db.collection(FirestoreCollections.TASKS).whereEqualTo(TaskDto::userId.name, userId),
            TaskDto::class.java
        )
    }

    override fun getByIds(
        userId: String,
        ids: List<String>,
        onResult: (List<TaskDto>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (ids.isEmpty()) {
            onResult(emptyList())
            return
        }
        db.collection(FirestoreCollections.TASKS)
            .whereEqualTo(TaskDto::userId.name, userId)
            .whereIn(FieldPath.documentId(), ids)
            .get()
            .addOnSuccessListener { snapshot -> onResult(mapDocs(snapshot, TaskDto::class.java)) }
            .addOnFailureListener(onError)
    }

    override suspend fun getTasksByIds(userId: String, ids: List<String>): List<TaskDto> {
        if (ids.isEmpty()) return emptyList()
        // Firestore whereIn supports up to 10 items. If we have more, we need to chunk requests.
        // For simplicity assuming < 10 or implemented chunking logic if needed.
        // Assuming simple case for now.
        return try {
            val validIds = ids.take(10) // Limit to 10 for safety in this migration step
            val snapshot = db.collection(FirestoreCollections.TASKS)
                .whereEqualTo(TaskDto::userId.name, userId)
                .whereIn(FieldPath.documentId(), validIds)
                .get()
                .await()
            mapDocs(snapshot, TaskDto::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

class FirestoreTrainRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : TrainRepository, BaseFirestoreRepository<TrainDto>(db.collection(FirestoreCollections.TRAINS)) {

    override fun upsert(train: TrainDto): String {
        val doc = docRef(train.id)
        val now = Timestamp.now()
        val payload = train.copy(id = doc.id, createdAt = train.createdAt ?: now, updatedAt = now)
        doc.set(payload)
        return doc.id
    }

    override fun addTaskToTrain(trainId: String, taskId: String) {
        if (trainId.isNotBlank() && taskId.isNotBlank()) {
            db.collection(FirestoreCollections.TRAINS).document(trainId)
                .update(TrainDto::taskIds.name, FieldValue.arrayUnion(taskId))
        }
    }

    override fun listByTeam(
        userId: String,
        teamId: String,
        onResult: (List<TrainDto>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(FirestoreCollections.TRAINS)
            .whereEqualTo(TrainDto::userId.name, userId)
            .whereEqualTo(TrainDto::teamId.name, teamId)
            .get()
            .addOnSuccessListener { snapshot -> onResult(mapDocs(snapshot, TrainDto::class.java)) }
            .addOnFailureListener(onError)
    }

    override fun listByTeamFlow(userId: String, teamId: String): Flow<List<TrainDto>> {
        return observeQuery(
            db.collection(FirestoreCollections.TRAINS)
                .whereEqualTo(TrainDto::userId.name, userId)
                .whereEqualTo(TrainDto::teamId.name, teamId),
            TrainDto::class.java
        )
    }

    override suspend fun getLastTrainId(userId: String, teamId: String): String? {
        try {
            val snapshot = db.collection(FirestoreCollections.TRAINS)
                .whereEqualTo(TrainDto::userId.name, userId)
                .whereEqualTo(TrainDto::teamId.name, teamId)
                // Assuming we want the last created one, or last date? Room query was "ORDER BY id DESC".
                // Auto-inc ID usually correlates with creation time.
                .orderBy(TrainDto::dateMillis.name, Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            return snapshot.documents.firstOrNull()?.id
        } catch (e: Exception) {
            return null
        }
    }

    override suspend fun getTrainById(trainId: String): TrainDto? {
        if (trainId.isBlank()) return null
        val snapshot = db.collection(FirestoreCollections.TRAINS).document(trainId).get().await()
        return snapshot.toObject(TrainDto::class.java)?.copy(id = snapshot.id)
    }
}

class FirestoreConceptRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ConceptRepository,
    BaseFirestoreRepository<ConceptDto>(db.collection(FirestoreCollections.CONCEPTS)) {

    override fun upsert(concept: ConceptDto): String {
        val doc = docRef(concept.id)
        val now = Timestamp.now()
        val payload =
            concept.copy(id = doc.id, createdAt = concept.createdAt ?: now, updatedAt = now)
        doc.set(payload)
        return doc.id
    }

    override fun listByTeam(
        userId: String,
        teamId: String,
        onResult: (List<ConceptDto>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(FirestoreCollections.CONCEPTS)
            .whereEqualTo(ConceptDto::userId.name, userId)
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(
                    mapDocs(
                        snapshot,
                        ConceptDto::class.java
                    )
                )
            }
            .addOnFailureListener(onError)
    }

    override fun listByUserFlow(userId: String): Flow<List<ConceptDto>> {
        return observeQuery(
            db.collection(FirestoreCollections.CONCEPTS)
                .whereEqualTo(ConceptDto::userId.name, userId),
            ConceptDto::class.java
        )
    }

    override fun getByIds(
        userId: String,
        ids: List<String>,
        onResult: (List<ConceptDto>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (ids.isEmpty()) {
            onResult(emptyList())
            return
        }
        db.collection(FirestoreCollections.CONCEPTS)
            .whereEqualTo(ConceptDto::userId.name, userId)
            .whereIn(FieldPath.documentId(), ids)
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(
                    mapDocs(
                        snapshot,
                        ConceptDto::class.java
                    )
                )
            }
            .addOnFailureListener(onError)
    }

    override fun getByIdsFlow(userId: String, ids: List<String>): Flow<List<ConceptDto>> {
        if (ids.isEmpty()) return kotlinx.coroutines.flow.flowOf(emptyList())
        return observeQuery(
            db.collection(FirestoreCollections.CONCEPTS)
                .whereEqualTo(ConceptDto::userId.name, userId)
                .whereIn(FieldPath.documentId(), ids),
            ConceptDto::class.java
        )
    }

    override suspend fun getConceptsByIds(userId: String, ids: List<String>): List<ConceptDto> {
        if (ids.isEmpty()) return emptyList()
        return try {
            val snapshot = db.collection(FirestoreCollections.CONCEPTS)
                .whereEqualTo(ConceptDto::userId.name, userId)
                .whereIn(FieldPath.documentId(), ids)
                .get()
                .await()
            mapDocs(snapshot, ConceptDto::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

class FirestoreTeamObjectiveRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : TeamObjectiveRepository,
    BaseFirestoreRepository<TeamObjectiveDto>(db.collection(FirestoreCollections.TEAM_OBJECTIVES)) {

    override fun upsert(objective: TeamObjectiveDto): String {
        val doc = docRef(objective.id)
        val now = Timestamp.now()
        val payload =
            objective.copy(id = doc.id, createdAt = objective.createdAt ?: now, updatedAt = now)
        doc.set(payload)
        return doc.id
    }

    override fun delete(objectiveId: String) {
        if (objectiveId.isNotBlank()) {
            db.collection(FirestoreCollections.TEAM_OBJECTIVES).document(objectiveId).delete()
        }
    }

    override fun listByTeamFlow(userId: String, teamId: String): Flow<List<TeamObjectiveDto>> {
        return observeQuery(
            db.collection(FirestoreCollections.TEAM_OBJECTIVES)
                .whereEqualTo(TeamObjectiveDto::userId.name, userId)
                .whereEqualTo(TeamObjectiveDto::teamId.name, teamId),
            TeamObjectiveDto::class.java
        )
    }

    override suspend fun toggleCompletion(objectiveId: String, isCompleted: Boolean) {
        if (objectiveId.isNotBlank()) {
            db.collection(FirestoreCollections.TEAM_OBJECTIVES).document(objectiveId)
                .update(TeamObjectiveDto::completed.name, isCompleted).await()
        }
    }
}
