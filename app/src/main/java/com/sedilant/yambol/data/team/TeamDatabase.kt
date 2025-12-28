package com.sedilant.yambol.data.team

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sedilant.yambol.data.converters.Converters
import com.sedilant.yambol.data.team.player.PlayerDao
import com.sedilant.yambol.data.team.player.PlayerEntity

@Database(
    entities = [
        PlayerEntity::class,
        TeamEntity::class,
        TeamObjectivesEntity::class,
        TrainEntity::class,
        TrainCrossTrainTaskEntity::class,
        TaskEntity::class],
    version = 1,
    exportSchema = false, // TODO enabled it before production
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ]
)
@TypeConverters(Converters::class)
abstract class TeamDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun teamObjectivesDao(): TeamObjectivesDao
    abstract fun trainingDao(): TrainingDao
    abstract fun teamDao(): TeamDao


    companion object {
        @Volatile
        private var Instance: TeamDatabase? = null
        fun getDatabase(context: Context): TeamDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TeamDatabase::class.java, "team_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
