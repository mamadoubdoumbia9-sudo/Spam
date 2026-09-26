package com.whalert.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.whalert.app.model.User
import com.whalert.app.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User entities
 */
@Dao
interface UserDao {

    // User operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User): Int

    @Query("SELECT * FROM users WHERE uid = :userId")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE whatsappNumber = :phoneNumber")
    suspend fun getUserByWhatsAppNumber(phoneNumber: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("DELETE FROM users WHERE uid = :userId")
    suspend fun deleteUser(userId: String): Int

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("SELECT COUNT(*) FROM users WHERE whatsappConnected = 1")
    suspend fun getConnectedUsersCount(): Int

    // User preferences operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(preferences: UserPreferences): Long

    @Update
    suspend fun updatePreferences(preferences: UserPreferences): Int

    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    suspend fun getPreferencesByUserId(userId: String): UserPreferences?

    // Daily report limit operations
    @Query("""
        UPDATE users 
        SET dailyReportCount = dailyReportCount + 1, 
            lastReportDate = CURRENT_TIMESTAMP
        WHERE uid = :userId
    """)
    suspend fun incrementDailyReportCount(userId: String): Int

    @Query("""
        UPDATE users 
        SET dailyReportCount = 0
        WHERE uid = :userId
    """)
    suspend fun resetDailyReportCount(userId: String): Int

    @Query("SELECT dailyReportCount FROM users WHERE uid = :userId")
    suspend fun getDailyReportCount(userId: String): Int?

    // Combined operations
    @Query("""
        SELECT u.*, up.* FROM users u
        LEFT JOIN user_preferences up ON u.uid = up.userId
        WHERE u.uid = :userId
    """)
    suspend fun getUserWithPreferences(userId: String): List<Any>?
}
