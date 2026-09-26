package com.whalert.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whalert.app.model.DailyLimitInfo
import com.whalert.app.model.Report
import com.whalert.app.model.ReportCategory
import com.whalert.app.model.ReportCreateRequest
import com.whalert.app.model.ReportStatus
import com.whalert.app.model.ReportSubmissionResponse
import com.whalert.app.model.ReportUpdateRequest
import com.whalert.app.repository.ReportRepository
import com.whalert.app.repository.UserRepository
import com.whalert.app.util.PhoneNumberValidator
import com.whalert.app.util.ValidationResult
import com.whalert.app.util.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel for managing reports
 */
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // State
    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    private val _currentReport = MutableStateFlow<Report?>(null)
    val currentReport: StateFlow<Report?> = _currentReport.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _validationErrors = MutableStateFlow<List<String>>(emptyList())
    val validationErrors: StateFlow<List<String>> = _validationErrors.asStateFlow()

    private val _validationWarnings = MutableStateFlow<List<String>>(emptyList())
    val validationWarnings: StateFlow<List<String>> = _validationWarnings.asStateFlow()

    private val _dailyLimitInfo = MutableStateFlow(DailyLimitInfo())
    val dailyLimitInfo: StateFlow<DailyLimitInfo> = _dailyLimitInfo.asStateFlow()

    private val _phoneNumberValidation = MutableStateFlow(ValidationResult(isValid = false))
    val phoneNumberValidation: StateFlow<ValidationResult> = _phoneNumberValidation.asStateFlow()

    private val _descriptionValidation = MutableStateFlow(ValidationResult(isValid = false))
    val descriptionValidation: StateFlow<ValidationResult> = _descriptionValidation.asStateFlow()

    private val _formValid = MutableStateFlow(false)
    val formValid: StateFlow<Boolean> = _formValid.asStateFlow()

    // Report filters
    private val _selectedCategory = MutableStateFlow<ReportCategory?>(null)
    val selectedCategory: StateFlow<ReportCategory?> = _selectedCategory.asStateFlow()

    private val _selectedStatus = MutableStateFlow<ReportStatus?>(null)
    val selectedStatus: StateFlow<ReportStatus?> = _selectedStatus.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Report creation form
    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _countryCode = MutableStateFlow("")
    val countryCode: StateFlow<String> = _countryCode.asStateFlow()

    private val _category = MutableStateFlow<ReportCategory>(ReportCategory.SPAM)
    val category: StateFlow<ReportCategory> = _category.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _evidenceText = MutableStateFlow("")
    val evidenceText: StateFlow<String> = _evidenceText.asStateFlow()

    private val _evidenceScreenshots = MutableStateFlow<List<String>>(emptyList())
    val evidenceScreenshots: StateFlow<List<String>> = _evidenceScreenshots.asStateFlow()

    private val _incidentDate = MutableStateFlow<Date?>(null)
    val incidentDate: StateFlow<Date?> = _incidentDate.asStateFlow()

    private val _additionalInfo = MutableStateFlow("")
    val additionalInfo: StateFlow<String> = _additionalInfo.asStateFlow()

    init {
        loadReports()
        loadDailyLimitInfo()
        
        // Combine validations
        viewModelScope.launch {
            combine(
                phoneNumberValidation,
                descriptionValidation
            ) { phoneValid, descValid ->
                phoneValid.isValid && descValid.isValid
            }.collect { isValid ->
                _formValid.value = isValid
            }
        }
    }

    /**
     * Loads all reports for the current user
     */
    fun loadReports() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val result = reportRepository.getUserReports()
                result.onSuccess { reports ->
                    _reports.value = reports
                }.onFailure { e ->
                    _error.value = e.message ?: "Erreur lors du chargement des signalements"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Loads reports with filters
     */
    fun loadReportsWithFilters() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                var result = reportRepository.getUserReports()
                
                result.onSuccess { reports ->
                    var filtered = reports
                    
                    _selectedCategory.value?.let { category ->
                        filtered = filtered.filter { it.category == category }
                    }
                    
                    _selectedStatus.value?.let { status ->
                        filtered = filtered.filter { it.status == status }
                    }
                    
                    if (_searchQuery.value.isNotBlank()) {
                        val query = _searchQuery.value.lowercase()
                        filtered = filtered.filter { report ->
                            report.phoneNumber.contains(query, ignoreCase = true) ||
                            report.description.contains(query, ignoreCase = true) ||
                            report.category.getDisplayName().contains(query, ignoreCase = true)
                        }
                    }
                    
                    _reports.value = filtered
                }.onFailure { e ->
                    _error.value = e.message ?: "Erreur lors du chargement des signalements"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Loads daily limit information
     */
    private fun loadDailyLimitInfo() {
        viewModelScope.launch {
            try {
                val userId = userRepository.getCurrentUserId()
                userId?.let { uid ->
                    val count = userRepository.getDailyReportCount(uid)
                    val limit = userRepository.getPreferences().getOrNull()?.dailyLimit ?: 3
                    
                    val calendar = java.util.Calendar.getInstance()
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                    calendar.set(java.util.Calendar.MINUTE, 0)
                    calendar.set(java.util.Calendar.SECOND, 0)
                    calendar.set(java.util.Calendar.MILLISECOND, 0)
                    
                    _dailyLimitInfo.value = DailyLimitInfo(
                        currentCount = count,
                        maxAllowed = limit,
                        resetTime = calendar.time,
                        isLimitReached = count >= limit
                    )
                }
            } catch (e: Exception) {
                // Use default values
                _dailyLimitInfo.value = DailyLimitInfo(
                    currentCount = 0,
                    maxAllowed = 3,
                    isLimitReached = false
                )
            }
        }
    }

    /**
     * Sets the phone number for validation
     */
    fun setPhoneNumber(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
        validatePhoneNumber(phoneNumber)
    }

    /**
     * Sets the country code
     */
    fun setCountryCode(countryCode: String) {
        _countryCode.value = countryCode
    }

    /**
     * Sets the category
     */
    fun setCategory(category: ReportCategory) {
        _category.value = category
    }

    /**
     * Sets the description
     */
    fun setDescription(description: String) {
        _description.value = description
        validateDescription(description)
    }

    /**
     * Sets evidence text
     */
    fun setEvidenceText(text: String) {
        _evidenceText.value = text
    }

    /**
     * Adds a screenshot
     */
    fun addScreenshot(uri: String) {
        if (_evidenceScreenshots.value.size < 10) {
            _evidenceScreenshots.value = _evidenceScreenshots.value + uri
        }
    }

    /**
     * Removes a screenshot
     */
    fun removeScreenshot(uri: String) {
        _evidenceScreenshots.value = _evidenceScreenshots.value.filter { it != uri }
    }

    /**
     * Sets incident date
     */
    fun setIncidentDate(date: Date?) {
        _incidentDate.value = date
    }

    /**
     * Sets additional info
     */
    fun setAdditionalInfo(info: String) {
        _additionalInfo.value = info
    }

    /**
     * Validates phone number
     */
    private fun validatePhoneNumber(phoneNumber: String) {
        viewModelScope.launch {
            _phoneNumberValidation.value = ValidationUtils.validatePhoneNumber(phoneNumber)
        }
    }

    /**
     * Validates description
     */
    private fun validateDescription(description: String) {
        viewModelScope.launch {
            _descriptionValidation.value = ValidationUtils.validateDescription(description)
        }
    }

    /**
     * Validates the entire form
     */
    fun validateForm(): Boolean {
        val phoneValid = ValidationUtils.validatePhoneNumber(_phoneNumber.value)
        val descValid = ValidationUtils.validateDescription(_description.value)
        
        _phoneNumberValidation.value = phoneValid
        _descriptionValidation.value = descValid
        _validationErrors.value = (phoneValid.errors + descValid.errors).distinct()
        _validationWarnings.value = (phoneValid.warnings + descValid.warnings).distinct()
        
        return phoneValid.isValid && descValid.isValid
    }

    /**
     * Creates a new report
     */
    fun createReport(): Result<Report> {
        val phoneValid = ValidationUtils.validatePhoneNumber(_phoneNumber.value)
        val descValid = ValidationUtils.validateDescription(_description.value)
        
        val allErrors = (phoneValid.errors + descValid.errors).distinct()
        val allWarnings = (phoneValid.warnings + descValid.warnings).distinct()
        
        if (!phoneValid.isValid || !descValid.isValid) {
            _validationErrors.value = allErrors
            _validationWarnings.value = allWarnings
            return Result.failure(ValidationException(allErrors.joinToString(", ")))
        }

        // Check daily limit
        if (_dailyLimitInfo.value.isLimitReached) {
            _error.value = "Limite quotidienne atteinte: ${_dailyLimitInfo.value.maxAllowed} signalements max"
            return Result.failure(DailyLimitException("Daily limit reached"))
        }

        // Clean phone number
        val cleanedPhone = PhoneNumberValidator.cleanToE164(_phoneNumber.value)
            ?: return Result.failure(ValidationException("Invalid phone number format"))

        val countryCode = PhoneNumberValidator.extractCountryCode(cleanedPhone)
            ?: return Result.failure(ValidationException("Could not determine country code"))

        val request = ReportCreateRequest(
            phoneNumber = _phoneNumber.value,
            countryCode = countryCode,
            category = _category.value,
            description = _description.value,
            evidenceText = _evidenceText.value.takeIf { it.isNotBlank() },
            evidenceScreenshots = _evidenceScreenshots.value.takeIf { it.isNotEmpty() },
            incidentDate = _incidentDate.value,
            additionalInfo = _additionalInfo.value.takeIf { it.isNotBlank() }
        )

        var result: Result<Report> = Result.failure(Exception("Unknown error"))
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                result = reportRepository.createReport(request)
                result.onSuccess { report ->
                    _currentReport.value = report
                    _successMessage.value = "Signalement créé avec succès"
                    loadReports()
                    loadDailyLimitInfo()
                    
                    // Reset form
                    resetForm()
                }.onFailure { e ->
                    _error.value = e.message ?: "Erreur lors de la création du signalement"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            } finally {
                _isLoading.value = false
            }
        }

        return result
    }

    /**
     * Updates a report
     */
    fun updateReport(report: Report): Result<Report> {
        var result: Result<Report> = Result.failure(Exception("Unknown error"))
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                result = reportRepository.updateReport(report)
                result.onSuccess { updatedReport ->
                    _currentReport.value = updatedReport
                    _successMessage.value = "Signalement mis à jour"
                    loadReports()
                }.onFailure { e ->
                    _error.value = e.message ?: "Erreur lors de la mise à jour"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            } finally {
                _isLoading.value = false
            }
        }

        return result
    }

    /**
     * Submits a report
     */
    fun submitReport(reportId: Long): Result<ReportSubmissionResponse> {
        var result: Result<ReportSubmissionResponse> = Result.failure(Exception("Unknown error"))
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                result = reportRepository.submitReport(reportId)
                result.onSuccess { response ->
                    _successMessage.value = response.message ?: "Signalement soumis"
                    loadReports()
                }.onFailure { e ->
                    _error.value = e.message ?: "Erreur lors de la soumission"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            } finally {
                _isLoading.value = false
            }
        }

        return result
    }

    /**
     * Deletes a report
     */
    fun deleteReport(reportId: Long): Result<Boolean> {
        var result: Result<Boolean> = Result.failure(Exception("Unknown error"))
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                result = reportRepository.deleteReport(reportId)
                result.onSuccess { success ->
                    if (success) {
                        _successMessage.value = "Signalement supprimé"
                        loadReports()
                    }
                }.onFailure { e ->
                    _error.value = e.message ?: "Erreur lors de la suppression"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            } finally {
                _isLoading.value = false
            }
        }

        return result
    }

    /**
     * Gets a report by ID
     */
    fun getReportById(reportId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                val result = reportRepository.getReportById(reportId)
                result.onSuccess { report ->
                    _currentReport.value = report
                }.onFailure { e ->
                    _error.value = e.message ?: "Signalement introuvable"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur inconnue"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Sets the current report
     */
    fun setCurrentReport(report: Report?) {
        _currentReport.value = report
        
        report?.let {
            _phoneNumber.value = it.phoneNumber
            _countryCode.value = it.countryCode
            _category.value = it.category
            _description.value = it.description
            _evidenceText.value = it.evidenceText ?: ""
            _evidenceScreenshots.value = it.evidenceScreenshots ?: emptyList()
            _incidentDate.value = it.incidentDate
            _additionalInfo.value = it.additionalInfo ?: ""
        }
    }

    /**
     * Resets the form
     */
    fun resetForm() {
        _phoneNumber.value = ""
        _countryCode.value = ""
        _category.value = ReportCategory.SPAM
        _description.value = ""
        _evidenceText.value = ""
        _evidenceScreenshots.value = emptyList()
        _incidentDate.value = null
        _additionalInfo.value = ""
        _phoneNumberValidation.value = ValidationResult(isValid = false)
        _descriptionValidation.value = ValidationResult(isValid = false)
        _validationErrors.value = emptyList()
        _validationWarnings.value = emptyList()
        _currentReport.value = null
    }

    /**
     * Sets category filter
     */
    fun setCategoryFilter(category: ReportCategory?) {
        _selectedCategory.value = category
        loadReportsWithFilters()
    }

    /**
     * Sets status filter
     */
    fun setStatusFilter(status: ReportStatus?) {
        _selectedStatus.value = status
        loadReportsWithFilters()
    }

    /**
     * Sets search query
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        loadReportsWithFilters()
    }

    /**
     * Clears filters
     */
    fun clearFilters() {
        _selectedCategory.value = null
        _selectedStatus.value = null
        _searchQuery.value = ""
        loadReports()
    }

    /**
     * Generates report text for preview
     */
    fun generateReportText(): String {
        val report = Report(
            id = 0,
            userId = userRepository.getCurrentUserId(),
            phoneNumber = _phoneNumber.value,
            countryCode = _countryCode.value,
            fullPhoneNumber = PhoneNumberValidator.cleanToE164(_phoneNumber.value) ?: _phoneNumber.value,
            category = _category.value,
            description = _description.value,
            evidenceText = _evidenceText.value.takeIf { it.isNotBlank() },
            evidenceScreenshots = _evidenceScreenshots.value.takeIf { it.isNotEmpty() },
            incidentDate = _incidentDate.value,
            additionalInfo = _additionalInfo.value.takeIf { it.isNotBlank() },
            status = ReportStatus.DRAFT
        )
        
        return reportRepository.generateReportText(report)
    }

    /**
     * Clears error
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Clears success message
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    // Exception classes
    class ValidationException(message: String) : Exception(message)
    class DailyLimitException(message: String) : Exception(message)
}
