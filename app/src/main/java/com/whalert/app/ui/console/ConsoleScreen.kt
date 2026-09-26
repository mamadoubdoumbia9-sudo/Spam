package com.whalert.app.ui.console

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.whalert.app.R
import com.whalert.app.model.BackendStatus
import com.whalert.app.model.Report
import com.whalert.app.model.ReportCategory
import com.whalert.app.model.ReportStatus
import com.whalert.app.model.SystemHealth
import com.whalert.app.ui.theme.WhAlertColors
import com.whalert.app.util.SecurityUtils
import com.whalert.app.viewmodel.ConsoleViewModel
import java.util.Date

/**
 * Screen for admin console
 */
@Composable
fun ConsoleScreen(navController: NavController) {
    val viewModel: ConsoleViewModel = hiltViewModel()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val recentReports by viewModel.recentReports.collectAsState()
    val integrationStatuses by viewModel.integrationStatuses.collectAsState()

    // Login state
    var showLogin by remember { mutableStateOf(false) }
    var showSetup by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.checkConsoleSetup()
        if (isAuthenticated) {
            viewModel.loadStats()
            viewModel.loadIntegrationStatuses()
            viewModel.loadRecentReports()
        }
    }

    if (!isAuthenticated) {
        // Show login or setup
        if (showLogin) {
            ConsoleLoginScreen(
                onLogin = { success ->
                    if (success) {
                        showLogin = false
                    }
                },
                onSetup = { showLogin = false; showSetup = true },
                onBack = { navController.popBackStack() }
            )
        } else if (showSetup) {
            ConsoleSetupScreen(
                onSetup = { success ->
                    if (success) {
                        showSetup = false
                    }
                },
                onLogin = { showSetup = false; showLogin = true },
                onBack = { navController.popBackStack() }
            )
        } else {
            // Check if console is set up
            ConsoleAccessScreen(
                onLogin = { showLogin = true },
                onSetup = { showSetup = true },
                onBack = { navController.popBackStack() }
            )
        }
    } else {
        // Main console screen
        ConsoleMainScreen(
            stats = stats,
            recentReports = recentReports,
            integrationStatuses = integrationStatuses,
            isLoading = isLoading,
            onRefresh = {
                viewModel.loadStats()
                viewModel.loadIntegrationStatuses()
                viewModel.loadRecentReports()
            },
            onLogout = {
                viewModel.logout()
                navController.popBackStack()
            },
            onBack = { navController.popBackStack() },
            error = error,
            successMessage = successMessage,
            onClearError = { viewModel.clearError() },
            onClearSuccess = { viewModel.clearSuccessMessage() }
        )
    }
}

@Composable
fun ConsoleAccessScreen(
    onLogin: () -> Unit,
    onSetup: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Text(
                text = stringResource(R.string.console_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔒",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Accès à la console privée",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cette zone est réservée au propriétaire autorisé de l'application.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Login"
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Se connecter")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSetup,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Setup"
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Configurer la console")
            }
        }
    }
}

@Composable
fun ConsoleLoginScreen(
    onLogin: (Boolean) -> Unit,
    onSetup: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: ConsoleViewModel = hiltViewModel()
    val pin by viewModel.pin.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Text(
                text = stringResource(R.string.console_login_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.console_login_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            androidx.compose.material3.OutlinedTextField(
                value = pin,
                onValueChange = { viewModel.setPin(it) },
                label = { Text(stringResource(R.string.console_pin_hint)) },
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                LinearProgressIndicator()
            } else {
                Button(
                    onClick = {
                        val result = viewModel.authenticate()
                        result.onSuccess { success ->
                            onLogin(success)
                        }
                    },
                    enabled = pin.length >= 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Se connecter")
                }
            }

            if (!error.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onSetup
            ) {
                Text("Configurer la console")
            }
        }
    }
}

@Composable
fun ConsoleSetupScreen(
    onSetup: (Boolean) -> Unit,
    onLogin: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: ConsoleViewModel = hiltViewModel()
    val newPin by viewModel.newPin.collectAsState()
    val confirmPin by viewModel.confirmPin.collectAsState()
    val biometricEnabled by viewModel.biometricEnabled.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Text(
                text = "Configuration de la console",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(androidx.compose.foundation.rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Configurer un code PIN pour accéder à la console privée.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            androidx.compose.material3.OutlinedTextField(
                value = newPin,
                onValueChange = { viewModel.setNewPin(it) },
                label = { Text("Nouveau code PIN") },
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
            )

            androidx.compose.material3.OutlinedTextField(
                value = confirmPin,
                onValueChange = { viewModel.setConfirmPin(it) },
                label = { Text("Confirmer le code PIN") },
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Checkbox(
                    checked = biometricEnabled,
                    onCheckedChange = { viewModel.setBiometricEnabled(it) }
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Activer l'authentification biométrique",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (isLoading) {
                LinearProgressIndicator()
            } else {
                Button(
                    onClick = {
                        val result = viewModel.setupCredentials()
                        result.onSuccess { success ->
                            onSetup(success)
                        }
                    },
                    enabled = newPin.length >= 4 && newPin == confirmPin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Configurer la console")
                }
            }

            if (!error.isNullOrBlank()) {
                Text(
                    text = error ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onLogin
            ) {
                Text("Déjà un compte ? Se connecter")
            }
        }
    }
}

@Composable
fun ConsoleMainScreen(
    stats: com.whalert.app.model.ConsoleStats?,
    recentReports: List<Report>,
    integrationStatuses: List<com.whalert.app.model.IntegrationStatus>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    error: String?,
    successMessage: String?,
    onClearError: () -> Unit,
    onClearSuccess: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        ConsoleHeader(
            onBack = onBack,
            onLogout = onLogout
        )

        // Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Stats overview
            item {
                StatsOverview(stats = stats, isLoading = isLoading)
            }

            // Integration status
            item {
                IntegrationStatusSection(statuses = integrationStatuses)
            }

            // Recent reports
            item {
                RecentReportsSection(reports = recentReports)
            }

            // Actions
            item {
                ConsoleActions(
                    onRefresh = onRefresh,
                    isLoading = isLoading
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Error message
        if (!error.isNullOrBlank()) {
            ErrorCard(errors = listOf(error ?: "")) {
                onClearError()
            }
        }

        // Success message
        if (!successMessage.isNullOrBlank()) {
            SuccessCard(message = successMessage ?: "") {
                onClearSuccess()
            }
        }
    }
}

@Composable
fun ConsoleHeader(onBack: () -> Unit, onLogout: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Text(
            text = stringResource(R.string.console_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )

        IconButton(
            onClick = onLogout,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun StatsOverview(stats: com.whalert.app.model.ConsoleStats?, isLoading: Boolean) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.console_statistics),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator()
                }
            } else if (stats != null) {
                // Main stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatCard(
                        title = R.string.console_total_reports,
                        value = stats.totalReports.toString(),
                        icon = Icons.Default.Storage,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = R.string.console_total_users,
                        value = stats.totalUsers.toString(),
                        icon = Icons.Default.Group,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = R.string.console_transmissions,
                        value = stats.totalTransmissions.toString(),
                        icon = Icons.Default.MonitorHeart,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // System health
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Santé du système: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val health = stats.systemHealth
                    val healthColor = when (health) {
                        com.whalert.app.model.SystemHealth.GOOD -> MaterialTheme.colorScheme.primary
                        com.whalert.app.model.SystemHealth.WARNING -> MaterialTheme.colorScheme.tertiary
                        com.whalert.app.model.SystemHealth.CRITICAL -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.outline
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(healthColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = health.getDisplayName(),
                            style = MaterialTheme.typography.bodySmall,
                            color = healthColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Backend status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Backend: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val backendStatus = stats.backendStatus
                    val backendColor = when (backendStatus) {
                        BackendStatus.ONLINE -> MaterialTheme.colorScheme.primary
                        BackendStatus.OFFLINE -> MaterialTheme.colorScheme.error
                        BackendStatus.DEGRADED -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.outline
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(backendColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = backendStatus.getDisplayName(),
                            style = MaterialTheme.typography.bodySmall,
                            color = backendColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: Int, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(title),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun IntegrationStatusSection(statuses: List<com.whalert.app.model.IntegrationStatus>) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.console_integrations),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (statuses.isNotEmpty()) {
                statuses.forEach { status ->
                    IntegrationStatusItem(status = status)
                }
            } else {
                Text(
                    text = "Chargement des statuts d'intégration...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun IntegrationStatusItem(status: com.whalert.app.model.IntegrationStatus) {
    val color = when (status.status) {
        com.whalert.app.model.ServiceStatus.AVAILABLE -> MaterialTheme.colorScheme.primary
        com.whalert.app.model.ServiceStatus.UNAVAILABLE -> MaterialTheme.colorScheme.error
        com.whalert.app.model.ServiceStatus.DEGRADED -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = status.serviceName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (status.version != null) {
                Text(
                    text = "Version: ${status.version}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color)
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = when (status.status) {
                    com.whalert.app.model.ServiceStatus.AVAILABLE -> "En ligne"
                    com.whalert.app.model.ServiceStatus.UNAVAILABLE -> "Hors ligne"
                    com.whalert.app.model.ServiceStatus.DEGRADED -> "Dégradé"
                    else -> "Inconnu"
                },
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
    }
}

@Composable
fun RecentReportsSection(reports: List<Report>) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.console_history),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${reports.size} signalements",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            if (reports.isNotEmpty()) {
                reports.take(5).forEach { report ->
                    RecentReportItem(report = report)
                }
            } else {
                Text(
                    text = "Aucun signalement récent",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun RecentReportItem(report: Report) {
    val categoryColor = when (report.category) {
        ReportCategory.SPAM -> WhAlertColors.category_spam
        ReportCategory.SCAM -> WhAlertColors.category_scam
        ReportCategory.IMPERSONATION -> WhAlertColors.category_impersonation
        ReportCategory.HARASSMENT -> WhAlertColors.category_harassment
        ReportCategory.FRAUD -> WhAlertColors.category_fraud
        ReportCategory.MALICIOUS_BEHAVIOR -> WhAlertColors.category_malicious
        ReportCategory.OTHER -> WhAlertColors.category_other
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = SecurityUtils.maskPhoneNumber(report.fullPhoneNumber),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(categoryColor)
                )

                Spacer(modifier = Modifier.size(4.dp))

                Text(
                    text = report.category.getDisplayName(),
                    style = MaterialTheme.typography.bodySmall,
                    color = categoryColor
                )
            }
        }

        Text(
            text = java.text.SimpleDateFormat.getDateTimeInstance().format(report.createdAt),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun ConsoleActions(onRefresh: () -> Unit, isLoading: Boolean) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onRefresh,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Actualiser")
                }
            }
        }
    }
}

@Composable
fun ErrorCard(errors: List<String>, onDismiss: (() -> Unit)? = null) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Erreur",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                errors.forEach { error ->
                    Text(
                        text = "• $error",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun SuccessCard(message: String, onDismiss: () -> Unit) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Succès",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(start = 24.dp)
            )
        }
    }
}

@Composable
fun TextButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = "Se connecter",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

object CardDefaults {
    @Composable
    fun cardColors(
        containerColor: androidx.compose.ui.graphics.Color,
        contentColor: androidx.compose.ui.graphics.Color
    ): androidx.compose.material3.CardColors {
        return androidx.compose.material3.CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    }
}
