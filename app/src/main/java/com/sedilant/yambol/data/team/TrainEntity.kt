package com.sedilant.yambol.data.team

import androidx.room.Entity
import androidx.room.PrimaryKey

/** An example of Train is the next one:
 * TrainEntity(
 *      date = 10/10/2024
 *      time = 1  -> One hour
 */
@Entity(tableName = "train")
data class TrainEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val time: Float,
    val concepts: List<Long>,
    val teamId: Long
)
