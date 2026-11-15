package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.LanguageFlagIcon
import com.example.ui.components.TvFocusableCard
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.BroadcastCoral
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.IndigoGlow
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SlateSurface

@Composable
fun ActivationScreen(
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onActivate: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var codeInput by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var langMenuExpanded by remember { mutableStateOf(false) }

    fun submitCode() {
        val ok = onActivate(codeInput)
        if (!ok) {
            showError = true
        }
    }

    fun appendDigit(digit: String) {
        if (codeInput.length < 4) {
            codeInput += digit
            showError = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        IndigoGlow.copy(alpha = 0.22f),
                        MidnightNavy,
                        DeepObsidian
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .testTag("activation_screen")
    ) {
        // Top-right language switcher so first-time users can pick their language immediately
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            OutlinedButton(
                onClick = { langMenuExpanded = true },
                border = BorderStroke(1.dp, GlassBorder),
                modifier = Modifier.testTag("activation_language_button")
            ) {
                LanguageFlagIcon(
                    language = currentLanguage,
                    width = 24.dp,
                    height = 16.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentLanguage.nativeName,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            DropdownMenu(
                expanded = langMenuExpanded,
                onDismissRequest = { langMenuExpanded = false }
            ) {
                AppLanguage.entries.forEach { lang ->
                    DropdownMenuItem(
                        leadingIcon = {
                            LanguageFlagIcon(
                                language = lang,
                                width = 28.dp,
                                height = 18.dp
                            )
                        },
                        text = {
                            Text(
                                text = "${lang.nativeName} (${lang.displayName})",
                                fontWeight = if (lang == currentLanguage) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            onSelectLanguage(lang)
                            langMenuExpanded = false
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp),
                shape = RoundedCornerShape(26.dp),
                color = MidnightNavy.copy(alpha = 0.94f),
                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.45f)),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF101E39),
                            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.55f)),
                            modifier = Modifier.size(58.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_icon_foreground),
                                contentDescription = "JHE HAKAR Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }
                        Column {
                            Text(
                                text = "JHE HAKAR",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = strings.heroBadge,
                                style = MaterialTheme.typography.labelMedium,
                                color = ElectricCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SlateSurface,
                        border = BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.activationTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = strings.activationSubtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // 4-Digit Visual Code Slots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(4) { index ->
                            val isFilled = index < codeInput.length
                            val isActive = index == codeInput.length && codeInput.length < 4
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SlateSurface,
                                border = BorderStroke(
                                    width = if (isActive || isFilled) 2.dp else 1.dp,
                                    color = when {
                                        showError -> BroadcastCoral
                                        isFilled || isActive -> ElectricCyan
                                        else -> GlassBorder
                                    }
                                ),
                                modifier = Modifier.size(width = 58.dp, height = 62.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (isFilled) "●" else "—",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isFilled) ElectricCyan else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Keyboard / IME input for phones or external keyboards
                    OutlinedTextField(
                        value = codeInput,
                        onValueChange = { raw ->
                            codeInput = raw.filter { it.isDigit() }.take(4)
                            showError = false
                        },
                        label = { Text(strings.activationCodeLabel) },
                        placeholder = { Text(strings.activationCodeHint) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { submitCode() }),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SlateSurface,
                            unfocusedContainerColor = SlateSurface,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = GlassBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("activation_code_input")
                    )

                    AnimatedVisibility(visible = showError) {
                        Text(
                            text = strings.activationInvalidError,
                            style = MaterialTheme.typography.bodyMedium,
                            color = BroadcastCoral,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("activation_error_text")
                        )
                    }

                    // Smart TV D-Pad Friendly Numeric Keypad
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val rows = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("CLR", "0", "DEL")
                        )
                        rows.forEach { rowKeys ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowKeys.forEach { key ->
                                    TvFocusableCard(
                                        onClick = {
                                            when (key) {
                                                "CLR" -> {
                                                    codeInput = ""
                                                    showError = false
                                                }
                                                "DEL" -> {
                                                    if (codeInput.isNotEmpty()) {
                                                        codeInput = codeInput.dropLast(1)
                                                    }
                                                    showError = false
                                                }
                                                else -> appendDigit(key)
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        containerColor = SlateSurface,
                                        modifier = Modifier
                                            .weight(1f)
                                            .heightIn(min = 48.dp)
                                            .testTag("activation_key_$key")
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            when (key) {
                                                "DEL" -> Icon(
                                                    imageVector = Icons.AutoMirrored.Rounded.Backspace,
                                                    contentDescription = strings.activationClearBtn,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                "CLR" -> Text(
                                                    text = strings.activationClearBtn,
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                else -> Text(
                                                    text = key,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { submitCode() },
                        enabled = codeInput.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = DeepObsidian
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .testTag("activation_submit_button")
                    ) {
                        Icon(Icons.Rounded.VerifiedUser, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.activationSubmitBtn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = ElectricCyan.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = strings.activationOneTimeNote,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
