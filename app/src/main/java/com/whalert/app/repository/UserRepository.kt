package com.whalert.app.repository

import com.whalert.app.data.UserDao
import com.whalert.app.model.AuthResponse
import com.whalert.app.model.AuthState
import com.whalert.app.model.ConnectionStatus
import com.whalert.app.model.DarkMode
import com.whalert.app.model.LoginRequest
import com.whalert.app.model.RegisterRequest
import com.whalert.app.model.User
import com.whalert.app.model.UserPreferences
import com.whalert.app.util.PhoneNumberValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing user authentication and data
 */
@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    // Current authentication state
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Current user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Connection status
    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    init {
        // Load initial auth state
        loadAuthState()
    }

    /**
     * Loads the authentication state from persistent storage
     */
    private suspend fun loadAuthState() = withContext(Dispatchers.IO) {
        try {
            // In a real app, this would load from secure storage
            // For now, we'll just check if there's a logged-in user
            val user = getCurrentUser()
            _currentUser.value = user
            
            _authState.value = AuthState(
                isAuthenticated = user != null,
                userId = user?.uid,
                email = user?.email,
                whatsappNumber = user?.whatsappNumber,
                whatsappConnected = user?.whatsappConnected ?: false,
                connectionStatus = if (user?.whatsappConnected == true) {
                    ConnectionStatus.CONNECTED
                } else {
                    ConnectionStatus.DISCONNECTED
                }
            )
        } catch (e: Exception) {
            _authState.value = AuthState()
        }
    }

    /**
     * Registers a new user
     */
    suspend fun register(request: RegisterRequest): Result<AuthResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Validate email
            if (request.email.isBlank()) {
                return@withContext Result.failure(ValidationException("Email is required"))
            }

            // Validate password (simplified validation)
            if (request.password.isBlank()) {
                return@withContext Result.failure(ValidationException("Password is required"))
            }

            if (request.password.length < 8) {
                return@withContext Result.failure(ValidationException("Password must be at least 8 characters"))
            }

            // Check if user already exists
            val existingUser = userDao.getUserByEmail(request.email)
            if (existingUser != null) {
                return@withContext Result.failure(EmailAlreadyExistsException("Email already registered"))
            }

            // Create new user
            val user = User(
                uid = generateUserId(),
                email = request.email,
                displayName = request.displayName,
                whatsappNumber = null,
                whatsappConnected = false,
                createdAt = Date(),
                updatedAt = Date()
            )

            userDao.insertUser(user)

            // Create default preferences
            val preferences = UserPreferences(
                userId = user.uid,
                notificationsEnabled = true,
                soundEnabled = true,
                vibrationEnabled = true,
                darkMode = DarkMode.SYSTEM,
                language = "fr",
                dailyLimit = 3,
                lastUpdated = Date()
            )

            userDao.insertPreferences(preferences)

            // Update auth state
            _currentUser.value = user
            _authState.value = AuthState(
                isAuthenticated = true,
                userId = user.uid,
                email = user.email,
                whatsappNumber = null,
                whatsappConnected = false,
                connectionStatus = ConnectionStatus.DISCONNECTED
            )

            Result.success(AuthResponse(
                success = true,
                userId = user.uid,
                email = user.email,
                message = "Registration successful"
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logs in a user
     */
    suspend fun login(request: LoginRequest): Result<AuthResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Validate inputs
            if (request.email.isBlank()) {
                return@withContext Result.failure(ValidationException("Email is required"))
            }

            if (request.password.isBlank()) {
                return@withContext Result.failure(ValidationException("Password is required"))
            }

            // In a real app, we would verify the password
            // For this demo, we'll just check if the user exists
            val user = userDao.getUserByEmail(request.email)
                ?: return@withContext Result.failure(InvalidCredentialsException("Invalid email or password"))

            // Update auth state
            _currentUser.value = user
            _authState.value = AuthState(
                isAuthenticated = true,
                userId = user.uid,
                email = user.email,
                whatsappNumber = user.whatsappNumber,
                whatsappConnected = user.whatsappConnected,
                connectionStatus = if (user.whatsappConnected) {
                    ConnectionStatus.CONNECTED
                } else {
                    ConnectionStatus.DISCONNECTED
                }
            )

            Result.success(AuthResponse(
                success = true,
                userId = user.uid,
                email = user.email,
                message = "Login successful"
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logs out the current user
     */
    suspend fun logout(): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            _currentUser.value = null
            _authState.value = AuthState()
            _connectionStatus.value = ConnectionStatus.DISCONNECTED

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Connects WhatsApp account
     * Note: This does NOT use any WhatsApp API or private endpoints
     * It only stores the user's WhatsApp number for reference
     */
    suspend fun connectWhatsApp(phoneNumber: String): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Validate phone number
            if (!PhoneNumberValidator.isValid(phoneNumber)) {
                return@withContext Result.failure(ValidationException("Invalid phone number format"))
            }

            val cleanedPhone = PhoneNumberValidator.cleanToE164(phoneNumber)
                ?: return@withContext Result.failure(ValidationException("Could not format phone number"))

            val userId = getCurrentUserId()
                ?: return@withContext Result.failure(NotAuthenticatedException("User not authenticated"))

            // Update user
            val user = userDao.getUserById(userId)
                ?: return@withContext Result.failure(UserNotFoundException("User not found"))

            val updatedUser = user.copy(
                whatsappNumber = cleanedPhone,
                whatsappConnected = true,
                whatsappConnectionDate = Date(),
                updatedAt = Date()
            )

            userDao.updateUser(updatedUser)

            // Update current user and auth state
            _currentUser.value = updatedUser
            _authState.value = _authState.value.copy(
                whatsappNumber = cleanedPhone,
                whatsappConnected = true
            )
            _connectionStatus.value = ConnectionStatus.CONNECTED

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Disconnects WhatsApp account
     */
    suspend fun disconnectWhatsApp(): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            val userId = getCurrentUserId()
                ?: return@withContext Result.failure(NotAuthenticatedException("User not authenticated"))

            val user = userDao.getUserById(userId)
                ?: return@withContext Result.failure(UserNotFoundException("User not found"))

            val updatedUser = user.copy(
                whatsappNumber = null,
                whatsappConnected = false,
                whatsappConnectionDate = null,
                updatedAt = Date()
            )

            userDao.updateUser(updatedUser)

            // Update current user and auth state
            _currentUser.value = updatedUser
            _authState.value = _authState.value.copy(
                whatsappNumber = null,
                whatsappConnected = false
            )
            _connectionStatus.value = ConnectionStatus.DISCONNECTED

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets the current user
     */
    suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
        return@withContext _currentUser.value
    }

    /**
     * Gets the current user ID
     */
    fun getCurrentUserId(): String? {
        return _currentUser.value?.uid
    }

    /**
     * Gets user by ID
     */
    suspend fun getUserById(userId: String): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            val user = userDao.getUserById(userId)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(UserNotFoundException("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Updates user profile
     */
    suspend fun updateProfile(
        displayName: String? = null,
        email: String? = null
    ): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            val userId = getCurrentUserId()
                ?: return@withContext Result.failure(NotAuthenticatedException("User not authenticated"))

            val user = userDao.getUserById(userId)
                ?: return@withContext Result.failure(UserNotFoundException("User not found"))

            val updatedUser = user.copy(
                displayName = displayName ?: user.displayName,
                email = email ?: user.email,
                updatedAt = Date()
            )

            userDao.updateUser(updatedUser)

            // Update current user
            _currentUser.value = updatedUser

            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets user preferences
     */
    suspend fun getPreferences(): Result<UserPreferences> = withContext(Dispatchers.IO) {
        return@withContext try {
            val userId = getCurrentUserId()
                ?: return@withContext Result.failure(NotAuthenticatedException("User not authenticated"))

            val preferences = userDao.getPreferences(userId)
                ?: UserPreferences(userId = userId)

            Result.success(preferences)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Updates user preferences
     */
    suspend fun updatePreferences(
        notificationsEnabled: Boolean? = null,
        soundEnabled: Boolean? = null,
        vibrationEnabled: Boolean? = null,
        darkMode: DarkMode? = null,
        language: String? = null,
        dailyLimit: Int? = null
    ): Result<UserPreferences> = withContext(Dispatchers.IO) {
        return@withContext try {
            val userId = getCurrentUserId()
                ?: return@withContext Result.failure(NotAuthenticatedException("User not authenticated"))

            val preferences = userDao.getPreferences(userId)
                ?: UserPreferences(userId = userId)

            val updatedPreferences = preferences.copy(
                notificationsEnabled = notificationsEnabled ?: preferences.notificationsEnabled,
                soundEnabled = soundEnabled ?: preferences.soundEnabled,
                vibrationEnabled = vibrationEnabled ?: preferences.vibrationEnabled,
                darkMode = darkMode ?: preferences.darkMode,
                language = language ?: preferences.language,
                dailyLimit = dailyLimit ?: preferences.dailyLimit,
                lastUpdated = Date()
            )

            userDao.updatePreferences(updatedPreferences)

            Result.success(updatedPreferences)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets daily report count for the current user
     */
    suspend fun getDailyReportCount(userId: String): Int {
        return userDao.getDailyReportCount(userId) ?: 0
    }

    /**
     * Increments daily report count for the current user
     */
    suspend fun incrementDailyReportCount(userId: String): Int {
        return userDao.incrementDailyReportCount(userId)
    }

    /**
     * Resets daily report count for the current user
     */
    suspend fun resetDailyReportCount(userId: String): Int {
        return userDao.resetDailyReportCount(userId)
    }

    /**
     * Checks if daily limit is reached
     */
    suspend fun isDailyLimitReached(userId: String): Boolean {
        val count = getDailyReportCount(userId)
        val preferences = userDao.getPreferences(userId)
        val limit = preferences?.dailyLimit ?: 3
        return count >= limit
    }

    /**
     * Gets all users (for admin console)
     */
    suspend fun getAllUsers(): Result<List<User>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val users = userDao.getAllUsers()
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets user count (for admin console)
     */
    suspend fun getUserCount(): Result<Int> = withContext(Dispatchers.IO) {
        return@withContext try {
            val count = userDao.getUserCount()
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generates a unique user ID
     */
    private fun generateUserId(): String {
        return java.util.UUID.randomUUID().toString()
    }

    // Exception classes
    class ValidationException(message: String) : Exception(message)
    class EmailAlreadyExistsException(message: String) : Exception(message)
    class InvalidCredentialsException(message: String) : Exception(message)
    class NotAuthenticatedException(message: String) : Exception(message)
    class UserNotFoundException(message: String) : Exception(message)
}
