package com.whalert.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whalert.app.model.AuthResponse
import com.whalert.app.model.ConnectionStatus
import com.whalert.app.model.DarkMode
import com.whalert.app.model.LoginRequest
import com.whalert.app.model.RegisterRequest
import com.whalert.app.model.User
import com.whalert.app.model.UserPreferences
import com.whalert.app.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing user authentication and profile
 */
@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // State
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // Login form
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    // Registration form
    private val _registerEmail = MutableStateFlow("")
    val registerEmail: StateFlow<String> = _registerEmail.asStateFlow()

    private val _registerPassword = MutableStateFlow("")
    val registerPassword: StateFlow<String> = _registerPassword.asStateFlow()

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    // WhatsApp connection
    private val _whatsappNumber = MutableStateFlow("")
    val whatsappNumber: StateFlow<String> = _whatsappNumber.asStateFlow()

    // Preferences
    private val _preferences = MutableStateFlow<UserPreferences?>(null)
    val preferences: StateFlow<UserPreferences?> = _preferences.asStateFlow()

    init {
        loadAuthState()
    }

    /**
     * Loads authentication state
     */
    private fun loadAuthState() {
        viewModelScope.launch {
            try {
                val user = userRepository.getCurrentUser()
                _currentUser.value = user
                _isAuthenticated.value = user != null
                
                if (user != null) {
                    _connectionStatus.value = if (user.whatsappConnected) {
                        ConnectionStatus.CONNECTED
                    } else {
                        ConnectionStatus.DISCONNECTED
                    }
                }
                
                loadPreferences()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur lors du chargement de l'état"
            }
        }
    }

    /**
     * Loads user preferences
     */
    private fun loadPreferences() {
        viewModelScope.launch {
            try {
                val result = userRepository.getPreferences()
                result.onSuccess { prefs ->
                    _preferences.value = prefs
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur lors du chargement des préférences"
            }
        }
    }

    /**
     * Sets email for login
     */
    fun setEmail(email: String) {
        _email.value = email
    }

    /**
     * Sets password for login
     */
    fun setPassword(password: String) {
        _password.value = password
    }

    /**
     * Sets email for registration
     */
    fun setRegisterEmail(email: String) {
        _registerEmail.value = email
    }

    /**
     * Sets password for registration
     */
    fun setRegisterPassword(password: String) {
        _registerPassword.value = password
    }

    /**
     * Sets display name for registration
     */
    fun setDisplayName(displayName: String) {
        _displayName.value = displayName
    }

    /**
     * Sets WhatsApp number
     */
    fun setWhatsAppNumber(number: String) {
        _whatsappNumber.value = number
    }

    /**
     * Registers a new user
     */
    fun register(): Result<AuthResponse> {
        var result: Result<AuthResponse> = Result.failure(Exception("Unknown error"))
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val request = RegisterRequest(
                    email = _registerEmail.value,
                    password = _registerPassword.value,
                    displayName = _displayName.value.takeIf { it.isNotBlank() }
                )
                
                result = userRepository.register(request)
                result.onSuccess { response ->
                    _successMessage.value = response.message ?: "Inscription réussie"
                    _isAuthenticated.value = true
                    loadAuthState()
                    
                    // Clear registration form
                    _registerEmail.value = ""
                    _registerPassword.value = ""
                    _displayName.value = ""
                }.onFailure { e ->
                    _error.value = when (e) {
                        is UserRepository.ValidationException -> e.message
                        is UserRepository.EmailAlreadyExistsException -> e.message
                        else -> e.message ?: "Erreur lors de l'inscription"
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            } finally {
                _isLoading.value = false
            }
        }

        return result
    }

    /**
     * Logs in a user
     */
    fun login(): Result<AuthResponse> {
        var result: Result<AuthResponse> = Result.failure(Exception("Unknown error"))
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val request = LoginRequest(
                    email = _email.value,
                    password = _password.value
                )
                
                result = userRepository.login(request)
                result.onSuccess { response ->
                    _successMessage.value = response.message ?: "Connexion réussie"
                    _isAuthenticated.value = true
                    loadAuthState()
                    
                    // Clear login form
                    _email.value = ""
                    _password.value = ""
                }.onFailure { e ->
                    _error.value = when (e) {
                        is UserRepository.ValidationException -> e.message
                        is UserRepository.InvalidCredentialsException -> e.message
                        else -> e.message ?: "Erreur lors de la connexion"
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            } finally {
                _isLoading.value = false
            }
        }

        return result
    }

    /**
     * Logs out the current user
     */
    fun logout(): Result<Boolean> {
        var result: Result<Boolean> = Result.failure(Exception("Unknown error"))
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                result = userRepository.logout()
                result.onSuccess { success ->
                    if (success) {
                        _successMessage.value = "Déconnexion réussie"
                        _isAuthenticated.value = false
                        _currentUser.value = null
                        _connectionStatus.value = ConnectionStatus.DISCONNECTED
                    }
                }.onFailure { e ->
                    _error.value = e.message ?: "Erreur lors de la déconnexion"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            } finally {
                _isLoading.value = false
            }
        }

        return result
    }

    /**
     * Connects WhatsApp account
     */
    fun connectWhatsApp(): Result<Boolean> {
        var result: Result<Boolean> = Result.failure(Exception("Unknown error"))
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                result = userRepository.connectWhatsApp(_whatsappNumber.value)
                result.onSuccess { success ->
                    if (success) {
                        _successMessage.value = "Compte WhatsApp connecté"
                        _connectionStatus.value = ConnectionStatus.CONNECTED
                        loadAuthState()
                        _whatsappNumber.value = ""
                    }
                }.onFailure { e ->
                    _error.value = when (e) {
                        is UserRepository.ValidationException -> e.message
                        else -> e.message ?: "Erreur lors de la connexion WhatsApp"
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            } finally {
                _isLoading.value = false
            }
        }

        return result
    }

    /**
     * Disconnects WhatsApp account
     */
    fun disconnectWhatsApp(): Result<Boolean> {
        var result: Result<Boolean> = Result.failure(Exception("Unknown error"))
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                result = userRepository.disconnectWhatsApp()
                result.onSuccess { success ->
                    if (success) {
                        _successMessage.value = "Compte WhatsApp déconnecté"
                        _connectionStatus.value = ConnectionStatus.DISCONNECTED
                        loadAuthState()
                    }
                }.onFailure { e ->
                    _error.value = e.message ?: "Erreur lors de la déconnexion WhatsApp"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            } finally {
                _isLoading.value = false
            }
        }

        return result
    }

    /**
     * Updates user profile
     */
    fun updateProfile(
        displayName: String? = null,
        email: String? = null
    ): Result<User> {
        var result: Result<User> = Result.failure(Exception("Unknown error"))
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                result = userRepository.updateProfile(displayName, email)
                result.onSuccess { user ->
                    _currentUser.value = user
                    _successMessage.value = "Profil mis à jour"
                }.onFailure { e ->
                    _error.value = e.message ?: "Erreur lors de la mise à jour du profil"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            } finally {
                _isLoading.value = false
            }
        }

        return result
    }

    /**
     * Updates preferences
     */
    fun updatePreferences(
        notificationsEnabled: Boolean? = null,
        soundEnabled: Boolean? = null,
        vibrationEnabled: Boolean? = null,
        darkMode: DarkMode? = null,
        language: String? = null,
        dailyLimit: Int? = null
    ): Result<UserPreferences> {
        var result: Result<UserPreferences> = Result.failure(Exception("Unknown error"))
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                result = userRepository.updatePreferences(
                    notificationsEnabled,
                    soundEnabled,
                    vibrationEnabled,
                    darkMode,
                    language,
                    dailyLimit
                )
                result.onSuccess { prefs ->
                    _preferences.value = prefs
                    _successMessage.value = "Préférences mises à jour"
                }.onFailure { e ->
                    _error.value = e.message ?: "Erreur lors de la mise à jour des préférences"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            } finally {
                _isLoading.value = false
            }
        }

        return result
    }

    /**
     * Checks if daily limit is reached
     */
    suspend fun isDailyLimitReached(): Boolean {
        val userId = userRepository.getCurrentUserId()
        return userId?.let { uid ->
            userRepository.isDailyLimitReached(uid)
        } ?: true
    }

    /**
     * Gets remaining report count for today
     */
    suspend fun getRemainingReportCount(): Int {
        val userId = userRepository.getCurrentUserId()
        return userId?.let { uid ->
            val count = userRepository.getDailyReportCount(uid)
            val limit = userRepository.getPreferences().getOrNull()?.dailyLimit ?: 3
            maxOf(0, limit - count)
        } ?: 0
    }

    /**
     * Clears error
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Clears success message
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}
