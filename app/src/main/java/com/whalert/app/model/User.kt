package com.whalert.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a user of the WhAlert application
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String,
    val email: String? = null,
    val displayName: String? = null,
    val whatsappNumber: String? = null,
    val whatsappConnected: Boolean = false,
    val whatsappConnectionDate: Date? = null,
    val dailyReportCount: Int = 0,
    val lastReportDate: Date? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

/**
 * User preferences and settings
 */
@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey val userId: String,
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val darkMode: DarkMode = DarkMode.SYSTEM,
    val language: String = "fr",
    val dailyLimit: Int = Report.DAILY_LIMIT,
    val lastUpdated: Date = Date()
)

/**
 * Dark mode settings
 */
enum class DarkMode {
    LIGHT,
    DARK,
    SYSTEM;
}

/**
 * User authentication state
 */
data class AuthState(
    val isAuthenticated: Boolean = false,
    val userId: String? = null,
    val email: String? = null,
    val whatsappNumber: String? = null,
    val whatsappConnected: Boolean = false,
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED
)

/**
 * Console authentication credentials
 */
@Entity(tableName = "console_credentials")
data class ConsoleCredentials(
    @PrimaryKey val id: Int = 1,
    val pinHash: String? = null,
    val biometricEnabled: Boolean = false,
    val lastAccess: Date? = null,
    val accessCount: Int = 0,
    val createdAt: Date = Date()
)

/**
 * DTO for user login
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * DTO for user registration
 */
data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String? = null
)

/**
 * Response for authentication operations
 */
data class AuthResponse(
    val success: Boolean,
    val userId: String? = null,
    val email: String? = null,
    val message: String? = null,
    val error: String? = null
)
