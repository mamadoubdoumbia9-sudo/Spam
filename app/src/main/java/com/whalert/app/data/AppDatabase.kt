package com.whalert.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.whalert.app.model.ConsoleCredentials
import com.whalert.app.model.Report
import com.whalert.app.model.User
import com.whalert.app.model.UserPreferences
import com.whalert.app.util.Converters

/**
 * Room database for WhAlert application
 */
@Database(
    entities = [
        User::class,
        UserPreferences::class,
        Report::class,
        ConsoleCredentials::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun reportDao(): ReportDao
    abstract fun preferencesDao(): PreferencesDao
    abstract fun consoleDao(): ConsoleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "whalert_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getDatabaseInMemory(context: Context): AppDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                AppDatabase::class.java
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
