package com.sedilant.yambol.data.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference

object FirestoreCollections {
    const val TEAMS = "teams"
    const val PLAYERS = "players"
    const val TASKS = "tasks"
    const val TRAINS = "trains"
    const val CONCEPTS = "concepts"
}

interface TeamRepository {
    fun upsert(team: TeamDto)
    fun listByUser(userId: String, onResult: (List<TeamDto>) -> Unit, onError: (Exception) -> Unit)
}

interface PlayerRepository {
    fun upsert(player: PlayerDto)
    fun listByTeam(userId: String, teamId: String, onResult: (List<PlayerDto>) -> Unit, onError: (Exception) -> Unit)
}

interface TaskRepository {
    fun upsert(task: TaskDto)
    fun listByUser(userId: String, onResult: (List<TaskDto>) -> Unit, onError: (Exception) -> Unit)
    fun getByIds(userId: String, ids: List<String>, onResult: (List<TaskDto>) -> Unit, onError: (Exception) -> Unit)
}

interface TrainRepository {
    fun upsert(train: TrainDto)
    fun listByTeam(userId: String, teamId: String, onResult: (List<TrainDto>) -> Unit, onError: (Exception) -> Unit)
}

interface ConceptRepository {
    fun upsert(concept: ConceptDto)
    fun listByTeam(userId: String, teamId: String, onResult: (List<ConceptDto>) -> Unit, onError: (Exception) -> Unit)
    fun getByIds(userId: String, ids: List<String>, onResult: (List<ConceptDto>) -> Unit, onError: (Exception) -> Unit)
}

// Base repository helpers
abstract class BaseFirestoreRepository<T : FirestoreEntityMeta>(
    private val collection: CollectionReference
) {
    protected fun docRef(id: String): DocumentReference = if (id.isBlank()) collection.document() else collection.document(id)

    protected fun mapDocs(snapshot: com.google.firebase.firestore.QuerySnapshot, clazz: Class<out T>): List<T> =
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
}

class FirestoreTeamRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : TeamRepository, BaseFirestoreRepository<TeamDto>(db.collection(FirestoreCollections.TEAMS)) {

    override fun upsert(team: TeamDto) {
        val doc = docRef(team.id)
        val now = Timestamp.now()
        val payload = team.copy(id = doc.id, createdAt = team.createdAt ?: now, updatedAt = now)
        doc.set(payload)
    }

    override fun listByUser(userId: String, onResult: (List<TeamDto>) -> Unit, onError: (Exception) -> Unit) {
        db.collection(FirestoreCollections.TEAMS)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot -> onResult(mapDocs(snapshot, TeamDto::class.java)) }
            .addOnFailureListener(onError)
    }
}

class FirestorePlayerRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : PlayerRepository, BaseFirestoreRepository<PlayerDto>(db.collection(FirestoreCollections.PLAYERS)) {

    override fun upsert(player: PlayerDto) {
        val doc = docRef(player.id)
        val now = Timestamp.now()
        val payload = player.copy(id = doc.id, createdAt = player.createdAt ?: now, updatedAt = now)
        doc.set(payload)
    }

    override fun listByTeam(userId: String, teamId: String, onResult: (List<PlayerDto>) -> Unit, onError: (Exception) -> Unit) {
        db.collection(FirestoreCollections.PLAYERS)
            .whereEqualTo("userId", userId)
            .whereEqualTo("teamId", teamId)
            .get()
            .addOnSuccessListener { snapshot -> onResult(mapDocs(snapshot, PlayerDto::class.java)) }
            .addOnFailureListener(onError)
    }
}

class FirestoreTaskRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : TaskRepository, BaseFirestoreRepository<TaskDto>(db.collection(FirestoreCollections.TASKS)) {

    override fun upsert(task: TaskDto) {
        val doc = docRef(task.id)
        val now = Timestamp.now()
        val payload = task.copy(id = doc.id, createdAt = task.createdAt ?: now, updatedAt = now)
        doc.set(payload)
    }

    override fun listByUser(userId: String, onResult: (List<TaskDto>) -> Unit, onError: (Exception) -> Unit) {
        db.collection(FirestoreCollections.TASKS)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot -> onResult(mapDocs(snapshot, TaskDto::class.java)) }
            .addOnFailureListener(onError)
    }

    override fun getByIds(userId: String, ids: List<String>, onResult: (List<TaskDto>) -> Unit, onError: (Exception) -> Unit) {
        if (ids.isEmpty()) {
            onResult(emptyList())
            return
        }
        db.collection(FirestoreCollections.TASKS)
            .whereEqualTo("userId", userId)
            .whereIn(FieldPath.documentId(), ids)
            .get()
            .addOnSuccessListener { snapshot -> onResult(mapDocs(snapshot, TaskDto::class.java)) }
            .addOnFailureListener(onError)
    }
}

class FirestoreTrainRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : TrainRepository, BaseFirestoreRepository<TrainDto>(db.collection(FirestoreCollections.TRAINS)) {

    override fun upsert(train: TrainDto) {
        val doc = docRef(train.id)
        val now = Timestamp.now()
        val payload = train.copy(id = doc.id, createdAt = train.createdAt ?: now, updatedAt = now)
        doc.set(payload)
    }

    override fun listByTeam(userId: String, teamId: String, onResult: (List<TrainDto>) -> Unit, onError: (Exception) -> Unit) {
        db.collection(FirestoreCollections.TRAINS)
            .whereEqualTo("userId", userId)
            .whereEqualTo("teamId", teamId)
            .get()
            .addOnSuccessListener { snapshot -> onResult(mapDocs(snapshot, TrainDto::class.java)) }
            .addOnFailureListener(onError)
    }
}

class FirestoreConceptRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ConceptRepository, BaseFirestoreRepository<ConceptDto>(db.collection(FirestoreCollections.CONCEPTS)) {

    override fun upsert(concept: ConceptDto) {
        val doc = docRef(concept.id)
        val now = Timestamp.now()
        val payload = concept.copy(id = doc.id, createdAt = concept.createdAt ?: now, updatedAt = now)
        doc.set(payload)
    }

    override fun listByTeam(userId: String, teamId: String, onResult: (List<ConceptDto>) -> Unit, onError: (Exception) -> Unit) {
        db.collection(FirestoreCollections.CONCEPTS)
            .whereEqualTo("userId", userId)
            .whereEqualTo("teamId", teamId)
            .get()
            .addOnSuccessListener { snapshot -> onResult(mapDocs(snapshot, ConceptDto::class.java)) }
            .addOnFailureListener(onError)
    }

    override fun getByIds(userId: String, ids: List<String>, onResult: (List<ConceptDto>) -> Unit, onError: (Exception) -> Unit) {
        if (ids.isEmpty()) {
            onResult(emptyList())
            return
        }
        db.collection(FirestoreCollections.CONCEPTS)
            .whereEqualTo("userId", userId)
            .whereIn(FieldPath.documentId(), ids)
            .get()
            .addOnSuccessListener { snapshot -> onResult(mapDocs(snapshot, ConceptDto::class.java)) }
            .addOnFailureListener(onError)
    }
}
