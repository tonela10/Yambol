package com.sedilant.yambol.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sedilant.yambol.data.converters.Converters
import com.sedilant.yambol.data.draftTrain.TrainingDraftDao
import com.sedilant.yambol.data.draftTrain.TrainingDraftEntity
import com.sedilant.yambol.data.draftTrain.TrainingTaskEntity
import com.sedilant.yambol.data.team.TaskEntity
import com.sedilant.yambol.data.team.TeamDao
import com.sedilant.yambol.data.team.TeamEntity
import com.sedilant.yambol.data.team.TeamObjectivesDao
import com.sedilant.yambol.data.team.TeamObjectivesEntity
import com.sedilant.yambol.data.team.TrainCrossTrainTaskEntity
import com.sedilant.yambol.data.team.TrainEntity
import com.sedilant.yambol.data.team.TrainingDao
import com.sedilant.yambol.data.team.concept.ConceptDao
import com.sedilant.yambol.data.team.concept.ConceptEntity
import com.sedilant.yambol.data.team.player.PlayerDao
import com.sedilant.yambol.data.team.player.PlayerEntity

@Database(
    entities = [
        // Former TeamDatabase
        PlayerEntity::class,
        TeamEntity::class,
        TeamObjectivesEntity::class,
        TrainEntity::class,
        TrainCrossTrainTaskEntity::class,
        TaskEntity::class,
        ConceptEntity::class,
        // Former TrainingDatabase
        TrainingDraftEntity::class,
        TrainingTaskEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class YambolDatabase : RoomDatabase() {
    abstract fun conceptDao(): ConceptDao
    abstract fun playerDao(): PlayerDao
    abstract fun teamObjectivesDao(): TeamObjectivesDao
    abstract fun trainingDao(): TrainingDao
    abstract fun teamDao(): TeamDao
    abstract fun trainingDraftDao(): TrainingDraftDao

    companion object {
        @Volatile
        private var Instance: YambolDatabase? = null

        fun getDatabase(context: Context): YambolDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, YambolDatabase::class.java, "yambol_database")
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
