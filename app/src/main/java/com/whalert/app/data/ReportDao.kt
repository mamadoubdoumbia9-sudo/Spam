package com.whalert.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.whalert.app.model.Report
import com.whalert.app.model.ReportCategory
import com.whalert.app.model.ReportStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Data Access Object for Report entities
 */
@Dao
interface ReportDao {

    // Create operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: Report): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReports(reports: List<Report>): List<Long>

    // Read operations
    @Query("SELECT * FROM reports WHERE id = :reportId")
    suspend fun getReportById(reportId: Long): Report?

    @Query("SELECT * FROM reports WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getReportsByUserId(userId: String): List<Report>

    @Query("SELECT * FROM reports WHERE userId = :userId AND status = :status ORDER BY createdAt DESC")
    suspend fun getReportsByUserIdAndStatus(userId: String, status: ReportStatus): List<Report>

    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    suspend fun getAllReports(): List<Report>

    @Query("SELECT * FROM reports WHERE status = :status ORDER BY createdAt DESC")
    suspend fun getReportsByStatus(status: ReportStatus): List<Report>

    @Query("SELECT * FROM reports WHERE category = :category ORDER BY createdAt DESC")
    suspend fun getReportsByCategory(category: ReportCategory): List<Report>

    @Query("SELECT * FROM reports WHERE isSynced = 0")
    suspend fun getUnsyncedReports(): List<Report>

    @Query("SELECT * FROM reports WHERE createdAt >= :startDate AND createdAt <= :endDate ORDER BY createdAt DESC")
    suspend fun getReportsByDateRange(startDate: Date, endDate: Date): List<Report>

    @Query("SELECT * FROM reports WHERE userId = :userId AND createdAt >= :startDate ORDER BY createdAt DESC")
    suspend fun getReportsByUserAndDate(userId: String, startDate: Date): List<Report>

    // Update operations
    @Update
    suspend fun updateReport(report: Report): Int

    @Update
    suspend fun updateAllReports(reports: List<Report>): Int

    @Query("""
        UPDATE reports 
        SET status = :status, 
            updatedAt = CURRENT_TIMESTAMP
        WHERE id = :reportId
    """)
    suspend fun updateReportStatus(reportId: Long, status: ReportStatus): Int

    @Query("""
        UPDATE reports 
        SET status = :status,
            confirmationDate = :confirmationDate,
            confirmationReceived = :confirmationReceived,
            confirmationDetails = :confirmationDetails,
            updatedAt = CURRENT_TIMESTAMP
        WHERE id = :reportId
    """)
    suspend fun updateReportConfirmation(
        reportId: Long,
        status: ReportStatus,
        confirmationDate: Date? = null,
        confirmationReceived: Boolean = false,
        confirmationDetails: String? = null
    ): Int

    @Query("""
        UPDATE reports 
        SET isSynced = 1,
            syncTimestamp = CURRENT_TIMESTAMP
        WHERE id = :reportId
    """)
    suspend fun markReportAsSynced(reportId: Long): Int

    @Query("""
        UPDATE reports 
        SET isSynced = 1,
            syncTimestamp = CURRENT_TIMESTAMP
        WHERE id IN (:reportIds)
    """)
    suspend fun markReportsAsSynced(reportIds: List<Long>): Int

    // Delete operations
    @Query("DELETE FROM reports WHERE id = :reportId")
    suspend fun deleteReport(reportId: Long): Int

    @Query("DELETE FROM reports WHERE userId = :userId")
    suspend fun deleteReportsByUserId(userId: String): Int

    @Query("DELETE FROM reports WHERE id IN (:reportIds)")
    suspend fun deleteReports(reportIds: List<Long>): Int

    // Count operations
    @Query("SELECT COUNT(*) FROM reports")
    suspend fun getReportCount(): Long

    @Query("SELECT COUNT(*) FROM reports WHERE userId = :userId")
    suspend fun getReportCountByUserId(userId: String): Long

    @Query("SELECT COUNT(*) FROM reports WHERE status = :status")
    suspend fun getReportCountByStatus(status: ReportStatus): Long

    @Query("SELECT COUNT(*) FROM reports WHERE category = :category")
    suspend fun getReportCountByCategory(category: ReportCategory): Long

    @Query("SELECT COUNT(*) FROM reports WHERE userId = :userId AND createdAt >= :startDate")
    suspend fun getDailyReportCount(userId: String, startDate: Date): Long

    // Flow operations for live updates
    @Query("SELECT * FROM reports WHERE userId = :userId ORDER BY createdAt DESC")
    fun getReportsByUserIdFlow(userId: String): Flow<List<Report>>

    @Query("SELECT * FROM reports WHERE isSynced = 0")
    fun getUnsyncedReportsFlow(): Flow<List<Report>>

    // Statistics operations
    @Query("""
        SELECT 
            COUNT(*) as total,
            SUM(CASE WHEN status = 'DRAFT' THEN 1 ELSE 0 END) as draft_count,
            SUM(CASE WHEN status = 'PREPARED' THEN 1 ELSE 0 END) as prepared_count,
            SUM(CASE WHEN status = 'SUBMITTED' THEN 1 ELSE 0 END) as submitted_count,
            SUM(CASE WHEN status = 'CONFIRMATION_RECEIVED' THEN 1 ELSE 0 END) as confirmed_count,
            SUM(CASE WHEN status = 'FOLLOW_UP_NEEDED' THEN 1 ELSE 0 END) as follow_up_count,
            SUM(CASE WHEN status = 'CLOSED' THEN 1 ELSE 0 END) as closed_count
        FROM reports
        WHERE userId = :userId
    """)
    suspend fun getUserReportStatistics(userId: String): Map<String, Long>

    @Query("""
        SELECT 
            COUNT(*) as total,
            SUM(CASE WHEN category = 'SPAM' THEN 1 ELSE 0 END) as spam_count,
            SUM(CASE WHEN category = 'SCAM' THEN 1 ELSE 0 END) as scam_count,
            SUM(CASE WHEN category = 'IMPERSONATION' THEN 1 ELSE 0 END) as impersonation_count,
            SUM(CASE WHEN category = 'HARASSMENT' THEN 1 ELSE 0 END) as harassment_count,
            SUM(CASE WHEN category = 'FRAUD' THEN 1 ELSE 0 END) as fraud_count,
            SUM(CASE WHEN category = 'MALICIOUS_BEHAVIOR' THEN 1 ELSE 0 END) as malicious_count,
            SUM(CASE WHEN category = 'OTHER' THEN 1 ELSE 0 END) as other_count
        FROM reports
        WHERE userId = :userId
    """)
    suspend fun getUserReportCategoryStatistics(userId: String): Map<String, Long>

    // Search operations
    @Query("""
        SELECT * FROM reports 
        WHERE userId = :userId AND 
            (phoneNumber LIKE '%' || :query || '%' OR
             description LIKE '%' || :query || '%' OR
             category LIKE '%' || :query || '%')
        ORDER BY createdAt DESC
    """)
    suspend fun searchReports(userId: String, query: String): List<Report>
}
