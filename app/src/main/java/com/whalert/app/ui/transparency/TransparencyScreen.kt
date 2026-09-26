package com.whalert.app.ui.transparency

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.whalert.app.R

/**
 * Screen for displaying what the app can and cannot do
 */
@Composable
fun TransparencyScreen(navController: NavController) {
    val canDoItems = remember {
        listOf(
            TransparencyItem(
                title = R.string.transparency_can_prepare,
                description = "Préparer un dossier de signalement complet avec toutes les informations nécessaires.",
                icon = "✅"
            ),
            TransparencyItem(
                title = R.string.transparency_can_organize,
                description = "Organiser et structurer vos preuves (captures d'écran, texte, dates) pour un signalement efficace.",
                icon = "📁"
            ),
            TransparencyItem(
                title = R.string.transparency_can_generate,
                description = "Générer un texte de signalement professionnel et factuel basé sur vos informations.",
                icon = "📝"
            ),
            TransparencyItem(
                title = R.string.transparency_can_request_confirmation,
                description = "Demander une confirmation avant l'envoi de chaque signalement.",
                icon = "🤔"
            ),
            TransparencyItem(
                title = R.string.transparency_can_transmit,
                description = "Vous guider vers le mécanisme officiel de WhatsApp pour transmettre votre signalement.",
                icon = "📤"
            ),
            TransparencyItem(
                title = R.string.transparency_can_keep_history,
                description = "Conserver l'historique de vos signalements pour suivi personnel.",
                icon = "📊"
            ),
            TransparencyItem(
                title = R.string.transparency_can_show_confirmations,
                description = "Afficher les confirmations techniques réellement reçues de votre part.",
                icon = "✉️"
            )
        )
    }

    val cannotDoItems = remember {
        listOf(
            TransparencyItem(
                title = R.string.transparency_cannot_guarantee,
                description = "Garantir qu'un compte sera banni. La décision appartient uniquement à WhatsApp.",
                icon = "❌"
            ),
            TransparencyItem(
                title = R.string.transparency_cannot_know,
                description = "Connaître les décisions internes de WhatsApp sans source officielle publique.",
                icon = "🔒"
            ),
            TransparencyItem(
                title = R.string.transparency_cannot_fake,
                description = "Envoyer de faux signalements. Tous les signalements doivent être authentiques.",
                icon = "🚫"
            ),
            TransparencyItem(
                title = R.string.transparency_cannot_multiply,
                description = "Multiplier artificiellement le même signalement pour forcer une action.",
                icon = "➖"
            ),
            TransparencyItem(
                title = R.string.transparency_cannot_bypass,
                description = "Contourner les protections ou les mécanismes de sécurité de WhatsApp.",
                icon = "🛡️"
            ),
            TransparencyItem(
                title = R.string.transparency_cannot_force,
                description = "Forcer WhatsApp à suspendre un compte. Seuls les mécanismes officiels de WhatsApp peuvent le faire.",
                icon = "🚨"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        TransparencyHeader(
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

            // Introduction
            item {
                ElevatedCard(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                            text = "ℹ️",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        
                        Text(
                            text = stringResource(R.string.transparency_title),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Text(
                            text = "Comprenez clairement ce que WhAlert peut et ne peut pas faire pour vous.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Can do section
            item {
                SectionHeader(
                    title = R.string.transparency_can,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(canDoItems) { item ->
                TransparencyItemCard(
                    item = item,
                    isPositive = true
                )
            }

            // Cannot do section
            item {
                SectionHeader(
                    title = R.string.transparency_cannot,
                    color = MaterialTheme.colorScheme.error
                )
            }

            items(cannotDoItems) { item ->
                TransparencyItemCard(
                    item = item,
                    isPositive = false
                )
            }

            // Important note
            item {
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Important",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            
                            Spacer(modifier = Modifier.size(8.dp))
                            
                            Text(
                                text = "Note importante",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Text(
                            text = "WhatsApp indique qu'un signalement peut conduire à une action, mais qu'un signalement ne garantit pas un bannissement. La décision finale appartient toujours à WhatsApp.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Text(
                            text = "Cette application ne prétend jamais savoir qu'un compte a été banni ou suspendu si WhatsApp ne fournit pas officiellement cette information.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun TransparencyHeader(onBack: () -> Unit) {
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
            text = stringResource(R.string.transparency_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun SectionHeader(title: Int, color: androidx.compose.ui.graphics.Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .aspectRatio(1f)
                .background(color, RoundedCornerShape(2.dp))
        )
        
        Spacer(modifier = Modifier.size(8.dp))
        
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
    }
}

@Composable
fun TransparencyItemCard(item: TransparencyItem, isPositive: Boolean) {
    val color = if (isPositive) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }

    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.icon,
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Spacer(modifier = Modifier.size(12.dp))
                
                Text(
                    text = stringResource(item.title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = color
                )
            }
            
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

data class TransparencyItem(
    val title: Int,
    val description: String,
    val icon: String
)

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
