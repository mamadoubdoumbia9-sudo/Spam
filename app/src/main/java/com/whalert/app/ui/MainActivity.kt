package com.whalert.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.whalert.app.ui.console.ConsoleScreen
import com.whalert.app.ui.guide.SecurityGuideScreen
import com.whalert.app.ui.history.ReportHistoryScreen
import com.whalert.app.ui.home.HomeScreen
import com.whalert.app.ui.onboarding.OnboardingScreen
import com.whalert.app.ui.report.ReportCreationScreen
import com.whalert.app.ui.report.ReportDetailScreen
import com.whalert.app.ui.report.ReportVerificationScreen
import com.whalert.app.ui.settings.SettingsScreen
import com.whalert.app.ui.splash.SplashScreen
import com.whalert.app.ui.theme.WhAlertTheme
import com.whalert.app.ui.transparency.TransparencyScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for WhAlert application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            WhAlertTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        // Splash
                        composable("splash") {
                            SplashScreen(navController)
                        }
                        
                        // Onboarding
                        composable("onboarding") {
                            OnboardingScreen(navController)
                        }
                        
                        // Main screens
                        composable("home") {
                            HomeScreen(navController)
                        }
                        
                        // Report screens
                        composable("report/creation") {
                            ReportCreationScreen(navController)
                        }
                        
                        composable("report/verification/{reportId}") { backStackEntry ->
                            val reportId = backStackEntry.arguments?.getString("reportId")?.toLongOrNull()
                            ReportVerificationScreen(navController, reportId)
                        }
                        
                        composable("report/detail/{reportId}") { backStackEntry ->
                            val reportId = backStackEntry.arguments?.getString("reportId")?.toLongOrNull()
                            ReportDetailScreen(navController, reportId)
                        }
                        
                        // History
                        composable("history") {
                            ReportHistoryScreen(navController)
                        }
                        
                        // Guide
                        composable("guide") {
                            SecurityGuideScreen(navController)
                        }
                        
                        // Settings
                        composable("settings") {
                            SettingsScreen(navController)
                        }
                        
                        // Transparency
                        composable("transparency") {
                            TransparencyScreen(navController)
                        }
                        
                        // Console
                        composable("console") {
                            ConsoleScreen(navController)
                        }
                    }
                }
            }
        }
    }
}
