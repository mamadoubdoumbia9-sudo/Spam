package com.whalert.app.ui.report

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.whalert.app.R
import com.whalert.app.model.ReportCategory
import com.whalert.app.ui.theme.WhAlertColors
import com.whalert.app.util.PhoneNumberValidator
import com.whalert.app.viewmodel.ReportViewModel
import java.util.Date

/**
 * Screen for creating a new report
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportCreationScreen(navController: NavController) {
    val viewModel: ReportViewModel = hiltViewModel()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val countryCode by viewModel.countryCode.collectAsState()
    val category by viewModel.category.collectAsState()
    val description by viewModel.description.collectAsState()
    val evidenceText by viewModel.evidenceText.collectAsState()
    val evidenceScreenshots by viewModel.evidenceScreenshots.collectAsState()
    val incidentDate by viewModel.incidentDate.collectAsState()
    val additionalInfo by viewModel.additionalInfo.collectAsState()
    
    val phoneNumberValidation by viewModel.phoneNumberValidation.collectAsState()
    val descriptionValidation by viewModel.descriptionValidation.collectAsState()
    val validationErrors by viewModel.validationErrors.collectAsState()
    val validationWarnings by viewModel.validationWarnings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val formValid by viewModel.formValid.collectAsState()

    val context = LocalContext.current
    
    // State for dropdowns
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showCountryCodeDropdown by remember { mutableStateOf(false) }
    
    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri ->
            uri?.let { viewModel.addScreenshot(it.toString()) }
        }
    }

    // Date picker
    val datePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickDate()
    ) { date ->
        date?.let { viewModel.setIncidentDate(Date(it.time)) }
    }

    LaunchedEffect(Unit) {
        viewModel.clearError()
        viewModel.clearSuccessMessage()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        ReportHeader(
            title = R.string.report_title,
            subtitle = R.string.report_subtitle
        )

        // Form
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Phone number field
            item {
                PhoneNumberField(
                    phoneNumber = phoneNumber,
                    countryCode = countryCode,
                    validation = phoneNumberValidation,
                    onPhoneNumberChange = { viewModel.setPhoneNumber(it) },
                    onCountryCodeChange = { viewModel.setCountryCode(it) }
                )
            }

            // Category selection
            item {
                CategorySelection(
                    selectedCategory = category,
                    onCategoryChange = { viewModel.setCategory(it) }
                )
            }

            // Description field
            item {
                DescriptionField(
                    description = description,
                    validation = descriptionValidation,
                    onDescriptionChange = { viewModel.setDescription(it) }
                )
            }

            // Evidence section
            item {
                EvidenceSection(
                    evidenceText = evidenceText,
                    evidenceScreenshots = evidenceScreenshots,
                    onEvidenceTextChange = { viewModel.setEvidenceText(it) },
                    onAddScreenshot = {
                        imagePickerLauncher.launch("image/*")
                    },
                    onRemoveScreenshot = { viewModel.removeScreenshot(it) }
                )
            }

            // Incident date
            item {
                DateField(
                    date = incidentDate,
                    onDateChange = { viewModel.setIncidentDate(it) },
                    onPickerClick = {
                        datePickerLauncher.launch(null)
                    }
                )
            }

            // Additional info
            item {
                AdditionalInfoField(
                    info = additionalInfo,
                    onInfoChange = { viewModel.setAdditionalInfo(it) }
                )
            }

            // Validation warnings
            if (validationWarnings.isNotEmpty()) {
                item {
                    WarningCard(warnings = validationWarnings)
                }
            }

            // Validation errors
            if (validationErrors.isNotEmpty()) {
                item {
                    ErrorCard(errors = validationErrors)
                }
            }

            // Success message
            if (!successMessage.isNullOrBlank()) {
                item {
                    SuccessCard(message = successMessage ?: "") {
                        viewModel.clearSuccessMessage()
                    }
                }
            }

            // Error message
            if (!error.isNullOrBlank()) {
                item {
                    ErrorCard(errors = listOf(error ?: "")) {
                        viewModel.clearError()
                    }
                }
            }

            // Submit button
            item {
                SubmitButton(
                    isLoading = isLoading,
                    isValid = formValid,
                    onClick = {
                        val result = viewModel.createReport()
                        result.onSuccess { report ->
                            navController.navigate("report/verification/${report.id}") {
                                popUpTo("report/creation") { inclusive = true }
                            }
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun ReportHeader(title: Int, subtitle: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = stringResource(subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.phone_number_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PhoneNumberField(
    phoneNumber: String,
    countryCode: String,
    validation: com.whalert.app.util.ValidationResult,
    onPhoneNumberChange: (String) -> Unit,
    onCountryCodeChange: (String) -> Unit
) {
    val countryCodes = listOf(
        "+1", "+44", "+33", "+49", "+39", "+34", "+91", "+86", "+81",
        "+55", "+7", "+61", "+27", "+234", "+254", "+233", "+221",
        "+225", "+223", "+212"
    )
    
    var showCountryCodeDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(R.string.phone_number_hint),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Country code
            Box {
                OutlinedTextField(
                    value = countryCode.takeIf { it.isNotBlank() } ?: "+223",
                    onValueChange = { 
                        if (it.startsWith("+") && it.length <= 4) {
                            onCountryCodeChange(it)
                        }
                    },
                    label = { Text("Code pays") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.weight(0.3f),
                    trailingIcon = {
                        IconButton(onClick = { showCountryCodeDropdown = true }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select country code"
                            )
                        }
                    }
                )
                
                DropdownMenu(
                    expanded = showCountryCodeDropdown,
                    onDismissRequest = { showCountryCodeDropdown = false }
                ) {
                    countryCodes.forEach { code ->
                        DropdownMenuItem(
                            text = { Text(code) },
                            onClick = {
                                onCountryCodeChange(code)
                                showCountryCodeDropdown = false
                            }
                        )
                    }
                }
            }
            
            // Phone number
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                label = { Text(stringResource(R.string.phone_number_hint)) },
                placeholder = { Text("XXXXXXXXXX") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.weight(0.7f),
                isError = !validation.isValid && phoneNumber.isNotBlank(),
                supportingText = {
                    if (!validation.isValid && phoneNumber.isNotBlank()) {
                        Text(
                            text = validation.errors.firstOrNull() ?: "",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
        
        // Validation warnings
        if (validation.warnings.isNotEmpty() && phoneNumber.isNotBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                validation.warnings.forEach { warning ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = warning,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySelection(
    selectedCategory: ReportCategory,
    onCategoryChange: (ReportCategory) -> Unit
) {
    val categories = ReportCategory.values()
    var showDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(R.string.category_title),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Box {
            OutlinedTextField(
                value = selectedCategory.getDisplayName(),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.category_title)) },
                trailingIcon = {
                    IconButton(onClick = { showDropdown = true }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select category"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.getDisplayName()) },
                        onClick = {
                            onCategoryChange(category)
                            showDropdown = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DescriptionField(
    description: String,
    validation: com.whalert.app.util.ValidationResult,
    onDescriptionChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(R.string.description_hint),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.description_hint)) },
            placeholder = { Text(stringResource(R.string.description_hint)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            maxLines = 8,
            isError = !validation.isValid && description.isNotBlank(),
            supportingText = {
                if (!validation.isValid && description.isNotBlank()) {
                    Text(
                        text = validation.errors.firstOrNull() ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }
}

@Composable
fun EvidenceSection(
    evidenceText: String,
    evidenceScreenshots: List<String>,
    onEvidenceTextChange: (String) -> Unit,
    onAddScreenshot: () -> Unit,
    onRemoveScreenshot: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.evidence_title),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // Text evidence
        OutlinedTextField(
            value = evidenceText,
            onValueChange = onEvidenceTextChange,
            label = { Text(stringResource(R.string.evidence_text)) },
            placeholder = { Text(stringResource(R.string.evidence_text)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )
        
        // Screenshots
        Text(
            text = stringResource(R.string.evidence_screenshots),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        if (evidenceScreenshots.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                evidenceScreenshots.forEach { uri ->
                    ScreenshotThumbnail(
                        uri = uri,
                        onRemove = { onRemoveScreenshot(uri) }
                    )
                }
            }
        }
        
        // Add screenshot button
        Button(
            onClick = onAddScreenshot,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = stringResource(R.string.evidence_screenshots)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(stringResource(R.string.evidence_screenshots))
        }
    }
}

@Composable
fun ScreenshotThumbnail(uri: String, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "Screenshot",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun DateField(
    date: Date?,
    onDateChange: (Date?) -> Unit,
    onPickerClick: () -> Unit
) {
    val formattedDate = date?.let {
        java.text.SimpleDateFormat.getDateTimeInstance().format(it)
    } ?: ""

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(R.string.evidence_date_time),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        OutlinedTextField(
            value = formattedDate,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.evidence_date_time)) },
            placeholder = { Text("Sélectionnez une date") },
            trailingIcon = {
                IconButton(onClick = onPickerClick) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AdditionalInfoField(
    info: String,
    onInfoChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(R.string.evidence_additional),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        OutlinedTextField(
            value = info,
            onValueChange = onInfoChange,
            label = { Text(stringResource(R.string.evidence_additional)) },
            placeholder = { Text(stringResource(R.string.evidence_additional)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )
    }
}

@Composable
fun WarningCard(warnings: List<String>, onDismiss: (() -> Unit)? = null) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f),
            contentColor = MaterialTheme.colorScheme.onSurface
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
                    imageVector = Icons.Default.Info,
                    contentDescription = "Warning",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Informations importantes",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                warnings.forEach { warning ->
                    Text(
                        text = "• $warning",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
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
                    text = "Erreurs",
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

@Composable
fun SubmitButton(
    isLoading: Boolean,
    isValid: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = isValid && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = stringResource(R.string.next)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(stringResource(R.string.next))
        }
    }
}
