package com.whalert.app.ui.report

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.whalert.app.R
import com.whalert.app.model.Report
import com.whalert.app.model.ReportStatus
import com.whalert.app.model.TransmissionStatus
import com.whalert.app.ui.theme.WhAlertColors
import com.whalert.app.util.PhoneNumberValidator
import com.whalert.app.viewmodel.ReportViewModel

/**
 * Screen for viewing report details and status
 */
@Composable
fun ReportDetailScreen(navController: NavController, reportId: Long?) {
    val viewModel: ReportViewModel = hiltViewModel()
    val currentReport by viewModel.currentReport.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // State for actions
    var showActions by remember { mutableStateOf(false) }

    LaunchedEffect(reportId) {
        if (reportId != null) {
            viewModel.getReportById(reportId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        ReportDetailHeader(
            onBack = { navController.popBackStack() }
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator()
                }
            } else if (currentReport != null) {
                // Status indicator
                StatusIndicator(report = currentReport!!)

                Spacer(modifier = Modifier.height(8.dp))

                // Report details
                ReportDetails(report = currentReport!!)

                Spacer(modifier = Modifier.height(8.dp))

                // Timeline
                ReportTimeline(report = currentReport!!)

                Spacer(modifier = Modifier.height(8.dp))

                // Action buttons
                ActionButtons(
                    report = currentReport!!,
                    onEdit = {
                        navController.navigate("report/creation") {
                            popUpTo("report/detail/${currentReport?.id}") { inclusive = true }
                        }
                    },
                    onShare = {
                        showActions = true
                    },
                    onDelete = {
                        viewModel.deleteReport(currentReport!!.id)
                        navController.popBackStack()
                    }
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Error message
    if (!error.isNullOrBlank()) {
        ErrorCard(errors = listOf(error ?: "")) {
            viewModel.clearError()
        }
    }
}

@Composable
fun ReportDetailHeader(onBack: () -> Unit) {
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
            text = stringResource(R.string.report_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun StatusIndicator(report: Report) {
    val status = report.status
    val (color, icon, text) = when (status) {
        ReportStatus.DRAFT -> Triple(
            MaterialTheme.colorScheme.outline,
            Icons.Default.Edit,
            stringResource(R.string.report_status_draft)
        )
        ReportStatus.PREPARED -> Triple(
            MaterialTheme.colorScheme.primary,
            Icons.Default.ArrowForward,
            stringResource(R.string.report_status_prepared)
        )
        ReportStatus.SUBMITTED -> Triple(
            MaterialTheme.colorScheme.primary,
            Icons.Default.Check,
            stringResource(R.string.report_status_submitted)
        )
        ReportStatus.CONFIRMATION_RECEIVED -> Triple(
            MaterialTheme.colorScheme.primary,
            Icons.Default.Check,
            stringResource(R.string.report_status_confirmation_received)
        )
        ReportStatus.FOLLOW_UP_NEEDED -> Triple(
            MaterialTheme.colorScheme.tertiary,
            Icons.Default.Edit,
            stringResource(R.string.report_status_follow_up_needed)
        )
        ReportStatus.CLOSED -> Triple(
            MaterialTheme.colorScheme.outline,
            Icons.Default.Close,
            stringResource(R.string.report_status_closed)
        )
    }

    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f),
            contentColor = color
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = status.getDisplayName(),
                tint = color
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = color
            )
        }
    }
}

@Composable
fun ReportDetails(report: Report) {
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
            // Number
            DetailRow(
                label = stringResource(R.string.verification_number),
                value = PhoneNumberValidator.formatForDisplay(report.fullPhoneNumber)
            )

            // Category
            DetailRow(
                label = stringResource(R.string.verification_category),
                value = report.category.getDisplayName(),
                valueColor = MaterialTheme.colorScheme.primary
            )

            // Description
            DetailRow(
                label = stringResource(R.string.verification_description),
                value = report.description,
                isMultiline = true
            )

            // Date
            if (report.incidentDate != null) {
                DetailRow(
                    label = stringResource(R.string.evidence_date_time),
                    value = java.text.SimpleDateFormat.getDateTimeInstance().format(report.incidentDate)
                )
            }

            // Submission date
            if (report.submissionDate != null) {
                DetailRow(
                    label = "Date de soumission",
                    value = java.text.SimpleDateFormat.getDateTimeInstance().format(report.submissionDate)
                )
            }

            // Evidence
            if (!report.evidenceText.isNullOrBlank() || !report.evidenceScreenshots.isNullOrEmpty()) {
                Text(
                    text = stringResource(R.string.verification_attachments, 
                        (report.evidenceScreenshots?.size ?: 0) + if (!report.evidenceText.isNullOrBlank()) 1 else 0),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (!report.evidenceScreenshots.isNullOrEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        report.evidenceScreenshots?.forEach { uri ->
                            EvidenceThumbnail(uri = uri)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportTimeline(report: Report) {
    val steps = listOf(
        Triple(
            "Préparation",
            report.createdAt,
            report.status != ReportStatus.DRAFT
        ),
        Triple(
            "Signalement transmis",
            report.submissionDate,
            report.status == ReportStatus.SUBMITTED || 
            report.status == ReportStatus.CONFIRMATION_RECEIVED ||
            report.status == ReportStatus.FOLLOW_UP_NEEDED ||
            report.status == ReportStatus.CLOSED
        ),
        Triple(
            "Confirmation",
            report.confirmationDate,
            report.status == ReportStatus.CONFIRMATION_RECEIVED ||
            report.status == ReportStatus.FOLLOW_UP_NEEDED ||
            report.status == ReportStatus.CLOSED
        ),
        Triple(
            "Suivi",
            report.closedDate,
            report.status == ReportStatus.FOLLOW_UP_NEEDED ||
            report.status == ReportStatus.CLOSED
        )
    )

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
                text = "État du signalement",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            steps.forEachIndexed { index, (title, date, completed) ->
                TimelineStep(
                    title = title,
                    date = date,
                    completed = completed,
                    isLast = index == steps.size - 1
                )
            }

            // WhatsApp status
            Spacer(modifier = Modifier.height(8.dp))
            
            ElevatedCard(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Décision de WhatsApp",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = stringResource(R.string.status_unknown),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    
                    Text(
                        text = stringResource(R.string.status_no_official_confirmation),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineStep(title: String, date: Date?, completed: Boolean, isLast: Boolean) {
    val dateText = date?.let {
        java.text.SimpleDateFormat.getDateTimeInstance().format(it)
    } ?: "En attente"

    val color = if (completed) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Step indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (completed) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (completed) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            // Step info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (completed) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    }
                )
                
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        if (!isLast) {
            // Connector line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .padding(start = 11.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            if (completed) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            }
                        )
                )
            }
        }
    }
}

@Composable
fun ActionButtons(
    report: Report,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (report.status == ReportStatus.DRAFT) {
            Button(
                onClick = onEdit,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(stringResource(R.string.edit))
            }
        } else {
            Button(
                onClick = onShare,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.action_share)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(stringResource(R.string.action_share))
            }
        }

        Button(
            onClick = onDelete,
            modifier = Modifier.weight(1f),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.delete)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(stringResource(R.string.delete))
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    isMultiline: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        if (isMultiline) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = valueColor
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = valueColor
            )
        }
    }
}

@Composable
fun EvidenceThumbnail(uri: String) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "Evidence",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ErrorCard(errors: List<String>, onDismiss: (() -> Unit)? = null) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        modifier = Modifier.fillMaxWidth()
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
        modifier = Modifier.fillMaxWidth()
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
