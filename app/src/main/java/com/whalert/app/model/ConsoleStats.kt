package com.whalert.app.model

import java.util.Date

/**
 * Statistics for the admin console
 */
data class ConsoleStats(
    val totalReports: Long = 0,
    val reportsByStatus: Map<ReportStatus, Long> = emptyMap(),
    val reportsByCategory: Map<ReportCategory, Long> = emptyMap(),
    val totalTransmissions: Long = 0,
    val transmissionErrors: Long = 0,
    val totalUsers: Long = 0,
    val activeUsers: Long = 0,
    val dailyReports: Map<String, Long> = emptyMap(), // Date string -> count
    val weeklyReports: Map<String, Long> = emptyMap(),
    val monthlyReports: Map<String, Long> = emptyMap(),
    val backendStatus: BackendStatus = BackendStatus.UNKNOWN,
    val lastSync: Date? = null,
    val systemHealth: SystemHealth = SystemHealth.GOOD,
    val errorLogCount: Int = 0,
    val warningCount: Int = 0
)

/**
 * Backend service status
 */
enum class BackendStatus {
    ONLINE,
    OFFLINE,
    DEGRADED,
    UNKNOWN;

    fun getDisplayName(): String {
        return when (this) {
            ONLINE -> "En ligne"
            OFFLINE -> "Hors ligne"
            DEGRADED -> "Dégradé"
            UNKNOWN -> "Inconnu"
        }
    }

    fun getColor(): Int {
        return when (this) {
            ONLINE -> android.R.color.holo_green_dark
            OFFLINE -> android.R.color.holo_red_dark
            DEGRADED -> android.R.color.holo_orange_dark
            UNKNOWN -> android.R.color.darker_gray
        }
    }
}

/**
 * Overall system health
 */
enum class SystemHealth {
    GOOD,
    WARNING,
    CRITICAL,
    UNKNOWN;

    fun getDisplayName(): String {
        return when (this) {
            GOOD -> "Bon"
            WARNING -> "Avertissement"
            CRITICAL -> "Critique"
            UNKNOWN -> "Inconnu"
        }
    }

    fun getColor(): Int {
        return when (this) {
            GOOD -> android.R.color.holo_green_dark
            WARNING -> android.R.color.holo_orange_dark
            CRITICAL -> android.R.color.holo_red_dark
            UNKNOWN -> android.R.color.darker_gray
        }
    }
}

/**
 * Log entry for console
 */
data class ConsoleLogEntry(
    val id: String,
    val timestamp: Date = Date(),
    val level: LogLevel,
    val category: String,
    val message: String,
    val details: String? = null,
    val userId: String? = null,
    val reportId: Long? = null
)

/**
 * Log levels
 */
enum class LogLevel {
    DEBUG,
    INFO,
    WARNING,
    ERROR,
    CRITICAL;

    fun getColor(): Int {
        return when (this) {
            DEBUG -> android.R.color.holo_blue_dark
            INFO -> android.R.color.holo_green_dark
            WARNING -> android.R.color.holo_orange_dark
            ERROR -> android.R.color.holo_red_dark
            CRITICAL -> android.R.color.holo_red_light
        }
    }
}

/**
 * Integration status for external services
 */
data class IntegrationStatus(
    val serviceName: String,
    val status: ServiceStatus,
    val lastCheck: Date? = null,
    val lastError: String? = null,
    val version: String? = null
)

/**
 * Service status
 */
enum class ServiceStatus {
    AVAILABLE,
    UNAVAILABLE,
    DEGRADED,
    UNKNOWN;
}

/**
 * Daily limit information
 */
data class DailyLimitInfo(
    val currentCount: Int = 0,
    val maxAllowed: Int = Report.DAILY_LIMIT,
    val resetTime: Date? = null,
    val isLimitReached: Boolean = false
)
