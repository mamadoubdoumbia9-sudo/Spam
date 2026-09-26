package com.whalert.app.util

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import java.util.regex.Pattern

/**
 * Utility class for validating phone numbers in E.164 format
 * Uses Google's libphonenumber for accurate validation
 */
object PhoneNumberValidator {

    private val phoneNumberUtil: PhoneNumberUtil by lazy { PhoneNumberUtil.getInstance() }

    // E.164 format: + followed by country code (1-3 digits) and subscriber number
    // Total length: 1-15 digits (excluding the +)
    private val E164_PATTERN = Pattern.compile("^\\+[1-9]\\d{1,14}"")

    // More permissive pattern that allows spaces and hyphens
    private val FORMATTED_PATTERN = Pattern.compile("^\\+[1-9]\\d{1,14}$|^\\+[1-9]\\d{1,2}[-\\\\s]\\d{4,14}$")

    // Country code patterns
    private val COUNTRY_CODES = mapOf(
        "US" to "1",
        "CA" to "1",
        "GB" to "44",
        "FR" to "33",
        "DE" to "49",
        "IT" to "39",
        "ES" to "34",
        "IN" to "91",
        "CN" to "86",
        "JP" to "81",
        "BR" to "55",
        "RU" to "7",
        "AU" to "61",
        "ZA" to "27",
        "NG" to "234",
        "KE" to "254",
        "GH" to "233",
        "SN" to "221",
        "CI" to "225",
        "ML" to "223",
        "MA" to "212"
    )

    /**
     * Validates if a phone number is in E.164 format using regex
     */
    fun isValidE164(phoneNumber: String): Boolean {
        return E164_PATTERN.matcher(phoneNumber).matches()
    }

    /**
     * Validates if a phone number is valid using Google's libphonenumber
     */
    fun isValidWithLibphonenumber(phoneNumber: String, defaultRegion: String = "US"): Boolean {
        return try {
            val parsedNumber = phoneNumberUtil.parse(phoneNumber, defaultRegion)
            phoneNumberUtil.isValidNumber(parsedNumber)
        } catch (e: NumberParseException) {
            false
        }
    }

    /**
     * Validates if a phone number is valid (allows formatted versions)
     */
    fun isValid(phoneNumber: String): Boolean {
        // Remove all non-digit characters except +
        val cleaned = phoneNumber.replace("[^\\d+]".toRegex(), "")
        return isValidE164(cleaned)
    }

    /**
     * Cleans a phone number to E.164 format using libphonenumber
     */
    fun cleanToE164(phoneNumber: String): String? {
        return try {
            val parsedNumber = phoneNumberUtil.parse(phoneNumber, null)
            val formatted = phoneNumberUtil.format(parsedNumber, Phonenumber.PhoneNumberFormat.E164)
            if (isValidE164(formatted)) formatted else null
        } catch (e: NumberParseException) {
            // Fallback to regex-based cleaning
            val digitsOnly = phoneNumber.replace("[^\\d]".toRegex(), "")
            
            if (digitsOnly.startsWith("00")) {
                val withoutPrefix = digitsOnly.substring(2)
                return if (isValidE164("+$withoutPrefix")) "+$withoutPrefix" else null
            }
            
            if (digitsOnly.startsWith("+")) {
                return if (isValidE164(digitsOnly)) digitsOnly else null
            }
            
            null
        }
    }

    /**
     * Formats phone number to E.164 format with default region
     */
    fun formatToE164(phoneNumber: String, defaultRegion: String = "US"): String? {
        return try {
            val parsedNumber = phoneNumberUtil.parse(phoneNumber, defaultRegion)
            phoneNumberUtil.format(parsedNumber, Phonenumber.PhoneNumberFormat.E164)
        } catch (e: NumberParseException) {
            null
        }
    }

    /**
     * Extracts country code from phone number
     */
    fun extractCountryCode(phoneNumber: String): String? {
        val cleaned = phoneNumber.replace("[^\\d]".toRegex(), "")
        
        if (cleaned.startsWith("00")) {
            val withoutPrefix = cleaned.substring(2)
            return when {
                withoutPrefix.startsWith("1") -> "1" // US/CA
                withoutPrefix.startsWith("44") -> "44" // UK
                withoutPrefix.startsWith("33") -> "33" // France
                withoutPrefix.startsWith("39") -> "39" // Italy
                withoutPrefix.startsWith("34") -> "34" // Spain
                withoutPrefix.startsWith("91") -> "91" // India
                withoutPrefix.startsWith("86") -> "86" // China
                withoutPrefix.startsWith("81") -> "81" // Japan
                withoutPrefix.startsWith("55") -> "55" // Brazil
                withoutPrefix.startsWith("7") -> "7" // Russia
                withoutPrefix.startsWith("61") -> "61" // Australia
                withoutPrefix.startsWith("27") -> "27" // South Africa
                withoutPrefix.startsWith("234") -> "234" // Nigeria
                withoutPrefix.startsWith("254") -> "254" // Kenya
                withoutPrefix.startsWith("233") -> "233" // Ghana
                withoutPrefix.startsWith("221") -> "221" // Senegal
                withoutPrefix.startsWith("225") -> "225" // Ivory Coast
                withoutPrefix.startsWith("223") -> "223" // Mali
                withoutPrefix.startsWith("212") -> "212" // Morocco
                else -> null
            }
        }
        
        if (cleaned.startsWith("+")) {
            val withoutPlus = cleaned.substring(1)
            return when {
                withoutPlus.startsWith("1") -> "1"
                withoutPlus.startsWith("44") -> "44"
                withoutPlus.startsWith("33") -> "33"
                withoutPlus.startsWith("39") -> "39"
                withoutPlus.startsWith("34") -> "34"
                withoutPlus.startsWith("91") -> "91"
                withoutPlus.startsWith("86") -> "86"
                withoutPlus.startsWith("81") -> "81"
                withoutPlus.startsWith("55") -> "55"
                withoutPlus.startsWith("7") -> "7"
                withoutPlus.startsWith("61") -> "61"
                withoutPlus.startsWith("27") -> "27"
                withoutPlus.startsWith("234") -> "234"
                withoutPlus.startsWith("254") -> "254"
                withoutPlus.startsWith("233") -> "233"
                withoutPlus.startsWith("221") -> "221"
                withoutPlus.startsWith("225") -> "225"
                withoutPlus.startsWith("223") -> "223"
                withoutPlus.startsWith("212") -> "212"
                else -> null
            }
        }
        
        return null
    }

    /**
     * Gets country name from country code using libphonenumber
     */
    fun getCountryName(countryCode: String): String {
        return try {
            val regionCode = phoneNumberUtil.getRegionCodeForCountryCode(countryCode.toInt())
            if (regionCode != null && regionCode.isNotEmpty()) {
                return regionCode
            }
        } catch (e: Exception) {
            // Fallback to manual mapping
        }
        
        return when (countryCode) {
            "1" -> "États-Unis/Canada"
            "44" -> "Royaume-Uni"
            "33" -> "France"
            "49" -> "Allemagne"
            "39" -> "Italie"
            "34" -> "Espagne"
            "91" -> "Inde"
            "86" -> "Chine"
            "81" -> "Japon"
            "55" -> "Brésil"
            "7" -> "Russie"
            "61" -> "Australie"
            "27" -> "Afrique du Sud"
            "234" -> "Nigeria"
            "254" -> "Kenya"
            "233" -> "Ghana"
            "221" -> "Sénégal"
            "225" -> "Côte d'Ivoire"
            "223" -> "Mali"
            "212" -> "Maroc"
            else -> "Inconnu"
        }
    }

    /**
     * Gets region code for a phone number
     */
    fun getRegionCode(phoneNumber: String): String? {
        return try {
            val parsedNumber = phoneNumberUtil.parse(phoneNumber, null)
            phoneNumberUtil.getRegionCodeForNumber(parsedNumber)
        } catch (e: NumberParseException) {
            null
        }
    }

    /**
     * Formats phone number for display
     */
    fun formatForDisplay(phoneNumber: String): String {
        val cleaned = phoneNumber.replace("[^\\d+]".toRegex(), "")
        
        if (cleaned.length <= 4) return cleaned
        
        return when {
            cleaned.startsWith("+") -> {
                val countryCode = extractCountryCode(cleaned)
                val number = cleaned.substring(countryCode?.length?.plus(1) ?: 1)
                
                when (countryCode) {
                    "1" -> formatUSNumber(number) // US/Canada
                    "44" -> formatUKNumber(number) // UK
                    "33" -> formatFRNumber(number) // France
                    else -> formatInternationalNumber(cleaned)
                }
            }
            else -> cleaned
        }
    }

    private fun formatUSNumber(number: String): String {
        if (number.length != 10) return number
        return "(${number.substring(0, 3)}) ${number.substring(3, 6)}-${number.substring(6)}"
    }

    private fun formatUKNumber(number: String): String {
        if (number.length == 10) {
            return "${number.substring(0, 5)} ${number.substring(5)}"
        }
        return number
    }

    private fun formatFRNumber(number: String): String {
        if (number.length == 9) {
            return "${number.substring(0, 1)} ${number.substring(1, 3)} ${number.substring(3, 5)} ${number.substring(5, 7)} ${number.substring(7)}"
        }
        return number
    }

    private fun formatInternationalNumber(number: String): String {
        // Group by 3 digits from the end
        val cleaned = number.replace("[^\\d]".toRegex(), "")
        if (cleaned.length <= 4) return cleaned
        
        val sb = StringBuilder()
        var i = 0
        
        // Add country code
        while (i < cleaned.length && cleaned[i] != '+' && i < 3) {
            sb.append(cleaned[i])
            i++
        }
        
        if (i < cleaned.length && cleaned[i] == '+') {
            sb.append('+')
            i++
        }
        
        // Add spaces every 3 digits
        var count = 0
        while (i < cleaned.length) {
            if (count == 3) {
                sb.append(' ')
                count = 0
            }
            sb.append(cleaned[i])
            i++
            count++
        }
        
        return sb.toString()
    }

    /**
     * Checks if phone number is from a specific country
     */
    fun isFromCountry(phoneNumber: String, countryCode: String): Boolean {
        val extracted = extractCountryCode(phoneNumber)
        return extracted == countryCode
    }

    /**
     * Validates that the phone number has a valid length for its country
     */
    fun hasValidLength(phoneNumber: String): Boolean {
        val countryCode = extractCountryCode(phoneNumber) ?: return true
        val number = phoneNumber.replace("[^\\d]".toRegex(), "").removePrefix("00").removePrefix("+")
        
        val minLength = when (countryCode) {
            "1" -> 10 // US/Canada: 10 digits
            "44" -> 9 // UK: 9-10 digits
            "33" -> 9 // France: 9 digits
            "49" -> 4 // Germany: 4-11 digits
            "39" -> 5 // Italy: 5-10 digits
            "34" -> 9 // Spain: 9 digits
            "91" -> 10 // India: 10 digits
            "86" -> 5 // China: 5-12 digits
            "81" -> 4 // Japan: 4-10 digits
            "55" -> 8 // Brazil: 8-9 digits
            "7" -> 10 // Russia: 10 digits
            "61" -> 9 // Australia: 9 digits
            "27" -> 9 // South Africa: 9 digits
            "234" -> 10 // Nigeria: 10 digits
            "254" -> 9 // Kenya: 9 digits
            "233" -> 9 // Ghana: 9 digits
            "221" -> 9 // Senegal: 9 digits
            "225" -> 10 // Ivory Coast: 10 digits
            "223" -> 8 // Mali: 8 digits
            "212" -> 9 // Morocco: 9 digits
            else -> 4 // Default minimum
        }
        
        val maxLength = when (countryCode) {
            "1" -> 15
            "44" -> 15
            "33" -> 15
            "49" -> 15
            "39" -> 15
            "34" -> 15
            "91" -> 15
            "86" -> 15
            "81" -> 15
            "55" -> 15
            "7" -> 15
            "61" -> 15
            "27" -> 15
            "234" -> 15
            "254" -> 15
            "233" -> 15
            "221" -> 15
            "225" -> 15
            "223" -> 15
            "212" -> 15
            else -> 15
        }
        
        return number.length in minLength..maxLength
    }
}
