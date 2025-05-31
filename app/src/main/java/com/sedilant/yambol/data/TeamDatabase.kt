package com.sedilant.yambol.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sedilant.yambol.data.entities.AbilityNameEntity
import com.sedilant.yambol.data.entities.AbilityRecordEntity
import com.sedilant.yambol.data.entities.PlayerEntity
import com.sedilant.yambol.data.entities.TeamEntity
import com.sedilant.yambol.data.entities.TeamObjectivesEntity

@Database(
    entities = [PlayerEntity::class, TeamEntity::class, TeamObjectivesEntity::class,
        AbilityRecordEntity::class, AbilityNameEntity::class],
    version = 1,
//    exportSchema = true,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ]
)
abstract class TeamDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun teamObjectivesDao(): TeamObjectivesDao
    abstract fun statsDao(): StatsDao

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
