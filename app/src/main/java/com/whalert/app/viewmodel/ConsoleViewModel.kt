package com.whalert.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whalert.app.model.ConsoleLogEntry
import com.whalert.app.model.ConsoleStats
import com.whalert.app.model.IntegrationStatus
import com.whalert.app.model.LogLevel
import com.whalert.app.repository.ConsoleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel for managing admin console functionality
 */
@HiltViewModel
class ConsoleViewModel @Inject constructor(
    private val consoleRepository: ConsoleRepository
) : ViewModel() {

    // State
    private val _stats = MutableStateFlow<ConsoleStats?>(null)
    val stats: StateFlow<ConsoleStats?> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _integrationStatuses = MutableStateFlow<List<IntegrationStatus>>(emptyList())
    val integrationStatuses: StateFlow<List<IntegrationStatus>> = _integrationStatuses.asStateFlow()

    private val _logs = MutableStateFlow<List<ConsoleLogEntry>>(emptyList())
    val logs: StateFlow<List<ConsoleLogEntry>> = _logs.asStateFlow()

    private val _recentReports = MutableStateFlow<List<com.whalert.app.model.Report>>(emptyList())
    val recentReports: StateFlow<List<com.whalert.app.model.Report>> = _recentReports.asStateFlow()

    // Console login
    private val _pin = MutableStateFlow("")
    val pin: StateFlow<String> = _pin.asStateFlow()

    // New PIN setup
    private val _newPin = MutableStateFlow("")
    val newPin: StateFlow<String> = _newPin.asStateFlow()

    private val _confirmPin = MutableStateFlow("")
    val confirmPin: StateFlow<String> = _confirmPin.asStateFlow()

    private val _biometricEnabled = MutableStateFlow(false)
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled.asStateFlow()

    init {
        checkConsoleSetup()
    }

    /**
     * Checks if console is set up
     */
    private fun checkConsoleSetup() {
        viewModelScope.launch {
            try {
                val result = consoleRepository.isConsoleSetup()
                result.onSuccess { isSetup ->
                    _isAuthenticated.value = isSetup
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur lors de la vérification"
            }
        }
    }

    /**
     * Sets PIN
     */
    fun setPin(pin: String) {
        _pin.value = pin
    }

    /**
     * Sets new PIN
     */
    fun setNewPin(pin: String) {
        _newPin.value = pin
    }

    /**
     * Sets confirm PIN
     */
    fun setConfirmPin(pin: String) {
        _confirmPin.value = pin
    }

    /**
     * Sets biometric enabled
     */
    fun setBiometricEnabled(enabled: Boolean) {
        _biometricEnabled.value = enabled
    }

    /**
     * Authenticates to the console
     */
    fun authenticate(): Result<Boolean> {
        var result: Result<Boolean> = Result.failure(Exception("Unknown error"))
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                result = consoleRepository.authenticate(_pin.value)
                result.onSuccess { success ->
                    if (success) {
                        _isAuthenticated.value = true
                        _successMessage.value = "Authentification réussie"
                        loadStats()
                        loadIntegrationStatuses()
                        loadRecentReports()
                        _pin.value = ""
                    }
                }.onFailure { e ->
                    _error.value = when (e) {
                        is ConsoleRepository.AuthenticationException -> "PIN incorrect"
                        else -> e.message ?: "Erreur d'authentification"
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
     * Sets up console credentials
     */
    fun setupCredentials(): Result<Boolean> {
        if (_newPin.value != _confirmPin.value) {
            _error.value = "Les codes PIN ne correspondent pas"
            return Result.failure(PinMismatchException("PINs do not match"))
        }

        if (_newPin.value.length < 4) {
            _error.value = "Le code PIN doit contenir au moins 4 chiffres"
            return Result.failure(ValidationException("PIN too short"))
        }

        var result: Result<Boolean> = Result.failure(Exception("Unknown error"))
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                result = consoleRepository.setupCredentials(_newPin.value, _biometricEnabled.value)
                result.onSuccess { success ->
                    if (success) {
                        _isAuthenticated.value = true
                        _successMessage.value = "Console configurée avec succès"
                        loadStats()
                        loadIntegrationStatuses()
                        loadRecentReports()
                        
                        // Clear form
                        _newPin.value = ""
                        _confirmPin.value = ""
                        _biometricEnabled.value = false
                    }
                }.onFailure { e ->
                    _error.value = e.message ?: "Erreur lors de la configuration"
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
     * Loads console statistics
     */
    fun loadStats() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                val result = consoleRepository.getConsoleStats()
                result.onSuccess { stats ->
                    _stats.value = stats
                }.onFailure { e ->
                    _error.value = e.message ?: "Erreur lors du chargement des statistiques"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Loads integration statuses
     */
    fun loadIntegrationStatuses() {
        viewModelScope.launch {
            try {
                val result = consoleRepository.getIntegrationStatuses()
                result.onSuccess { statuses ->
                    _integrationStatuses.value = statuses
                }.onFailure { e ->
                    _error.value = e.message ?: "Erreur lors du chargement des intégrations"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            }
        }
    }

    /**
     * Loads recent reports
     */
    fun loadRecentReports() {
        viewModelScope.launch {
            try {
                val result = consoleRepository.getRecentReports(50)
                result.onSuccess { reports ->
                    _recentReports.value = reports
                }.onFailure { e ->
                    _error.value = e.message ?: "Erreur lors du chargement des signalements"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            }
        }
    }

    /**
     * Loads console logs
     */
    fun loadLogs() {
        viewModelScope.launch {
            try {
                val result = consoleRepository.getConsoleLogs(100)
                result.onSuccess { logs ->
                    _logs.value = logs
                }.onFailure { e ->
                    _error.value = e.message ?: "Erreur lors du chargement des journaux"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            }
        }
    }

    /**
     * Adds a log entry
     */
    fun addLogEntry(
        level: LogLevel,
        category: String,
        message: String,
        details: String? = null
    ) {
        viewModelScope.launch {
            try {
                val entry = ConsoleLogEntry(
                    id = java.util.UUID.randomUUID().toString(),
                    timestamp = Date(),
                    level = level,
                    category = category,
                    message = message,
                    details = details
                )
                
                consoleRepository.addLogEntry(entry)
                loadLogs()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur lors de l'ajout du journal"
            }
        }
    }

    /**
     * Resets access count
     */
    fun resetAccessCount(): Result<Boolean> {
        var result: Result<Boolean> = Result.failure(Exception("Unknown error"))
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                result = consoleRepository.resetAccessCount()
                result.onSuccess { success ->
                    if (success) {
                        _successMessage.value = "Compteur d'accès réinitialisé"
                    }
                }.onFailure { e ->
                    _error.value = e.message ?: "Erreur lors de la réinitialisation"
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
     * Logs out from console
     */
    fun logout() {
        _isAuthenticated.value = false
        _stats.value = null
        _recentReports.value = emptyList()
        _logs.value = emptyList()
        _pin.value = ""
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

    // Exception classes
    class PinMismatchException(message: String) : Exception(message)
    class ValidationException(message: String) : Exception(message)
}
