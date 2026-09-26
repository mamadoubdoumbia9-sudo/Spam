package com.whalert.app.repository

import com.whalert.app.data.ConsoleDao
import com.whalert.app.data.ReportDao
import com.whalert.app.data.UserDao
import com.whalert.app.model.ConsoleCredentials
import com.whalert.app.model.ConsoleLogEntry
import com.whalert.app.model.ConsoleStats
import com.whalert.app.model.IntegrationStatus
import com.whalert.app.model.Report
import com.whalert.app.model.ReportCategory
import com.whalert.app.model.ReportStatus
import com.whalert.app.model.ServiceStatus
import com.whalert.app.model.SystemHealth
import com.whalert.app.util.SecurityUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing admin console functionality
 */
@Singleton
class ConsoleRepository @Inject constructor(
    private val consoleDao: ConsoleDao,
    private val reportDao: ReportDao,
    private val userDao: UserDao
) {

    /**
     * Authenticates to the console with a PIN
     */
    suspend fun authenticate(pin: String): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            val credentials = consoleDao.getCredentials()
            
            if (credentials == null) {
                // No credentials set, allow access if this is the first time
                // In a real app, you would need to set up initial credentials
                return@withContext Result.success(true)
            }

            // Verify PIN
            val pinHash = SecurityUtils.hashPin(pin)
            if (credentials.pinHash != pinHash) {
                return@withContext Result.failure(AuthenticationException("PIN incorrect"))
            }

            // Update access count
            consoleDao.incrementAccessCount()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sets up console credentials
     */
    suspend fun setupCredentials(pin: String, biometricEnabled: Boolean = false): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            val pinHash = SecurityUtils.hashPin(pin)
            
            val credentials = ConsoleCredentials(
                id = 1,
                pinHash = pinHash,
                biometricEnabled = biometricEnabled,
                lastAccess = Date(),
                accessCount = 0,
                createdAt = Date()
            )

            consoleDao.insertCredentials(credentials)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Updates console credentials
     */
    suspend fun updateCredentials(
        currentPin: String,
        newPin: String? = null,
        biometricEnabled: Boolean? = null
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            val credentials = consoleDao.getCredentials()
                ?: return@withContext Result.failure(CredentialsNotFoundException("Console credentials not found"))

            // Verify current PIN
            val currentPinHash = SecurityUtils.hashPin(currentPin)
            if (credentials.pinHash != currentPinHash) {
                return@withContext Result.failure(AuthenticationException("Current PIN incorrect"))
            }

            // Update credentials
            val newPinHash = newPin?.let { SecurityUtils.hashPin(it) } ?: credentials.pinHash
            val isBiometricEnabled = biometricEnabled ?: credentials.biometricEnabled

            consoleDao.updateConsoleSettings(newPinHash, isBiometricEnabled)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets console statistics
     */
    suspend fun getConsoleStats(): Result<ConsoleStats> = withContext(Dispatchers.IO) {
        return@withContext try {
            val totalReports = reportDao.getReportCount()
            val totalUsers = userDao.getUserCount()
            val activeUsers = userDao.getConnectedUsersCount()

            // Get reports by status
            val reportsByStatus = ReportStatus.values().associateWith { status ->
                reportDao.getReportCountByStatus(status)
            }

            // Get reports by category
            val reportsByCategory = ReportCategory.values().associateWith { category ->
                reportDao.getReportCountByCategory(category)
            }

            // Get transmission errors (reports that failed to sync)
            val unsyncedReports = reportDao.getUnsyncedReports()
            val transmissionErrors = unsyncedReports.size.toLong()

            // Get daily reports (last 7 days)
            val calendar = java.util.Calendar.getInstance()
            val dailyReports = mutableMapOf<String, Long>()
            
            repeat(7) { i ->
                calendar.add(java.util.Calendar.DAY_OF_MONTH, -1)
                val date = calendar.time
                val dateStr = java.text.SimpleDateFormat.getDateInstance().format(date)
                val count = reportDao.getReportsByDateRange(
                    java.sql.Date(date.time),
                    java.sql.Date(date.time + 24 * 60 * 60 * 1000)
                ).size.toLong()
                dailyReports[dateStr] = count
            }

            // Determine system health
            val systemHealth = when {
                transmissionErrors > 10 -> SystemHealth.CRITICAL
                transmissionErrors > 5 -> SystemHealth.WARNING
                else -> SystemHealth.GOOD
            }

            Result.success(ConsoleStats(
                totalReports = totalReports,
                reportsByStatus = reportsByStatus,
                reportsByCategory = reportsByCategory,
                totalTransmissions = totalReports - unsyncedReports.size,
                transmissionErrors = transmissionErrors,
                totalUsers = totalUsers.toLong(),
                activeUsers = activeUsers.toLong(),
                dailyReports = dailyReports,
                backendStatus = determineBackendStatus(transmissionErrors),
                systemHealth = systemHealth,
                errorLogCount = 0, // Would be populated from actual logs
                warningCount = 0
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Determines backend status based on error count
     */
    private fun determineBackendStatus(transmissionErrors: Long): com.whalert.app.model.BackendStatus {
        return when {
            transmissionErrors > 20 -> com.whalert.app.model.BackendStatus.OFFLINE
            transmissionErrors > 10 -> com.whalert.app.model.BackendStatus.DEGRADED
            else -> com.whalert.app.model.BackendStatus.ONLINE
        }
    }

    /**
     * Gets recent reports for console
     */
    suspend fun getRecentReports(limit: Int = 50): Result<List<Report>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val reports = reportDao.getAllReports()
                .sortedByDescending { it.createdAt }
                .take(limit)
            Result.success(reports)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets integration statuses
     */
    suspend fun getIntegrationStatuses(): Result<List<IntegrationStatus>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val statuses = listOf(
                IntegrationStatus(
                    serviceName = "WhatsApp Official API",
                    status = ServiceStatus.UNAVAILABLE,
                    lastCheck = Date(),
                    lastError = "No official public API available for reporting",
                    version = null
                ),
                IntegrationStatus(
                    serviceName = "Firebase Backend",
                    status = ServiceStatus.AVAILABLE,
                    lastCheck = Date(),
                    lastError = null,
                    version = "32.7.2"
                ),
                IntegrationStatus(
                    serviceName = "Network Connectivity",
                    status = ServiceStatus.AVAILABLE,
                    lastCheck = Date(),
                    lastError = null,
                    version = null
                ),
                IntegrationStatus(
                    serviceName = "Local Database",
                    status = ServiceStatus.AVAILABLE,
                    lastCheck = Date(),
                    lastError = null,
                    version = "2.6.1"
                )
            )
            Result.success(statuses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets console logs
     */
    suspend fun getConsoleLogs(limit: Int = 100): Result<List<ConsoleLogEntry>> = withContext(Dispatchers.IO) {
        return@withContext try {
            // In a real implementation, this would fetch from a logs table
            // For now, return empty list
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Adds a log entry
     */
    suspend fun addLogEntry(entry: ConsoleLogEntry): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            // In a real implementation, this would insert into a logs table
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets credentials flow
     */
    fun getCredentialsFlow(): Flow<ConsoleCredentials?> {
        return consoleDao.getCredentialsFlow()
    }

    /**
     * Checks if console is set up
     */
    suspend fun isConsoleSetup(): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            val credentials = consoleDao.getCredentials()
            Result.success(credentials != null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Resets console access count
     */
    suspend fun resetAccessCount(): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            val credentials = consoleDao.getCredentials()
                ?: return@withContext Result.failure(CredentialsNotFoundException("Console credentials not found"))

            val updatedCredentials = credentials.copy(
                accessCount = 0,
                lastAccess = null
            )

            consoleDao.updateCredentials(updatedCredentials)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Exception classes
    class AuthenticationException(message: String) : Exception(message)
    class CredentialsNotFoundException(message: String) : Exception(message)
}
