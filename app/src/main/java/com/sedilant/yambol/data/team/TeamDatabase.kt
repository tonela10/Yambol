package com.sedilant.yambol.data.team

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sedilant.yambol.data.converters.Converters

@Database(
    entities = [
        PlayerEntity::class,
        TeamEntity::class,
        TeamObjectivesEntity::class,
        TrainEntity::class,
        TrainCrossTrainTaskEntity::class,
        TaskEntity::class],
    version = 1,
    exportSchema = true,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ]
)
@TypeConverters(Converters::class)
abstract class TeamDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun teamObjectivesDao(): TeamObjectivesDao
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
