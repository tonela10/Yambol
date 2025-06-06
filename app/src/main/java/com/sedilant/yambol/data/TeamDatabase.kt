package com.sedilant.yambol.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sedilant.yambol.data.converters.Converters
import com.sedilant.yambol.data.entities.AbilityNameEntity
import com.sedilant.yambol.data.entities.AbilityRecordEntity
import com.sedilant.yambol.data.entities.PlayerEntity
import com.sedilant.yambol.data.entities.TeamEntity
import com.sedilant.yambol.data.entities.TeamObjectivesEntity
import com.sedilant.yambol.data.entities.TrainCrossTrainTaskEntity
import com.sedilant.yambol.data.entities.TrainEntity
import com.sedilant.yambol.data.entities.TrainTaskEntity

@Database(
    entities = [
        PlayerEntity::class,
        TeamEntity::class,
        TeamObjectivesEntity::class,
        AbilityRecordEntity::class,
        AbilityNameEntity::class,
        TrainEntity::class,
        TrainCrossTrainTaskEntity::class,
        TrainTaskEntity::class],
    version = 1,
    exportSchema = false,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ]
)
@TypeConverters(Converters::class)
abstract class TeamDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun teamObjectivesDao(): TeamObjectivesDao
    abstract fun statsDao(): StatsDao
    abstract fun trainingDao(): TrainingDao

    companion object {
        @Volatile
        private var Instance: TeamDatabase? = null
        fun getDatabase(context: Context): TeamDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TeamDatabase::class.java, "team_database")
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL(
                                """
                            INSERT INTO ability_name (name) VALUES 
                            ('bounce'), 
                            ('pass'), 
                            ('shoot'), 
                            ('defend'), 
                            ('physical_state'), 
                            ('mental_state');
                        """.trimIndent()
                            )
                        }
                    })
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
