package com.whalert.app.ui.guide

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
 * Screen for displaying security guide
 */
@Composable
fun SecurityGuideScreen(navController: NavController) {
    val guideItems = remember {
        listOf(
            GuideItem(
                title = R.string.guide_how_to_recognize_scam,
                description = "Les arnaques sur WhatsApp peuvent prendre plusieurs formes. Méfiez-vous des messages promettant de l'argent facile, des offres trop belles pour être vraies, ou des demandes urgentes de transfert d'argent.",
                icon = "💰"
            ),
            GuideItem(
                title = R.string.guide_how_to_recognize_fraud,
                description = "Les comptes frauduleux peuvent imiter des contacts connus ou utiliser des photos de profil volées. Vérifiez toujours l'identité de votre interlocuteur avant de partager des informations sensibles.",
                icon = "👤"
            ),
            GuideItem(
                title = R.string.guide_keep_evidence,
                description = "Conservez les captures d'écran des conversations suspectes. Ces preuves peuvent être utiles pour signaler le compte et aider WhatsApp à investiguer.",
                icon = "📸"
            ),
            GuideItem(
                title = R.string.guide_dont_threaten,
                description = "Ne menacez jamais l'autre personne, même si vous êtes certain qu'il s'agit d'une arnaque. Restez calme et signalez simplement le compte.",
                icon = "🚫"
            ),
            GuideItem(
                title = R.string.guide_dont_fake_reports,
                description = "Ne faites jamais de faux signalements. Les faux signalements peuvent nuire à des utilisateurs innocents et sont contraires aux conditions d'utilisation de WhatsApp.",
                icon = "❌"
            ),
            GuideItem(
                title = R.string.guide_use_official_mechanisms,
                description = "Utilisez toujours les mécanismes officiels de WhatsApp pour signaler un compte. Cette application vous aide à préparer votre dossier, mais le signalement doit être fait via WhatsApp.",
                icon = "✅"
            ),
            GuideItem(
                title = R.string.guide_protect_information,
                description = "Ne partagez jamais vos informations personnelles (mot de passe, code PIN, numéro de carte bancaire) avec des inconnus sur WhatsApp.",
                icon = "🔒"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        GuideHeader(
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
                            text = "🛡️",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        
                        Text(
                            text = stringResource(R.string.guide_title),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Text(
                            text = "Apprenez à vous protéger contre les arnaques et les comptes malveillants sur WhatsApp.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Guide items
            items(guideItems) { item ->
                GuideItemCard(item = item)
            }

            // Additional tips
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
                        Text(
                            text = "💡 Conseils supplémentaires",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Text(
                            text = "• Vérifiez toujours le numéro de téléphone avant de répondre à un message suspect.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Text(
                            text = "• Activez la vérification en deux étapes sur votre compte WhatsApp.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Text(
                            text = "• Signalez immédiatement tout comportement suspect.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Text(
                            text = "• Sensibilisez vos proches aux risques des arnaques en ligne.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
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
fun GuideHeader(onBack: () -> Unit) {
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
            text = stringResource(R.string.guide_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun GuideItemCard(item: GuideItem) {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.icon,
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Spacer(modifier = Modifier.size(12.dp))
                
                Text(
                    text = stringResource(item.title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
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

data class GuideItem(
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
