package com.whalert.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.whalert.app.repository.ReportRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Service for syncing reports with the backend
 */
@AndroidEntryPoint
class ReportSyncService : Service() {

    @Inject
    lateinit var reportRepository: ReportRepository

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onCreate() {
        super.onCreate()
        startSync()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startSync()
        return START_STICKY
    }

    /**
     * Starts the sync process
     */
    private fun startSync() {
        serviceScope.launch {
            try {
                // Get unsynced reports
                reportRepository.getUnsyncedReportsFlow().collect { reports ->
                    if (reports.isNotEmpty()) {
                        syncReports(reports)
                    }
                }
            } catch (e: Exception) {
                // Log error
                e.printStackTrace()
            }
        }
    }

    /**
     * Syncs reports with the backend
     */
    private suspend fun syncReports(reports: List<com.whalert.app.model.Report>) {
        // In a real implementation, this would:
        // 1. Send each report to the backend
        // 2. Mark as synced if successful
        // 3. Retry failed reports
        
        // For now, we'll just mark them as synced after a delay
        // This is a placeholder for actual sync logic
        
        reports.forEach { report ->
            try {
                // Simulate network delay
                // Thread.sleep(1000)
                
                // Mark as synced
                reportRepository.markReportAsSynced(report.id)
            } catch (e: Exception) {
                // Log error
                e.printStackTrace()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
