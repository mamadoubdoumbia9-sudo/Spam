package com.whalert.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SoundOff
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.whalert.app.R
import com.whalert.app.model.DarkMode
import com.whalert.app.viewmodel.UserViewModel

/**
 * Screen for application settings
 */
@Composable
fun SettingsScreen(navController: NavController) {
    val userViewModel: UserViewModel = hiltViewModel()
    val preferences by userViewModel.preferences.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val error by userViewModel.error.collectAsState()
    val successMessage by userViewModel.successMessage.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.clearError()
        userViewModel.clearSuccessMessage()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        SettingsHeader(
            onBack = { navController.popBackStack() }
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

            // Account section
            item {
                SettingsSection(title = R.string.settings_account) {
                    SettingsItem(
                        title = R.string.settings_profile,
                        icon = Icons.Default.Person,
                        onClick = {
                            // Navigate to profile
                        }
                    )

                    SettingsItem(
                        title = R.string.settings_whatsapp_connection,
                        icon = Icons.Default.Security,
                        description = if (preferences?.whatsappConnected == true) {
                            "Connecté"
                        } else {
                            "Non connecté"
                        },
                        onClick = {
                            // Navigate to WhatsApp connection
                        }
                    )

                    SettingsItem(
                        title = R.string.settings_logout,
                        icon = Icons.Default.Logout,
                        onClick = {
                            userViewModel.logout()
                            navController.navigate("onboarding") {
                                popUpTo("settings") { inclusive = true }
                            }
                        },
                        isDestructive = true
                    )
                }
            }

            // Notifications section
            item {
                SettingsSection(title = R.string.settings_notifications) {
                    val notificationsEnabled = preferences?.notificationsEnabled ?: true
                    val soundEnabled = preferences?.soundEnabled ?: true
                    val vibrationEnabled = preferences?.vibrationEnabled ?: true

                    SwitchSetting(
                        title = R.string.settings_notifications,
                        icon = Icons.Default.Notifications,
                        checked = notificationsEnabled,
                        onCheckedChange = { enabled ->
                            userViewModel.updatePreferences(
                                notificationsEnabled = enabled
                            )
                        }
                    )

                    SwitchSetting(
                        title = "Son",
                        icon = Icons.Default.SoundOff,
                        checked = soundEnabled,
                        onCheckedChange = { enabled ->
                            userViewModel.updatePreferences(
                                soundEnabled = enabled
                            )
                        }
                    )

                    SwitchSetting(
                        title = "Vibration",
                        icon = Icons.Default.Vibration,
                        checked = vibrationEnabled,
                        onCheckedChange = { enabled ->
                            userViewModel.updatePreferences(
                                vibrationEnabled = enabled
                            )
                        }
                    )
                }
            }

            // General section
            item {
                SettingsSection(title = R.string.settings_general) {
                    val darkMode = preferences?.darkMode ?: DarkMode.SYSTEM

                    SingleChoiceSetting(
                        title = R.string.settings_dark_mode,
                        icon = Icons.Default.DarkMode,
                        selected = when (darkMode) {
                            DarkMode.LIGHT -> "Clair"
                            DarkMode.DARK -> "Sombre"
                            DarkMode.SYSTEM -> "Système"
                        },
                        options = listOf("Clair", "Sombre", "Système"),
                        onSelect = { option ->
                            val mode = when (option) {
                                "Clair" -> DarkMode.LIGHT
                                "Sombre" -> DarkMode.DARK
                                else -> DarkMode.SYSTEM
                            }
                            userViewModel.updatePreferences(darkMode = mode)
                        }
                    )

                    SettingsItem(
                        title = R.string.settings_language,
                        icon = Icons.Default.Language,
                        description = preferences?.language ?: "Français",
                        onClick = {
                            // Navigate to language settings
                        }
                    )

                    SettingsItem(
                        title = R.string.settings_daily_limit,
                        icon = Icons.Default.Security,
                        description = "${preferences?.dailyLimit ?: 3} signalements par 24 heures",
                        onClick = {
                            // Navigate to daily limit settings
                        }
                    )
                }
            }

            // Security section
            item {
                SettingsSection(title = R.string.settings_security) {
                    SettingsItem(
                        title = "Changer le mot de passe",
                        icon = Icons.Default.Security,
                        onClick = {
                            // Navigate to change password
                        }
                    )

                    SettingsItem(
                        title = "Console privée",
                        icon = Icons.Default.Security,
                        description = "Accès à la console d'administration",
                        onClick = {
                            navController.navigate("console")
                        }
                    )
                }
            }

            // About section
            item {
                SettingsSection(title = R.string.settings_about) {
                    SettingsItem(
                        title = "À propos de WhAlert",
                        icon = Icons.Default.Info,
                        onClick = {
                            // Navigate to about
                        }
                    )

                    SettingsItem(
                        title = "Version",
                        icon = Icons.Default.Info,
                        description = "1.0.0",
                        onClick = {}
                    )

                    SettingsItem(
                        title = "Politique de confidentialité",
                        icon = Icons.Default.Security,
                        onClick = {
                            // Navigate to privacy policy
                        }
                    )

                    SettingsItem(
                        title = "Conditions d'utilisation",
                        icon = Icons.Default.Info,
                        onClick = {
                            // Navigate to terms
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Error message
    if (!error.isNullOrBlank()) {
        ErrorCard(errors = listOf(error ?: "")) {
            userViewModel.clearError()
        }
    }

    // Success message
    if (!successMessage.isNullOrBlank()) {
        SuccessCard(message = successMessage ?: "") {
            userViewModel.clearSuccessMessage()
        }
    }
}

@Composable
fun SettingsHeader(onBack: () -> Unit) {
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
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun SettingsSection(title: Int, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        ElevatedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            content()
        }
    }
}

@Composable
fun SettingsItem(
    title: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String? = null,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(title),
                tint = if (isDestructive) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
            
            Spacer(modifier = Modifier.size(12.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                
                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = "Next",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun SwitchSetting(
    title: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(title),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.size(12.dp))
            
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SingleChoiceSetting(
    title: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { showDialog = true })
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(title),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.size(12.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = selected,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = "Select",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )
    }

    if (showDialog) {
        SingleChoiceDialog(
            title = stringResource(title),
            options = options,
            selected = selected,
            onSelect = { option ->
                onSelect(option)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun SingleChoiceDialog(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss)
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .align(Alignment.Center)
                .padding(16.dp),
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(option)
                                onDismiss()
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (option == selected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                        } else {
                            Spacer(modifier = Modifier.size(24.dp))
                        }
                        
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
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
