package com.whalert.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.whalert.app.model.ConsoleCredentials
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for ConsoleCredentials entities
 */
@Dao
interface ConsoleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredentials(credentials: ConsoleCredentials): Long

    @Update
    suspend fun updateCredentials(credentials: ConsoleCredentials): Int

    @Query("SELECT * FROM console_credentials WHERE id = 1")
    suspend fun getCredentials(): ConsoleCredentials?

    @Query("SELECT * FROM console_credentials WHERE id = 1")
    fun getCredentialsFlow(): Flow<ConsoleCredentials?>

    @Query("DELETE FROM console_credentials WHERE id = 1")
    suspend fun deleteCredentials(): Int

    @Query("""
        UPDATE console_credentials 
        SET accessCount = accessCount + 1,
            lastAccess = CURRENT_TIMESTAMP
        WHERE id = 1
    """)
    suspend fun incrementAccessCount(): Int

    @Query("""
        UPDATE console_credentials 
        SET pinHash = :pinHash,
            biometricEnabled = :biometricEnabled
        WHERE id = 1
    """)
    suspend fun updateConsoleSettings(pinHash: String?, biometricEnabled: Boolean): Int
}
