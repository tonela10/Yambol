package com.sedilant.yambol.data.firestore

import com.google.firebase.Timestamp

// Common metadata for Firestore entities
interface FirestoreEntityMeta {
    val id: String
    val userId: String
    val createdAt: Timestamp?
    val updatedAt: Timestamp?
    val localId: Long?
}

data class TeamDto(
    override val id: String = "",
    val name: String = "",
    override val userId: String = "",
    override val createdAt: Timestamp? = null,
    override val updatedAt: Timestamp? = null,
    override val localId: Long? = null
) : FirestoreEntityMeta

data class PlayerDto(
    override val id: String = "",
    val name: String = "",
    val number: Int? = null,
    val teamId: String = "",
    override val userId: String = "",
    override val createdAt: Timestamp? = null,
    override val updatedAt: Timestamp? = null,
    override val localId: Long? = null
) : FirestoreEntityMeta

data class TaskDto(
    override val id: String = "",
    val name: String = "",
    val description: String = "",
    val variables: List<String> = emptyList(),
    val conceptIds: List<String> = emptyList(),
    override val userId: String = "",
    override val createdAt: Timestamp? = null,
    override val updatedAt: Timestamp? = null,
    override val localId: Long? = null
) : FirestoreEntityMeta

data class TrainDto(
    override val id: String = "",
    val dateMillis: Long? = null,
    val endTime: Float? = null,
    val startTime: Float? = null,
    val teamId: String = "",
    val taskIds: List<String> = emptyList(),
    val conceptIds: List<String> = emptyList(),
    override val userId: String = "",
    override val createdAt: Timestamp? = null,
    override val updatedAt: Timestamp? = null,
    override val localId: Long? = null
) : FirestoreEntityMeta

data class ConceptDto(
    override val id: String = "",
    val name: String = "",
    override val userId: String = "",
    override val createdAt: Timestamp? = null,
    override val updatedAt: Timestamp? = null,
    override val localId: Long? = null
) : FirestoreEntityMeta

data class TeamObjectiveDto(
    override val id: String = "",
    val title: String = "",
    val teamId: String = "",
    val isCompleted: Boolean = false,
    override val userId: String = "",
    override val createdAt: Timestamp? = null,
    override val updatedAt: Timestamp? = null,
    override val localId: Long? = null
) : FirestoreEntityMeta
