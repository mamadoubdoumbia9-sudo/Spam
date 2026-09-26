package com.whalert.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.whalert.app.util.Converters
import java.util.Date

/**
 * Represents a report made by a user about a suspicious WhatsApp account
 */
@Entity(tableName = "reports")
@TypeConverters(Converters::class)
data class Report(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String? = null,
    val phoneNumber: String,
    val countryCode: String,
    val fullPhoneNumber: String,
    val category: ReportCategory,
    val description: String,
    val evidenceText: String? = null,
    val evidenceScreenshots: List<String>? = null,
    val incidentDate: Date? = null,
    val additionalInfo: String? = null,
    val status: ReportStatus = ReportStatus.DRAFT,
    val submissionDate: Date? = null,
    val confirmationDate: Date? = null,
    val confirmationReceived: Boolean = false,
    val confirmationDetails: String? = null,
    val followUpNeeded: Boolean = false,
    val closedDate: Date? = null,
    val closedReason: String? = null,
    val isSynced: Boolean = false,
    val syncTimestamp: Date? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    companion object {
        // Maximum number of reports per 24 hours (application limit)
        const val DAILY_LIMIT = 3
    }
}

/**
 * Categories for reporting suspicious WhatsApp accounts
 */
enum class ReportCategory {
    SPAM,
    SCAM,
    IMPERSONATION,
    HARASSMENT,
    FRAUD,
    MALICIOUS_BEHAVIOR,
    OTHER;

    fun getDisplayName(): String {
        return when (this) {
            SPAM -> "Spam"
            SCAM -> "Arnaque"
            IMPERSONATION -> "Usurpation d'identité"
            HARASSMENT -> "Harcèlement"
            FRAUD -> "Contenu frauduleux"
            MALICIOUS_BEHAVIOR -> "Comportement malveillant"
            OTHER -> "Autre"
        }
    }

    fun getColorResId(): Int {
        return when (this) {
            SPAM -> android.R.color.holo_orange_dark
            SCAM -> android.R.color.holo_red_dark
            IMPERSONATION -> android.R.color.holo_purple
            HARASSMENT -> android.R.color.holo_red_light
            FRAUD -> android.R.color.holo_orange_light
            MALICIOUS_BEHAVIOR -> android.R.color.holo_orange_dark
            OTHER -> android.R.color.darker_gray
        }
    }
}

/**
 * Status of a report in its lifecycle
 */
enum class ReportStatus {
    DRAFT,
    PREPARED,
    SUBMITTED,
    CONFIRMATION_RECEIVED,
    FOLLOW_UP_NEEDED,
    CLOSED;

    fun getDisplayName(): String {
        return when (this) {
            DRAFT -> "Brouillon"
            PREPARED -> "Préparé"
            SUBMITTED -> "Soumis"
            CONFIRMATION_RECEIVED -> "Confirmation obtenue"
            FOLLOW_UP_NEEDED -> "Suivi nécessaire"
            CLOSED -> "Clôturé"
        }
    }

    fun getDescription(): String {
        return when (this) {
            DRAFT -> "Signalement en cours de création"
            PREPARED -> "Prêt pour vérification"
            SUBMITTED -> "Signalement transmis"
            CONFIRMATION_RECEIVED -> "Confirmation technique reçue"
            FOLLOW_UP_NEEDED -> "Nécessite un suivi"
            CLOSED -> "Signalement clôturé"
        }
    }
}

/**
 * Moderation decision status (from WhatsApp perspective)
 */
enum class ModerationDecision {
    UNKNOWN,
    NO_OFFICIAL_CONFIRMATION,
    OFFICIAL_INFORMATION_AVAILABLE,
    ACTION_SIGNALED_BY_WHATSAPP;

    fun getDisplayName(): String {
        return when (this) {
            UNKNOWN -> "Décision de WhatsApp inconnue"
            NO_OFFICIAL_CONFIRMATION -> "Aucune confirmation officielle"
            OFFICIAL_INFORMATION_AVAILABLE -> "Information officielle disponible"
            ACTION_SIGNALED_BY_WHATSAPP -> "Action signalée par WhatsApp"
        }
    }

    fun getMessage(): String {
        return when (this) {
            UNKNOWN -> "WhatsApp ne fournit pas de confirmation publique permettant à cette application de vérifier directement si ce compte a été suspendu."
            NO_OFFICIAL_CONFIRMATION -> "Aucune confirmation officielle n'est disponible."
            OFFICIAL_INFORMATION_AVAILABLE -> "Information officielle disponible depuis WhatsApp."
            ACTION_SIGNALED_BY_WHATSAPP -> "WhatsApp a signalé qu'une action a été prise."
        }
    }
}

/**
 * Transmission status for a report
 */
enum class TransmissionStatus {
    NOT_SENT,
    SENT_FROM_APP,
    TRANSMITTED_TO_OFFICIAL,
    NO_CONFIRMATION_AVAILABLE;

    fun getDisplayName(): String {
        return when (this) {
            NOT_SENT -> "Non envoyé"
            SENT_FROM_APP -> "Signalement envoyé depuis cette application"
            TRANSMITTED_TO_OFFICIAL -> "Signalement transmis au canal officiel"
            NO_CONFIRMATION_AVAILABLE -> "Aucune confirmation disponible"
        }
    }
}

/**
 * Connection status for WhatsApp integration
 */
enum class ConnectionStatus {
    CONNECTED,
    DISCONNECTED,
    SESSION_EXPIRED,
    CONNECTION_ERROR;

    fun getDisplayName(): String {
        return when (this) {
            CONNECTED -> "Connecté"
            DISCONNECTED -> "Non connecté"
            SESSION_EXPIRED -> "Session expirée"
            CONNECTION_ERROR -> "Erreur de connexion"
        }
    }
}

/**
 * DTO for creating a new report
 */
data class ReportCreateRequest(
    val phoneNumber: String,
    val countryCode: String,
    val category: ReportCategory,
    val description: String,
    val evidenceText: String? = null,
    val evidenceScreenshots: List<String>? = null,
    val incidentDate: Date? = null,
    val additionalInfo: String? = null
)

/**
 * DTO for updating report status
 */
data class ReportUpdateRequest(
    val id: Long,
    val status: ReportStatus,
    val confirmationReceived: Boolean? = null,
    val confirmationDetails: String? = null,
    val followUpNeeded: Boolean? = null,
    val closedDate: Date? = null,
    val closedReason: String? = null
)

/**
 * Response for report submission
 */
data class ReportSubmissionResponse(
    val success: Boolean,
    val reportId: Long? = null,
    val transmissionStatus: TransmissionStatus,
    val message: String? = null,
    val error: String? = null,
    val timestamp: Date = Date()
)
