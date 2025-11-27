package com.sedilant.yambol.data.draftTrain

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TrainingDraftEntity::class,
        TrainingTaskEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TrainingDatabase : RoomDatabase() {
    abstract fun trainingDraftDao(): TrainingDraftDao

    companion object {
        @Volatile
        private var INSTANCE: TrainingDatabase? = null

        fun getDatabase(context: Context): TrainingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrainingDatabase::class.java,
                    "training_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}