package com.whalert.app.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

/**
 * Security utilities for the application
 */
object SecurityUtils {

    private const val KEY_STORE_NAME = "AndroidKeyStore"
    private const val KEY_ALIAS = "whalert_key"
    private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
    private const val ANDROID_KEY_STORE = "AndroidKeyStore"

    /**
     * Hashes a PIN using SHA-256
     */
    fun hashPin(pin: String): String {
        val bytes = pin.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return Base64.encodeToString(digest, Base64.NO_WRAP)
    }

    /**
     * Hashes a password using PBKDF2
     */
    fun hashPassword(password: String, salt: ByteArray = generateSalt()): String {
        val iterations = 10000
        val keyLength = 256
        
        val spec = javax.crypto.spec.PBEKeySpec(
            password.toCharArray(),
            salt,
            iterations,
            keyLength
        )
        
        val skf = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = skf.generateSecret(spec).encoded
        
        // Combine salt and hash for storage
        val combined = salt + hash
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    /**
     * Generates a random salt
     */
    fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        java.security.SecureRandom().nextBytes(salt)
        return salt
    }

    /**
     * Verifies a password against a stored hash
     */
    fun verifyPassword(password: String, storedHash: String): Boolean {
        try {
            val combined = Base64.decode(storedHash, Base64.NO_WRAP)
            val salt = combined.copyOfRange(0, 16)
            val hash = combined.copyOfRange(16, combined.size)
            
            val testHash = hashPassword(password, salt)
            return testHash == storedHash
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Encrypts data using AES
     */
    fun encrypt(data: String): String? {
        return try {
            val key = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            
            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            
            // Combine IV and encrypted data
            val combined = iv + encryptedBytes
            Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Decrypts data using AES
     */
    fun decrypt(encryptedData: String): String? {
        return try {
            val combined = Base64.decode(encryptedData, Base64.NO_WRAP)
            val ivSize = 16 // AES block size
            val iv = combined.copyOfRange(0, ivSize)
            val encryptedBytes = combined.copyOfRange(ivSize, combined.size)
            
            val key = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
            
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Gets or creates a secret key for encryption
     */
    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)
        
        if (keyStore.containsAlias(KEY_ALIAS)) {
            return keyStore.getKey(KEY_ALIAS, null) as SecretKey
        }
        
        // Create new key
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEY_STORE
        )
        
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setRandomizedEncryptionRequired(false)
            .build()
        
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    /**
     * Generates a secure random token
     */
    fun generateToken(): String {
        val random = java.security.SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    /**
     * Sanitizes input to prevent injection attacks
     */
    fun sanitizeInput(input: String): String {
        return input
            .replace("'", "''")
            .replace(";", "")
            .replace("--", "")
            .replace("/*", "")
            .replace("*/", "")
            .replace("xp_", "")
            .trim()
    }

    /**
     * Validates that a string doesn't contain SQL injection patterns
     */
    fun isSafeInput(input: String): Boolean {
        val patterns = listOf(
            "'",
            ";",
            "--",
            "/*",
            "*/",
            "xp_",
            "UNION SELECT",
            "DROP TABLE",
            "DELETE FROM",
            "INSERT INTO",
            "UPDATE ",
            "EXEC ",
            "EXECUTE ",
            "ALTER TABLE"
        )
        
        return patterns.none { pattern ->
            input.contains(pattern, ignoreCase = true)
        }
    }

    /**
     * Masks sensitive data for logging
     */
    fun maskSensitiveData(data: String): String {
        if (data.length <= 4) return "****"
        
        val visibleStart = data.take(2)
        val visibleEnd = data.takeLast(2)
        val maskedMiddle = "*".repeat(data.length - 4)
        
        return "$visibleStart$maskedMiddle$visibleEnd"
    }

    /**
     * Masks phone number for display
     */
    fun maskPhoneNumber(phoneNumber: String): String {
        val cleaned = phoneNumber.replace("[^\\d]".toRegex(), "")
        if (cleaned.length <= 4) return "****"
        
        val countryCode = PhoneNumberValidator.extractCountryCode(phoneNumber)
        val number = cleaned.removePrefix("+").removePrefix(countryCode ?: "")
        
        if (number.length <= 4) return "****"
        
        val visibleStart = number.take(2)
        val visibleEnd = number.takeLast(2)
        val maskedMiddle = "*".repeat(number.length - 4)
        
        return "+${countryCode ?: "XX"} $visibleStart$maskedMiddle$visibleEnd"
    }

    /**
     * Masks email for display
     */
    fun maskEmail(email: String): String {
        val parts = email.split("@")
        if (parts.size != 2) return "****"
        
        val localPart = parts[0]
        val domain = parts[1]
        
        val maskedLocal = when {
            localPart.length <= 2 -> "***"
            else -> localPart.take(2) + "*".repeat(localPart.length - 2)
        }
        
        val maskedDomain = when {
            domain.length <= 2 -> "***"
            else -> "*".repeat(domain.length - 2) + domain.takeLast(2)
        }
        
        return "$maskedLocal@$maskedDomain"
    }
}
