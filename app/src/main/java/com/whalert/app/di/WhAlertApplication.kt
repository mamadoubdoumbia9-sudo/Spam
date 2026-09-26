package com.whalert.app.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Custom Application class for WhAlert
 */
@HiltAndroidApp
class WhAlertApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize application
        initializeApp()
    }

    private fun initializeApp() {
        // In a real app, you would initialize various components here
        // For example:
        // - Analytics
        // - Crash reporting
        // - Logging
        // - Network monitoring
        
        println("WhAlert Application initialized")
    }
}
