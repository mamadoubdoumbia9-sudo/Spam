package com.whalert.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.whalert.app.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for UserPreferences entities
 */
@Dao
interface PreferencesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(preferences: UserPreferences): Long

    @Update
    suspend fun updatePreferences(preferences: UserPreferences): Int

    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    suspend fun getPreferences(userId: String): UserPreferences?

    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    fun getPreferencesFlow(userId: String): Flow<UserPreferences?>

    @Query("DELETE FROM user_preferences WHERE userId = :userId")
    suspend fun deletePreferences(userId: String): Int

    @Query("SELECT COUNT(*) FROM user_preferences")
    suspend fun getPreferencesCount(): Int
}
