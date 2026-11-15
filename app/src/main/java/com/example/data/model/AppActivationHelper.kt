package com.example.data.model

object AppActivationHelper {
    const val REQUIRED_ACTIVATION_CODE = "4312"

    fun verifyCode(input: String): Boolean {
        return input.trim() == REQUIRED_ACTIVATION_CODE
    }
}
