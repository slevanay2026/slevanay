package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.model.ParentalControlHelper
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.BroadcastCoral
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SlateSurface
import kotlinx.coroutines.launch

enum class ParentalPinDialogMode {
    CREATE,
    UNLOCK,
    CHANGE
}

@Composable
fun ParentalPinDialog(
    mode: ParentalPinDialogMode,
    onDismiss: () -> Unit,
    onSavePin: (String) -> Boolean,
    onVerifyPin: suspend (String) -> Boolean
) {
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()
    var pinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isVerifying by remember { mutableStateOf(false) }

    val title = when (mode) {
        ParentalPinDialogMode.CREATE -> strings.parentalPinDialogCreateTitle
        ParentalPinDialogMode.UNLOCK -> strings.parentalPinDialogUnlockTitle
        ParentalPinDialogMode.CHANGE -> strings.parentalPinDialogChangeTitle
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MidnightNavy,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (mode == ParentalPinDialogMode.UNLOCK) Icons.Rounded.Lock else Icons.Rounded.Shield,
                    contentDescription = null,
                    tint = ElectricCyan
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { raw ->
                        pinInput = raw.filter { it.isDigit() }.take(8)
                        errorMsg = null
                    },
                    label = { Text(strings.parentalPinLabel) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SlateSurface,
                        unfocusedContainerColor = SlateSurface,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = GlassBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("parental_pin_input")
                )

                if (mode != ParentalPinDialogMode.UNLOCK) {
                    OutlinedTextField(
                        value = confirmPinInput,
                        onValueChange = { raw ->
                            confirmPinInput = raw.filter { it.isDigit() }.take(8)
                            errorMsg = null
                        },
                        label = { Text(strings.parentalPinConfirmLabel) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SlateSurface,
                            unfocusedContainerColor = SlateSurface,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = GlassBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parental_pin_confirm_input")
                    )
                }

                if (errorMsg != null) {
                    Text(
                        text = errorMsg!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = BroadcastCoral
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isVerifying,
                onClick = {
                    if (mode == ParentalPinDialogMode.UNLOCK) {
                        if (pinInput.isBlank()) {
                            errorMsg = strings.parentalPinInvalidError
                            return@Button
                        }
                        scope.launch {
                            isVerifying = true
                            val ok = onVerifyPin(pinInput)
                            isVerifying = false
                            if (ok) {
                                onDismiss()
                            } else {
                                errorMsg = strings.parentalPinWrongError
                            }
                        }
                    } else {
                        if (!ParentalControlHelper.isValidPinFormat(pinInput)) {
                            errorMsg = strings.parentalPinInvalidError
                            return@Button
                        }
                        if (pinInput != confirmPinInput) {
                            errorMsg = strings.parentalPinMismatchError
                            return@Button
                        }
                        if (onSavePin(pinInput)) {
                            onDismiss()
                        } else {
                            errorMsg = strings.parentalPinInvalidError
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricCyan,
                    contentColor = DeepObsidian
                ),
                modifier = Modifier.testTag("parental_pin_submit_btn")
            ) {
                Text(
                    text = if (mode == ParentalPinDialogMode.UNLOCK) {
                        strings.parentalUnlockBtn
                    } else {
                        strings.parentalSaveBtn
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancelBtn, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
