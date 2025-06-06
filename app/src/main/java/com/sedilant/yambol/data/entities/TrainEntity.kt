package com.sedilant.yambol.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/** An example of Train is the next one:
 * TrainEntity(
 *      date = 10/10/2024
 *      time = 1  -> One hour
 *      numberOfPlayers: 12  -> 12 player for this training
 */
@Entity(tableName = "train")
data class TrainEntity(
    @PrimaryKey(autoGenerate = true)
    val trainId: Int, // TODO change with just --> id
    val date: Long,
    val time: Float,
    val concepts: List<String>,
    val teamId: Int
)
