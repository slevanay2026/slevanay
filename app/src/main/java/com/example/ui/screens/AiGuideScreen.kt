package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ai.AiUiState
import com.example.ui.components.TvFocusableCard
import com.example.ui.components.TvSectionHeader
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.BroadcastCoral
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SlateSurface

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiGuideScreen(
    uiState: AiUiState,
    channelCount: Int,
    vodCount: Int,
    seriesCount: Int,
    onAskPrompt: (String) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var promptInput by remember { mutableStateOf("") }
    var lastSubmittedPrompt by remember { mutableStateOf("") }

    val quickPrompts = strings.aiQuickPrompts

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MidnightNavy,
                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.45f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ElectricCyan.copy(alpha = 0.16f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.aiTitle,
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                            Text(
                                text = "${strings.aiCatalogSummaryPrefix}: $channelCount ${strings.channelCountSuffix} • $vodCount ${strings.statMoviesVod} • $seriesCount ${strings.statSeries}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ElectricCyan
                            )
                        }
                    }

                    Text(
                        text = strings.aiSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        quickPrompts.forEach { prompt ->
                            TvFocusableCard(
                                onClick = {
                                    lastSubmittedPrompt = prompt
                                    onAskPrompt(prompt)
                                },
                                shape = RoundedCornerShape(12.dp),
                                containerColor = SlateSurface
                            ) {
                                Text(
                                    text = prompt,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = promptInput,
                            onValueChange = { promptInput = it },
                            placeholder = { Text(strings.aiInputPlaceholder) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SlateSurface,
                                unfocusedContainerColor = SlateSurface,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ai_prompt_input")
                        )

                        Button(
                            onClick = {
                                val trimmed = promptInput.trim()
                                if (trimmed.isNotEmpty()) {
                                    lastSubmittedPrompt = trimmed
                                    onAskPrompt(trimmed)
                                }
                            },
                            enabled = promptInput.isNotBlank() && uiState !is AiUiState.Loading && uiState !is AiUiState.Streaming,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricCyan,
                                contentColor = DeepObsidian
                            ),
                            modifier = Modifier.testTag("ai_send_button")
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.aiSendBtn, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            when (uiState) {
                is AiUiState.Idle -> {
                    TvSectionHeader(
                        title = strings.aiIdleTitle,
                        subtitle = strings.aiIdleDesc
                    )
                }

                is AiUiState.Loading -> {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = SlateSurface,
                        border = BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(22.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            CircularProgressIndicator(
                                color = ElectricCyan,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = strings.aiLoadingText,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }
                    }
                }

                is AiUiState.Streaming, is AiUiState.Success -> {
                    val responseText = when (uiState) {
                        is AiUiState.Streaming -> uiState.text
                        is AiUiState.Success -> uiState.text
                        else -> ""
                    }
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = SlateSurface,
                        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.AutoAwesome,
                                        contentDescription = null,
                                        tint = ElectricCyan
                                    )
                                    Text(
                                        text = if (uiState is AiUiState.Streaming) strings.aiStreamingTitle else strings.aiSuccessTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = ElectricCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (uiState is AiUiState.Success) {
                                    OutlinedButton(onClick = onReset) {
                                        Text(strings.aiClearBtn)
                                    }
                                }
                            }

                            if (lastSubmittedPrompt.isNotBlank()) {
                                Text(
                                    text = "${strings.aiQuestionPrefix}: $lastSubmittedPrompt",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = responseText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                        }
                    }
                }

                is AiUiState.Error -> {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = BroadcastCoral.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, BroadcastCoral),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.ErrorOutline,
                                    contentDescription = null,
                                    tint = BroadcastCoral
                                )
                                Text(
                                    text = strings.aiErrorTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = BroadcastCoral,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = uiState.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                            if (lastSubmittedPrompt.isNotBlank()) {
                                OutlinedButton(
                                    onClick = { onAskPrompt(lastSubmittedPrompt) },
                                    border = BorderStroke(1.dp, BroadcastCoral)
                                ) {
                                    Icon(Icons.Rounded.Refresh, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(strings.aiRetryBtn, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
