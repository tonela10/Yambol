package com.sedilant.yambol.data.team.concept

import kotlinx.coroutines.flow.Flow

interface ConceptRepository {
    suspend fun insertConcept(concept: ConceptEntity): Long
    suspend fun insertConcepts(concepts: List<ConceptEntity>)
    suspend fun getConceptById(id: Long): ConceptEntity?
    fun getAllConcepts(): Flow<List<ConceptEntity>>
    suspend fun getListOfConcepts(ids: List<Long>): List<ConceptEntity>
    suspend fun updateConcept(concept: ConceptEntity)
    suspend fun deleteConcept(concept: ConceptEntity)
}
