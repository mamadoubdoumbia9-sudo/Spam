package com.whalert.app.ui.history

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.whalert.app.R
import com.whalert.app.model.Report
import com.whalert.app.model.ReportCategory
import com.whalert.app.model.ReportStatus
import com.whalert.app.ui.theme.WhAlertColors
import com.whalert.app.util.PhoneNumberValidator
import com.whalert.app.util.SecurityUtils
import com.whalert.app.viewmodel.ReportViewModel
import java.util.Date

/**
 * Screen for viewing report history
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportHistoryScreen(navController: NavController) {
    val viewModel: ReportViewModel = hiltViewModel()
    val reports by viewModel.reports.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Filter states
    var showCategoryFilter by remember { mutableStateOf(false) }
    var showStatusFilter by remember { mutableStateOf(false) }
    
    // Search state
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadReports()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        HistoryHeader(
            onBack = { navController.popBackStack() }
        )

        // Filter bar
        FilterBar(
            searchQuery = searchQuery,
            onSearchChange = { 
                searchQuery = it
                viewModel.setSearchQuery(it)
            },
            onCategoryFilter = { showCategoryFilter = true },
            onStatusFilter = { showStatusFilter = true },
            onClearFilters = {
                searchQuery = ""
                viewModel.clearFilters()
            }
        )

        // Content
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator()
            }
        } else if (reports.isEmpty()) {
            EmptyState(
                title = R.string.history_empty,
                description = "Aucun signalement trouvé. Créez votre premier signalement."
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reports) { report ->
                    ReportCard(
                        report = report,
                        onClick = {
                            navController.navigate("report/detail/${report.id}")
                        }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Category filter dropdown
        if (showCategoryFilter) {
            CategoryFilterDialog(
                onDismiss = { showCategoryFilter = false },
                onSelect = { category ->
                    viewModel.setCategoryFilter(category)
                    showCategoryFilter = false
                }
            )
        }

        // Status filter dropdown
        if (showStatusFilter) {
            StatusFilterDialog(
                onDismiss = { showStatusFilter = false },
                onSelect = { status ->
                    viewModel.setStatusFilter(status)
                    showStatusFilter = false
                }
            )
        }

        // Error message
        if (!error.isNullOrBlank()) {
            ErrorCard(errors = listOf(error ?: "")) {
                viewModel.clearError()
            }
        }
    }
}

@Composable
fun HistoryHeader(onBack: () -> Unit) {
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
            text = stringResource(R.string.history_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun FilterBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onCategoryFilter: () -> Unit,
    onStatusFilter: () -> Unit,
    onClearFilters: () -> Unit
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search
            androidx.compose.material3.OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                label = { Text("Rechercher") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search"
                    )
                },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            // Category filter
            IconButton(
                onClick = onCategoryFilter
            ) {
                Icon(
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = "Filter by category"
                )
            }

            // Status filter
            IconButton(
                onClick = onStatusFilter
            ) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "Filter by status"
                )
            }
        }
    }
}

@Composable
fun EmptyState(title: Int, description: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📄",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ReportCard(report: Report, onClick: () -> Unit) {
    val categoryColor = when (report.category) {
        ReportCategory.SPAM -> WhAlertColors.category_spam
        ReportCategory.SCAM -> WhAlertColors.category_scam
        ReportCategory.IMPERSONATION -> WhAlertColors.category_impersonation
        ReportCategory.HARASSMENT -> WhAlertColors.category_harassment
        ReportCategory.FRAUD -> WhAlertColors.category_fraud
        ReportCategory.MALICIOUS_BEHAVIOR -> WhAlertColors.category_malicious
        ReportCategory.OTHER -> WhAlertColors.category_other
    }

    val statusColor = when (report.status) {
        ReportStatus.DRAFT -> WhAlertColors.status_draft
        ReportStatus.PREPARED -> WhAlertColors.status_prepared
        ReportStatus.SUBMITTED -> WhAlertColors.status_submitted
        ReportStatus.CONFIRMATION_RECEIVED -> WhAlertColors.status_confirmation_received
        ReportStatus.FOLLOW_UP_NEEDED -> WhAlertColors.status_follow_up_needed
        ReportStatus.CLOSED -> WhAlertColors.status_closed
    }

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with category and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(categoryColor)
                    )
                    
                    Spacer(modifier = Modifier.size(8.dp))
                    
                    Text(
                        text = report.category.getDisplayName(),
                        style = MaterialTheme.typography.labelSmall,
                        color = categoryColor
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor)
                    )
                    
                    Spacer(modifier = Modifier.size(8.dp))
                    
                    Text(
                        text = report.status.getDisplayName(),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor
                    )
                }
            }

            // Phone number (masked)
            Text(
                text = SecurityUtils.maskPhoneNumber(report.fullPhoneNumber),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Description preview
            Text(
                text = report.description.take(100) + if (report.description.length > 100) "..." else "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            // Date
            Text(
                text = java.text.SimpleDateFormat.getDateTimeInstance().format(report.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            // Evidence count
            if (!report.evidenceScreenshots.isNullOrEmpty() || !report.evidenceText.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Evidence",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.size(4.dp))
                    
                    Text(
                        text = "${report.evidenceScreenshots?.size ?: 0 + if (!report.evidenceText.isNullOrBlank()) 1 else 0} preuve(s)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryFilterDialog(
    onDismiss: () -> Unit,
    onSelect: (ReportCategory?) -> Unit
) {
    val categories = listOf(
        null,
        ReportCategory.SPAM,
        ReportCategory.SCAM,
        ReportCategory.IMPERSONATION,
        ReportCategory.HARASSMENT,
        ReportCategory.FRAUD,
        ReportCategory.MALICIOUS_BEHAVIOR,
        ReportCategory.OTHER
    )

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
                    text = "Filtrer par catégorie",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                categories.forEach { category ->
                    val text = category?.getDisplayName() ?: "Toutes les catégories"
                    
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(category)
                                onDismiss()
                            }
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusFilterDialog(
    onDismiss: () -> Unit,
    onSelect: (ReportStatus?) -> Unit
) {
    val statuses = listOf(
        null,
        ReportStatus.DRAFT,
        ReportStatus.PREPARED,
        ReportStatus.SUBMITTED,
        ReportStatus.CONFIRMATION_RECEIVED,
        ReportStatus.FOLLOW_UP_NEEDED,
        ReportStatus.CLOSED
    )

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
                    text = "Filtrer par statut",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                statuses.forEach { status ->
                    val text = status?.getDisplayName() ?: "Tous les statuts"
                    
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(status)
                                onDismiss()
                            }
                            .padding(12.dp)
                    )
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
