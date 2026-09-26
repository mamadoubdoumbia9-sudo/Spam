package com.whalert.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.whalert.app.R
import com.whalert.app.model.ConnectionStatus
import com.whalert.app.ui.theme.WhAlertColors
import com.whalert.app.viewmodel.UserViewModel

/**
 * Home screen for WhAlert application
 */
@Composable
fun HomeScreen(navController: NavController) {
    val userViewModel: UserViewModel = hiltViewModel()
    val connectionStatus by userViewModel.connectionStatus.collectAsState()
    val isAuthenticated by userViewModel.isAuthenticated.collectAsState()

    LaunchedEffect(Unit) {
        // Check authentication state
        if (!isAuthenticated) {
            navController.navigate("onboarding") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        HomeHeader(connectionStatus = connectionStatus)

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

            // Quick actions
            item {
                Text(
                    text = stringResource(R.string.home_subtitle),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // New report button
                    ActionCard(
                        title = R.string.new_report,
                        description = R.string.report_subtitle,
                        icon = Icons.Outlined.Add,
                        color = WhAlertColors.Primary,
                        onClick = {
                            navController.navigate("report/creation")
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // My reports button
                    ActionCard(
                        title = R.string.my_reports,
                        description = R.string.history_title,
                        icon = Icons.Outlined.History,
                        color = WhAlertColors.Secondary,
                        onClick = {
                            navController.navigate("history")
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Information section
            item {
                Text(
                    text = "Informations",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Security guide
                    ActionCard(
                        title = R.string.security_guide,
                        description = R.string.guide_title,
                        icon = Icons.Outlined.Security,
                        color = WhAlertColors.Tertiary,
                        onClick = {
                            navController.navigate("guide")
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Transparency
                    ActionCard(
                        title = R.string.transparency,
                        description = R.string.transparency_title,
                        icon = Icons.Outlined.Info,
                        color = WhAlertColors.Primary,
                        onClick = {
                            navController.navigate("transparency")
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Admin section (only for authenticated users with console access)
            item {
                if (isAuthenticated) {
                    Text(
                        text = "Administration",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            item {
                if (isAuthenticated) {
                    ActionCard(
                        title = R.string.console,
                        description = R.string.console_title,
                        icon = Icons.Outlined.Settings,
                        color = 0xFFFF5722.toInt(), // Orange
                        onClick = {
                            navController.navigate("console")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating action button
        FloatingActionButton(
            onClick = {
                navController.navigate("report/creation")
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.new_report),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun HomeHeader(connectionStatus: ConnectionStatus) {
    val statusText = when (connectionStatus) {
        ConnectionStatus.CONNECTED -> stringResource(R.string.connection_status_connected)
        ConnectionStatus.DISCONNECTED -> stringResource(R.string.connection_status_disconnected)
        ConnectionStatus.SESSION_EXPIRED -> stringResource(R.string.connection_status_expired)
        ConnectionStatus.CONNECTION_ERROR -> stringResource(R.string.connection_status_error)
    }

    val statusColor = when (connectionStatus) {
        ConnectionStatus.CONNECTED -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.error
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    ) {
        // App name
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Connection status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(statusColor)
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ActionCard(
    title: Int,
    description: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .clickable(onClick = onClick),
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(title),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
