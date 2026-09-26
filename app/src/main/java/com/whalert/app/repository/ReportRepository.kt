package com.whalert.app.repository

import com.whalert.app.data.ReportDao
import com.whalert.app.model.Report
import com.whalert.app.model.ReportCategory
import com.whalert.app.model.ReportCreateRequest
import com.whalert.app.model.ReportStatus
import com.whalert.app.model.ReportSubmissionResponse
import com.whalert.app.model.ReportUpdateRequest
import com.whalert.app.model.TransmissionStatus
import com.whalert.app.util.PhoneNumberValidator
import com.whalert.app.util.ValidationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing reports
 */
@Singleton
class ReportRepository @Inject constructor(
    private val reportDao: ReportDao,
    private val userRepository: UserRepository
) {

    /**
     * Creates a new report
     */
    suspend fun createReport(request: ReportCreateRequest): Result<Report> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Validate the request
            val validation = ValidationUtils.validateReportRequest(request)
            if (!validation.isValid) {
                return@withContext Result.failure(ValidationException(validation.errors.joinToString(", ")))
            }

            // Check daily limit
            val userId = userRepository.getCurrentUserId()
            userId?.let { uid ->
                val dailyCount = userRepository.getDailyReportCount(uid)
                if (dailyCount >= Report.DAILY_LIMIT) {
                    return@withContext Result.failure(
                        DailyLimitException("Limite quotidienne atteinte: ${Report.DAILY_LIMIT} signalements max")
                    )
                }
            }

            // Clean phone number
            val cleanedPhone = PhoneNumberValidator.cleanToE164(request.phoneNumber)
                ?: throw ValidationException("Impossible de formater le numéro de téléphone")

            val countryCode = PhoneNumberValidator.extractCountryCode(cleanedPhone)
                ?: throw ValidationException("Impossible de déterminer le code pays")

            // Create the report
            val report = Report(
                userId = userId,
                phoneNumber = request.phoneNumber,
                countryCode = countryCode,
                fullPhoneNumber = cleanedPhone,
                category = request.category,
                description = request.description,
                evidenceText = request.evidenceText,
                evidenceScreenshots = request.evidenceScreenshots,
                incidentDate = request.incidentDate ?: Date(),
                additionalInfo = request.additionalInfo,
                status = ReportStatus.DRAFT,
                createdAt = Date(),
                updatedAt = Date()
            )

            val reportId = reportDao.insertReport(report)
            
            // Increment daily count
            userId?.let { uid ->
                userRepository.incrementDailyReportCount(uid)
            }

            // Return the report with ID
            Result.success(report.copy(id = reportId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Updates an existing report
     */
    suspend fun updateReport(report: Report): Result<Report> = withContext(Dispatchers.IO) {
        return@withContext try {
            val updatedRows = reportDao.updateReport(report)
            if (updatedRows > 0) {
                Result.success(report)
            } else {
                Result.failure(ReportNotFoundException("Report not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Updates report status
     */
    suspend fun updateReportStatus(request: ReportUpdateRequest): Result<Report> = withContext(Dispatchers.IO) {
        return@withContext try {
            val report = reportDao.getReportById(request.id)
                ?: return@withContext Result.failure(ReportNotFoundException("Report not found"))

            val updatedReport = report.copy(
                status = request.status,
                confirmationReceived = request.confirmationReceived ?: report.confirmationReceived,
                confirmationDetails = request.confirmationDetails ?: report.confirmationDetails,
                confirmationDate = request.confirmationDate ?: report.confirmationDate,
                followUpNeeded = request.followUpNeeded ?: report.followUpNeeded,
                closedDate = request.closedDate ?: report.closedDate,
                closedReason = request.closedReason ?: report.closedReason,
                updatedAt = Date()
            )

            val updatedRows = reportDao.updateReport(updatedReport)
            if (updatedRows > 0) {
                Result.success(updatedReport)
            } else {
                Result.failure(ReportNotFoundException("Report not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets a report by ID
     */
    suspend fun getReportById(reportId: Long): Result<Report> = withContext(Dispatchers.IO) {
        return@withContext try {
            val report = reportDao.getReportById(reportId)
            if (report != null) {
                Result.success(report)
            } else {
                Result.failure(ReportNotFoundException("Report not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets all reports for the current user
     */
    suspend fun getUserReports(): Result<List<Report>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val userId = userRepository.getCurrentUserId()
            if (userId == null) {
                Result.success(emptyList())
            } else {
                val reports = reportDao.getReportsByUserId(userId)
                Result.success(reports)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets reports by status for the current user
     */
    suspend fun getUserReportsByStatus(status: ReportStatus): Result<List<Report>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val userId = userRepository.getCurrentUserId()
            if (userId == null) {
                Result.success(emptyList())
            } else {
                val reports = reportDao.getReportsByUserIdAndStatus(userId, status)
                Result.success(reports)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Searches reports for the current user
     */
    suspend fun searchReports(query: String): Result<List<Report>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val userId = userRepository.getCurrentUserId()
            if (userId == null) {
                Result.success(emptyList())
            } else {
                val reports = reportDao.searchReports(userId, query)
                Result.success(reports)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deletes a report
     */
    suspend fun deleteReport(reportId: Long): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            val deletedRows = reportDao.deleteReport(reportId)
            Result.success(deletedRows > 0)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Submits a report (marks as submitted and attempts transmission)
     */
    suspend fun submitReport(reportId: Long): Result<ReportSubmissionResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val report = reportDao.getReportById(reportId)
                ?: return@withContext Result.failure(ReportNotFoundException("Report not found"))

            // Update report status
            val updatedReport = report.copy(
                status = ReportStatus.SUBMITTED,
                submissionDate = Date(),
                updatedAt = Date()
            )

            reportDao.updateReport(updatedReport)

            // Attempt transmission (this will use the actual transmission mechanism)
            val transmissionResult = transmitReport(updatedReport)

            Result.success(transmissionResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Transmits a report to the official channel
     * This is the actual transmission mechanism that respects WhatsApp's official procedures
     */
    private suspend fun transmitReport(report: Report): ReportSubmissionResponse {
        // IMPORTANT: This application does NOT have access to WhatsApp's private APIs
        // We can only use officially available mechanisms
        
        // The official mechanism for reporting a WhatsApp account is:
        // 1. Open the chat with the contact in WhatsApp
        // 2. Tap the three dots menu
        // 3. Select "Report" or "Signaler"
        // 4. Follow the official WhatsApp reporting flow
        
        // Since we cannot automate this (and should not), we provide:
        // 1. A way to generate the report text
        // 2. Instructions on how to use WhatsApp's official reporting
        // 3. Optional: Send via email if the user has configured it
        
        // Generate the report text
        val reportText = generateReportText(report)
        
        // For now, we mark it as sent from the app
        // The user will need to manually complete the official reporting process
        return ReportSubmissionResponse(
            success = true,
            reportId = report.id,
            transmissionStatus = TransmissionStatus.SENT_FROM_APP,
            message = "Signalement préparé. Pour compléter le signalement, ouvrez WhatsApp, trouvez la conversation avec ${report.fullPhoneNumber}, appuyez sur les trois points et sélectionnez 'Signaler'.",
            timestamp = Date()
        )
    }

    /**
     * Generates the text for the report based on the report data
     */
    fun generateReportText(report: Report): String {
        val countryName = PhoneNumberValidator.getCountryName(report.countryCode)
        val formattedDate = report.incidentDate?.let { 
            java.text.SimpleDateFormat.getDateTimeInstance().format(it) 
        } ?: "Non spécifiée"

        val evidenceText = report.evidenceText?.takeIf { it.isNotBlank() } 
            ?: "Aucun texte de preuve fourni"

        val screenshotsCount = report.evidenceScreenshots?.size ?: 0

        return """
Signalement WhAlert

Numéro concerné : ${report.fullPhoneNumber} ($countryName)

Motif : ${report.category.getDisplayName()}

Résumé des faits :
${report.description}

Date(s) des incidents : $formattedDate

Éléments disponibles :
- Texte : $evidenceText
- Captures d'écran : $screenshotsCount
${report.additionalInfo?.takeIf { it.isNotBlank() }?.let { "- Informations complémentaires : $it" } ?: ""}

Demande :
Je demande à WhatsApp d'examiner les éléments transmis conformément à ses procédures et à ses conditions d'utilisation.

---
Ce signalement a été préparé via l'application WhAlert. Pour le soumettre officiellement, veuillez utiliser le mécanisme de signalement intégré à WhatsApp.
        """.trimIndent()
    }

    /**
     * Gets report statistics for the current user
     */
    suspend fun getUserReportStatistics(): Result<Map<String, Long>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val userId = userRepository.getCurrentUserId()
            if (userId == null) {
                Result.success(emptyMap())
            } else {
                val stats = reportDao.getUserReportStatistics(userId)
                Result.success(stats)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets category statistics for the current user
     */
    suspend fun getUserCategoryStatistics(): Result<Map<String, Long>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val userId = userRepository.getCurrentUserId()
            if (userId == null) {
                Result.success(emptyMap())
            } else {
                val stats = reportDao.getUserReportCategoryStatistics(userId)
                Result.success(stats)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets the count of reports for the current user
     */
    suspend fun getUserReportCount(): Result<Long> = withContext(Dispatchers.IO) {
        return@withContext try {
            val userId = userRepository.getCurrentUserId()
            if (userId == null) {
                Result.success(0L)
            } else {
                val count = reportDao.getReportCountByUserId(userId)
                Result.success(count)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets unsynced reports
     */
    fun getUnsyncedReportsFlow(): Flow<List<Report>> {
        return reportDao.getUnsyncedReportsFlow()
    }

    /**
     * Marks a report as synced
     */
    suspend fun markReportAsSynced(reportId: Long): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            val updatedRows = reportDao.markReportAsSynced(reportId)
            Result.success(updatedRows > 0)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets all reports (for admin console)
     */
    suspend fun getAllReports(): Result<List<Report>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val reports = reportDao.getAllReports()
            Result.success(reports)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets reports by date range (for admin console)
     */
    suspend fun getReportsByDateRange(startDate: Date, endDate: Date): Result<List<Report>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val reports = reportDao.getReportsByDateRange(startDate, endDate)
            Result.success(reports)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets reports by category (for admin console)
     */
    suspend fun getReportsByCategory(category: ReportCategory): Result<List<Report>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val reports = reportDao.getReportsByCategory(category)
            Result.success(reports)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets reports by status (for admin console)
     */
    suspend fun getReportsByStatus(status: ReportStatus): Result<List<Report>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val reports = reportDao.getReportsByStatus(status)
            Result.success(reports)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Exception classes
    class ReportNotFoundException(message: String) : Exception(message)
    class ValidationException(message: String) : Exception(message)
    class DailyLimitException(message: String) : Exception(message)
}
