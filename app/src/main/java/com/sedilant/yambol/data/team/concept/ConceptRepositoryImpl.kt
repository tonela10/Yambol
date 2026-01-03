package com.sedilant.yambol.data.team.concept

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConceptRepositoryImpl @Inject constructor(
    private val conceptDao: ConceptDao
) : ConceptRepository {
    override suspend fun insertConcept(concept: ConceptEntity): Long {
        return conceptDao.insertConcept(concept)
    }

    override suspend fun insertConcepts(concepts: List<ConceptEntity>) {
        conceptDao.insertConcepts(concepts)
    }

    override suspend fun getConceptById(id: Long): ConceptEntity? {
        return conceptDao.getConceptById(id)
    }

    override fun getAllConcepts(): Flow<List<ConceptEntity>> {
        return conceptDao.getAllConcepts()
    }

    override suspend fun getListOfConcepts(ids: List<Long>): List<ConceptEntity> {
        return conceptDao.getListOfConcepts(ids)
    }

    override suspend fun updateConcept(concept: ConceptEntity) {
        conceptDao.updateConcept(concept)
    }

    override suspend fun deleteConcept(concept: ConceptEntity) {
        conceptDao.deleteConcept(concept)
    }
}
