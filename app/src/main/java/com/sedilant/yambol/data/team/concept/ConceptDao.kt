package com.sedilant.yambol.data.team.concept

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ConceptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConcept(concept: ConceptEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConcepts(concepts: List<ConceptEntity>)

    @Query("SELECT * FROM concepts WHERE id = :id")
    suspend fun getConceptById(id: Long): ConceptEntity?

    @Query("SELECT * FROM concepts ORDER BY name ASC")
    fun getAllConcepts(): Flow<List<ConceptEntity>>

    @Query("SELECT * FROM concepts WHERE id IN (:ids)")
    suspend fun getListOfConcepts(ids: List<Long>): List<ConceptEntity>

    @Query("SELECT COUNT(*) FROM concepts")
    suspend fun getCount(): Int

    @Update
    suspend fun updateConcept(concept: ConceptEntity)

    @Delete
    suspend fun deleteConcept(concept: ConceptEntity)

    @Query("DELETE FROM concepts")
    suspend fun deleteAllConcepts()
}
