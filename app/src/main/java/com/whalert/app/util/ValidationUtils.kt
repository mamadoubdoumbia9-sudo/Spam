package com.whalert.app.util

import com.whalert.app.model.ReportCreateRequest
import java.util.Date

/**
 * Utility class for validating report data
 */
object ValidationUtils {

    /**
     * Validates a report creation request
     */
    fun validateReportRequest(request: ReportCreateRequest): ValidationResult {
        val errors = mutableListOf<String>()

        // Validate phone number
        if (request.phoneNumber.isBlank()) {
            errors.add("Le numéro de téléphone est requis")
        } else if (!PhoneNumberValidator.isValid(request.phoneNumber)) {
            errors.add("Format de numéro de téléphone invalide. Utilisez le format international (ex: +223XXXXXXXX)")
        }

        // Validate country code
        if (request.countryCode.isBlank()) {
            errors.add("Le code pays est requis")
        }

        // Validate category
        // Category is an enum, so it's always valid if set

        // Validate description
        if (request.description.isBlank()) {
            errors.add("La description est requise")
        } else if (request.description.length < 10) {
            errors.add("La description doit contenir au moins 10 caractères")
        } else if (request.description.length > 5000) {
            errors.add("La description ne peut pas dépasser 5000 caractères")
        }

        // Validate evidence text if provided
        request.evidenceText?.let { text ->
            if (text.length > 2000) {
                errors.add("Le texte des preuves ne peut pas dépasser 2000 caractères")
            }
        }

        // Validate additional info if provided
        request.additionalInfo?.let { info ->
            if (info.length > 1000) {
                errors.add("Les informations complémentaires ne peuvent pas dépasser 1000 caractères")
            }
        }

        // Validate screenshots count
        request.evidenceScreenshots?.let { screenshots ->
            if (screenshots.size > 10) {
                errors.add("Maximum 10 captures d'écran autorisées")
            }
        }

        // Validate incident date if provided
        request.incidentDate?.let { date ->
            if (date.after(Date())) {
                errors.add("La date de l'incident ne peut pas être dans le futur")
            }
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = getWarnings(request)
        )
    }

    /**
     * Gets warnings for a report request (non-blocking issues)
     */
    private fun getWarnings(request: ReportCreateRequest): List<String> {
        val warnings = mutableListOf<String>()

        // Check if phone number might be the user's own
        // This is just a warning, not an error

        // Check if description contains potentially problematic content
        val descriptionLower = request.description.lowercase()
        val problematicTerms = listOf("menace", "chantage", "tuer", "mourir", "viol")
        
        problematicTerms.forEach { term ->
            if (descriptionLower.contains(term)) {
                warnings.add("Votre description contient le terme '$term'. Assurez-vous que votre signalement est factuel et approprié.")
            }
        }

        // Check if evidence is missing
        if (request.evidenceText.isNullOrBlank() && 
            (request.evidenceScreenshots == null || request.evidenceScreenshots?.isEmpty() == true)) {
            warnings.add("Ajouter des preuves (captures d'écran ou texte) peut renforcer votre signalement")
        }

        // Check if incident date is missing
        if (request.incidentDate == null) {
            warnings.add("Ajouter la date et l'heure de l'incident peut aider WhatsApp à investiguer")
        }

        return warnings
    }

    /**
     * Validates phone number specifically
     */
    fun validatePhoneNumber(phoneNumber: String): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        if (phoneNumber.isBlank()) {
            errors.add("Le numéro de téléphone est requis")
            return ValidationResult(false, errors, warnings)
        }

        val cleaned = PhoneNumberValidator.cleanToE164(phoneNumber)
        if (cleaned == null) {
            errors.add("Impossible de formater le numéro de téléphone. Utilisez le format international.")
            return ValidationResult(false, errors, warnings)
        }

        if (!PhoneNumberValidator.isValidE164(cleaned)) {
            errors.add("Numéro de téléphone invalide. Utilisez le format E.164 (ex: +223XXXXXXXX)")
            return ValidationResult(false, errors, warnings)
        }

        val countryCode = PhoneNumberValidator.extractCountryCode(phoneNumber)
        if (countryCode == null) {
            warnings.add("Impossible de déterminer le code pays. Vérifiez que le numéro est complet.")
        } else if (!PhoneNumberValidator.hasValidLength(phoneNumber)) {
            warnings.add("Le numéro de téléphone peut avoir une longueur incorrecte pour le pays $countryCode")
        }

        // Important disclaimer
        warnings.add("Un numéro valide ne signifie pas qu'il possède nécessairement un compte WhatsApp.")
        warnings.add("Impossible de confirmer publiquement l'état de ce compte avec les informations disponibles.")

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings,
            cleanedValue = cleaned
        )
    }

    /**
     * Validates description
     */
    fun validateDescription(description: String): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        if (description.isBlank()) {
            errors.add("La description est requise")
            return ValidationResult(false, errors, warnings)
        }

        if (description.length < 10) {
            errors.add("La description doit contenir au moins 10 caractères")
        }

        if (description.length > 5000) {
            errors.add("La description ne peut pas dépasser 5000 caractères")
        }

        // Check for potentially problematic content
        val descriptionLower = description.lowercase()
        val problematicTerms = listOf(
            "menace", "chantage", "tuer", "mourir", "viol",
            "arnaque", "fraude", "escroquerie", "vol",
            "haine", "raciste", "discrimination"
        )
        
        problematicTerms.forEach { term ->
            if (descriptionLower.contains(term)) {
                warnings.add("Votre description contient le terme '$term'. Assurez-vous que votre signalement est factuel, précis et sans accusation non étayée.")
            }
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }

    /**
     * Validates evidence
     */
    fun validateEvidence(
        text: String? = null,
        screenshots: List<String>? = null
    ): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        text?.let { evidenceText ->
            if (evidenceText.length > 2000) {
                errors.add("Le texte des preuves ne peut pas dépasser 2000 caractères")
            }
        }

        screenshots?.let { screenshotList ->
            if (screenshotList.size > 10) {
                errors.add("Maximum 10 captures d'écran autorisées")
            }
            
            screenshotList.forEach { uri ->
                if (uri.isBlank()) {
                    errors.add("URI de capture d'écran invalide")
                }
            }
        }

        // Legal consideration
        warnings.add("Assurez-vous que les preuves que vous fournissez sont légalement appropriées et ne violent pas les droits d'autrui.")

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }

    /**
     * Validates incident date
     */
    fun validateIncidentDate(date: Date?): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        date?.let { incidentDate ->
            if (incidentDate.after(Date())) {
                errors.add("La date de l'incident ne peut pas être dans le futur")
            }
            
            // Check if date is too old (more than 1 year)
            val oneYearAgo = Date(System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000)
            if (incidentDate.before(oneYearAgo)) {
                warnings.add("L'incident s'est produit il y a plus d'un an. Les signalements pour des incidents anciens peuvent être moins efficaces.")
            }
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }

    /**
     * Validates daily limit
     */
    fun validateDailyLimit(currentCount: Int, maxAllowed: Int): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        if (currentCount >= maxAllowed) {
            errors.add("Limite quotidienne atteinte. Vous avez atteint le maximum de $maxAllowed signalements par 24 heures.")
        }

        if (currentCount >= maxAllowed - 1) {
            warnings.add("Vous approchez de la limite quotidienne (${maxAllowed - currentCount} signalement(s) restant(s)).")
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }
}

/**
 * Result of a validation operation
 */
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList(),
    val cleanedValue: String? = null
)
