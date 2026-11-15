package com.example.data.model

import java.security.MessageDigest

object ParentalControlHelper {
    private const val SALT = "slevany_parental_v1:"

    fun isValidPinFormat(pin: String): Boolean {
        val clean = pin.trim()
        return clean.length in 4..8 && clean.all { it.isDigit() }
    }

    fun hashPin(pin: String): String {
        val clean = pin.trim()
        val digest = MessageDigest.getInstance("SHA-256")
            .digest((SALT + clean).toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun verifyPin(enteredPin: String, storedHash: String?): Boolean {
        if (storedHash.isNullOrBlank()) return false
        val clean = enteredPin.trim()
        if (clean.isEmpty()) return false
        return hashPin(clean) == storedHash
    }
}
