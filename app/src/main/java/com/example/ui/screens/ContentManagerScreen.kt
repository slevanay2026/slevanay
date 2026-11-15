package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddLink
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.CategoryEntity
import com.example.data.local.ChannelEntity
import com.example.data.local.XtreamProfileEntity
import com.example.data.model.ContentType
import com.example.data.model.DefaultXtreamPreset
import com.example.data.model.XtreamLinkParser
import com.example.data.remote.RemoteWebPanelParser
import com.example.ui.components.LanguageFlagIcon
import com.example.ui.components.ParentalPinDialog
import com.example.ui.components.ParentalPinDialogMode
import com.example.ui.components.TvFocusableCard
import com.example.ui.components.TvSectionHeader
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.themeName
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AppThemeOption
import com.example.ui.theme.BroadcastCoral
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldLive
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SlateSurface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContentManagerScreen(
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    currentTheme: AppThemeOption = AppThemeOption.CYBER_OBSIDIAN,
    onSelectTheme: (AppThemeOption) -> Unit = {},
    profiles: List<XtreamProfileEntity>,
    activeProfile: XtreamProfileEntity?,
    liveCategories: List<CategoryEntity>,
    vodCategories: List<CategoryEntity>,
    seriesCategories: List<CategoryEntity>,
    allChannels: List<ChannelEntity>,
    isSyncing: Boolean,
    isParentalEnabled: Boolean = false,
    hasParentalPin: Boolean = false,
    isParentalUnlocked: Boolean = false,
    onConnectAndSync: (
        existingId: Long?,
        name: String,
        serverUrl: String,
        username: String,
        password: String
    ) -> Unit,
    onSwitchProfile: (XtreamProfileEntity) -> Unit,
    onSyncActiveProfile: () -> Unit,
    onDeleteProfile: (XtreamProfileEntity) -> Unit,
    onToggleCategoryVisibility: (CategoryEntity) -> Unit,
    onToggleCategoryPin: (CategoryEntity) -> Unit,
    onMoveCategory: (List<CategoryEntity>, Int, Int) -> Unit,
    onSetAllCategoriesVisibility: (ContentType, Boolean) -> Unit,
    onUpdateLiveFormat: (String) -> Unit,
    onClearHistory: () -> Unit,
    onSaveParentalPin: (String) -> Boolean = { false },
    onVerifyParentalPin: suspend (String) -> Boolean = { false },
    onLockParental: () -> Unit = {},
    onSetParentalEnabled: (Boolean) -> Unit = {},
    onToggleChannelHidden: (ChannelEntity) -> Unit = {},
    onUnhideAllChannels: () -> Unit = {},
    localWebPanelUrl: String? = null,
    remoteWebsiteUrl: String = "",
    remoteWebsiteLastSync: Long = 0L,
    isWebPanelSyncing: Boolean = false,
    onToggleLocalWebPanel: () -> Unit = {},
    onConnectRemoteWebsite: (String) -> Unit = {},
    onDisconnectRemoteWebsite: () -> Unit = {},
    onShowBanner: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    var editingProfileId by remember { mutableStateOf<Long?>(null) }
    var smartInput by remember { mutableStateOf("") }
    var panelName by remember { mutableStateOf("") }
    var serverUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var smartParseFeedback by remember { mutableStateOf<String?>(null) }

    var remoteWebsiteInput by remember { mutableStateOf(remoteWebsiteUrl) }
    LaunchedEffect(remoteWebsiteUrl) {
        if (remoteWebsiteUrl.isNotBlank() && remoteWebsiteInput.isBlank()) {
            remoteWebsiteInput = remoteWebsiteUrl
        }
    }

    var selectedCategoryTab by remember { mutableStateOf(ContentType.LIVE) }
    var categorySearchQuery by remember { mutableStateOf("") }

    var parentalDialogMode by remember { mutableStateOf<ParentalPinDialogMode?>(null) }
    var channelHideSearch by remember { mutableStateOf("") }
    var showHiddenChannelsOnly by remember { mutableStateOf(false) }

    val isParentalLocked = isParentalEnabled && !isParentalUnlocked
    val hiddenChannelsCount = remember(allChannels) { allChannels.count { it.isHidden } }
    val hiddenCategoriesCount = remember(liveCategories, vodCategories, seriesCategories) {
        liveCategories.count { !it.isVisible } +
            vodCategories.count { !it.isVisible } +
            seriesCategories.count { !it.isVisible }
    }

    val filteredParentalChannels = remember(allChannels, channelHideSearch, showHiddenChannelsOnly) {
        val q = channelHideSearch.trim().lowercase()
        val matched = allChannels.filter { ch ->
            (!showHiddenChannelsOnly || ch.isHidden) &&
                (q.isEmpty() || ch.name.lowercase().contains(q) || ch.num.toString() == q)
        }
        if (q.isEmpty() && !showHiddenChannelsOnly) matched.take(60) else matched
    }

    if (parentalDialogMode != null) {
        ParentalPinDialog(
            mode = parentalDialogMode!!,
            onDismiss = { parentalDialogMode = null },
            onSavePin = onSaveParentalPin,
            onVerifyPin = onVerifyParentalPin
        )
    }

    val activeCategories = when (selectedCategoryTab) {
        ContentType.LIVE -> liveCategories
        ContentType.VOD -> vodCategories
        ContentType.SERIES -> seriesCategories
    }
    val filteredCategories = remember(activeCategories, categorySearchQuery) {
        val q = categorySearchQuery.trim().lowercase()
        if (q.isEmpty()) activeCategories
        else activeCategories.filter { it.name.lowercase().contains(q) }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Section 0: Language Selector (5 Languages + RTL)
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
                    TvSectionHeader(
                        title = strings.languageSectionTitle,
                        subtitle = strings.languageSectionSub,
                        trailing = {
                            Icon(
                                imageVector = Icons.Rounded.Language,
                                contentDescription = null,
                                tint = ElectricCyan
                            )
                        }
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            val selected = lang == currentLanguage
                            TvFocusableCard(
                                onClick = { onSelectLanguage(lang) },
                                shape = RoundedCornerShape(12.dp),
                                containerColor = if (selected) ElectricCyan else SlateSurface,
                                modifier = Modifier.testTag("lang_chip_${lang.code}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    LanguageFlagIcon(
                                        language = lang,
                                        width = 34.dp,
                                        height = 22.dp
                                    )
                                    Column {
                                        Text(
                                            text = lang.nativeName,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) DeepObsidian else Color.White
                                        )
                                        if (lang.nativeName != lang.displayName) {
                                            Text(
                                                text = lang.displayName,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (selected) {
                                                    DeepObsidian.copy(alpha = 0.8f)
                                                } else {
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                }
                                            )
                                        }
                                    }
                                    if (selected) {
                                        Icon(
                                            imageVector = Icons.Rounded.CheckCircle,
                                            contentDescription = null,
                                            tint = DeepObsidian,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 0.5: App Theme & Color Atmosphere Selector
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
                    TvSectionHeader(
                        title = strings.themeSectionTitle,
                        subtitle = strings.themeSectionSub,
                        trailing = {
                            Icon(
                                imageVector = Icons.Rounded.Palette,
                                contentDescription = null,
                                tint = ElectricCyan
                            )
                        }
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AppThemeOption.entries.forEach { theme ->
                            val selected = theme == currentTheme
                            val p = theme.palette
                            TvFocusableCard(
                                onClick = { onSelectTheme(theme) },
                                shape = RoundedCornerShape(14.dp),
                                containerColor = if (selected) p.electricCyan else SlateSurface,
                                modifier = Modifier.testTag("theme_card_${theme.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        listOf(
                                            p.deepObsidian,
                                            p.slateSurface,
                                            p.electricCyan,
                                            p.indigoGlow
                                        ).forEach { swatchColor ->
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clip(CircleShape)
                                                    .background(swatchColor)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (selected) p.deepObsidian.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.3f),
                                                        shape = CircleShape
                                                    )
                                            )
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = strings.themeName(theme),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) p.deepObsidian else Color.White
                                        )
                                        if (selected) {
                                            Text(
                                                text = strings.themeActiveBadge,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = p.deepObsidian.copy(alpha = 0.85f),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                    if (selected) {
                                        Icon(
                                            imageVector = Icons.Rounded.CheckCircle,
                                            contentDescription = null,
                                            tint = p.deepObsidian,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 0.75: Website & Browser Control Panel
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Public,
                            contentDescription = null,
                            tint = ElectricCyan
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.webPanelSectionTitle,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = strings.webPanelSectionSub,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Card A: Built-in Live Browser Web Control Panel
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SlateSurface,
                        border = BorderStroke(
                            1.dp,
                            if (localWebPanelUrl != null) EmeraldLive.copy(alpha = 0.55f) else GlassBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.webPanelLocalTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (localWebPanelUrl != null) {
                                            strings.webPanelLocalRunning
                                        } else {
                                            strings.webPanelLocalStopped
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (localWebPanelUrl != null) EmeraldLive else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (localWebPanelUrl != null) EmeraldLive.copy(alpha = 0.2f) else BroadcastCoral.copy(alpha = 0.16f)
                                ) {
                                    Text(
                                        text = if (localWebPanelUrl != null) "LIVE" else "OFF",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (localWebPanelUrl != null) EmeraldLive else BroadcastCoral,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            if (localWebPanelUrl != null) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = DeepObsidian.copy(alpha = 0.75f),
                                    border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = localWebPanelUrl,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = ElectricCyan,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                    )
                                }
                            }

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = onToggleLocalWebPanel,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (localWebPanelUrl != null) BroadcastCoral else ElectricCyan,
                                        contentColor = if (localWebPanelUrl != null) Color.White else DeepObsidian
                                    ),
                                    modifier = Modifier
                                        .defaultMinSize(minHeight = 48.dp)
                                        .testTag("web_panel_toggle_button")
                                ) {
                                    Icon(Icons.Rounded.PowerSettingsNew, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (localWebPanelUrl != null) strings.webPanelStopBtn else strings.webPanelStartBtn,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (localWebPanelUrl != null) {
                                    OutlinedButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(localWebPanelUrl))
                                            onShowBanner(strings.webPanelUrlCopiedMsg)
                                        },
                                        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.65f)),
                                        modifier = Modifier
                                            .defaultMinSize(minHeight = 48.dp)
                                            .testTag("web_panel_copy_url_button")
                                    ) {
                                        Icon(Icons.Rounded.ContentCopy, contentDescription = null, tint = ElectricCyan)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(strings.webPanelCopyUrlBtn, color = ElectricCyan)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            runCatching { uriHandler.openUri(localWebPanelUrl) }
                                        },
                                        border = BorderStroke(1.dp, EmeraldLive.copy(alpha = 0.65f)),
                                        modifier = Modifier
                                            .defaultMinSize(minHeight = 48.dp)
                                            .testTag("web_panel_open_browser_button")
                                    ) {
                                        Icon(Icons.Rounded.OpenInBrowser, contentDescription = null, tint = EmeraldLive)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(strings.webPanelOpenBrowserBtn, color = EmeraldLive)
                                    }
                                }
                            }
                        }
                    }

                    // Card B: External Website Panel Sync (JSON / M3U / Xtream URL)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SlateSurface,
                        border = BorderStroke(1.dp, GlassBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = strings.webPanelRemoteTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = strings.webPanelRemoteSub,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = remoteWebsiteInput,
                                onValueChange = { remoteWebsiteInput = it },
                                label = { Text(strings.webPanelRemoteUrlLabel) },
                                placeholder = { Text("https://siteniz.com/jhehakar-panel.json") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = DeepObsidian.copy(alpha = 0.7f),
                                    unfocusedContainerColor = DeepObsidian.copy(alpha = 0.55f),
                                    focusedBorderColor = ElectricCyan,
                                    unfocusedBorderColor = GlassBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("remote_website_url_input")
                            )

                            if (remoteWebsiteLastSync > 0L) {
                                val formatted = remember(remoteWebsiteLastSync) {
                                    SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
                                        .format(Date(remoteWebsiteLastSync))
                                }
                                Text(
                                    text = "${strings.webPanelLastSyncPrefix}: $formatted",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = EmeraldLive
                                )
                            }

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { onConnectRemoteWebsite(remoteWebsiteInput) },
                                    enabled = !isWebPanelSyncing && remoteWebsiteInput.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ElectricCyan,
                                        contentColor = DeepObsidian
                                    ),
                                    modifier = Modifier
                                        .defaultMinSize(minHeight = 48.dp)
                                        .testTag("remote_website_connect_button")
                                ) {
                                    if (isWebPanelSyncing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = DeepObsidian,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(Icons.Rounded.CloudSync, contentDescription = null)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(strings.webPanelConnectSyncBtn, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        clipboardManager.setText(
                                            AnnotatedString(RemoteWebPanelParser.SAMPLE_PANEL_JSON_TEMPLATE)
                                        )
                                        onShowBanner(strings.webPanelTemplateCopiedMsg)
                                    },
                                    border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.65f)),
                                    modifier = Modifier
                                        .defaultMinSize(minHeight = 48.dp)
                                        .testTag("remote_website_copy_template_button")
                                ) {
                                    Icon(Icons.Rounded.ContentCopy, contentDescription = null, tint = AmberGold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(strings.webPanelCopyTemplateBtn, color = AmberGold)
                                }

                                if (remoteWebsiteUrl.isNotBlank()) {
                                    OutlinedButton(
                                        onClick = {
                                            remoteWebsiteInput = ""
                                            onDisconnectRemoteWebsite()
                                        },
                                        border = BorderStroke(1.dp, BroadcastCoral.copy(alpha = 0.65f)),
                                        modifier = Modifier
                                            .defaultMinSize(minHeight = 48.dp)
                                            .testTag("remote_website_disconnect_button")
                                    ) {
                                        Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = BroadcastCoral)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(strings.webPanelDisconnectBtn, color = BroadcastCoral)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 1: Xtream Panel Connection & Smart Code Parser
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MidnightNavy,
                border = BorderStroke(1.dp, GlassBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TvSectionHeader(
                        title = if (editingProfileId == null) {
                            strings.xtreamConnectTitle
                        } else {
                            strings.xtreamEditTitle
                        },
                        subtitle = strings.xtreamConnectSub
                    )

                    OutlinedTextField(
                        value = smartInput,
                        onValueChange = {
                            smartInput = it
                            smartParseFeedback = null
                        },
                        label = { Text(strings.smartPasteLabel) },
                        placeholder = {
                            Text("http://server:8080/get.php?username=...&password=...")
                        },
                        trailingIcon = {
                            OutlinedButton(
                                onClick = {
                                    val parsed = XtreamLinkParser.parseSmartInput(smartInput)
                                    if (parsed != null) {
                                        serverUrl = parsed.serverUrl
                                        if (parsed.username.isNotBlank()) username = parsed.username
                                        if (parsed.password.isNotBlank()) password = parsed.password
                                        if (panelName.isBlank() && parsed.suggestedName.isNotBlank()) {
                                            panelName = parsed.suggestedName
                                        }
                                        smartParseFeedback = strings.parseSuccessMsg
                                    } else {
                                        smartParseFeedback = strings.parseFailMsg
                                    }
                                },
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .testTag("smart_parse_button")
                            ) {
                                Icon(Icons.Rounded.AutoFixHigh, contentDescription = null, tint = ElectricCyan)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.parseBtn, color = ElectricCyan)
                            }
                        },
                        maxLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SlateSurface,
                            unfocusedContainerColor = SlateSurface,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = GlassBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("smart_xtream_input")
                    )

                    if (smartParseFeedback != null) {
                        Text(
                            text = smartParseFeedback!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ElectricCyan
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = panelName,
                            onValueChange = { panelName = it },
                            label = { Text(strings.panelNameLabel) },
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
                                .testTag("panel_name_input")
                        )
                        OutlinedTextField(
                            value = serverUrl,
                            onValueChange = { serverUrl = it },
                            label = { Text(strings.serverUrlLabel) },
                            placeholder = { Text("http://domain.com:8080") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SlateSurface,
                                unfocusedContainerColor = SlateSurface,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier
                                .weight(1.4f)
                                .testTag("server_url_input")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text(strings.usernameLabel) },
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
                                .testTag("username_input")
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(strings.passwordLabel) },
                            singleLine = true,
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                        contentDescription = strings.passwordLabel
                                    )
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SlateSurface,
                                unfocusedContainerColor = SlateSurface,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("password_input")
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                onConnectAndSync(
                                    editingProfileId,
                                    panelName,
                                    serverUrl,
                                    username,
                                    password
                                )
                                editingProfileId = null
                            },
                            enabled = !isSyncing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricCyan,
                                contentColor = DeepObsidian
                            ),
                            modifier = Modifier.testTag("connect_sync_button")
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = DeepObsidian
                                )
                            } else {
                                Icon(Icons.Rounded.AddLink, contentDescription = null)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSyncing) strings.connectingBtn else strings.connectAndSyncBtn,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (editingProfileId != null) {
                            OutlinedButton(
                                onClick = {
                                    editingProfileId = null
                                    panelName = ""
                                    serverUrl = ""
                                    username = ""
                                    password = ""
                                }
                            ) {
                                Text(strings.cancelBtn)
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    panelName = DefaultXtreamPreset.NAME
                                    serverUrl = DefaultXtreamPreset.SERVER_URL
                                    username = DefaultXtreamPreset.USERNAME
                                    password = DefaultXtreamPreset.PASSWORD
                                    smartParseFeedback = strings.sanazFilledMsg
                                },
                                modifier = Modifier.testTag("fill_sanaz_preset_button")
                            ) {
                                Text(strings.fillSanazBtn)
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Saved Panels
        if (profiles.isNotEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MidnightNavy,
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TvSectionHeader(
                            title = "${strings.savedPanelsTitle} (${profiles.size})",
                            subtitle = strings.savedPanelsSub,
                            trailing = {
                                if (activeProfile != null) {
                                    OutlinedButton(
                                        onClick = onSyncActiveProfile,
                                        enabled = !isSyncing
                                    ) {
                                        Icon(Icons.Rounded.Refresh, contentDescription = null, tint = ElectricCyan)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(strings.syncNowBtn, color = ElectricCyan)
                                    }
                                }
                            }
                        )

                        profiles.forEach { profile ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (profile.isActive) SlateSurface else DeepObsidian.copy(alpha = 0.6f),
                                border = BorderStroke(
                                    width = if (profile.isActive) 1.5.dp else 1.dp,
                                    color = if (profile.isActive) ElectricCyan else GlassBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            if (profile.isActive) {
                                                Icon(
                                                    Icons.Rounded.CheckCircle,
                                                    contentDescription = null,
                                                    tint = EmeraldLive,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Text(
                                                text = profile.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${profile.serverUrl} • ${strings.usernameLabel}: ${profile.username}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${strings.statusPrefix}: ${profile.status} • ${strings.expiryPrefix}: ${formatDate(profile.expDate, strings)} • ${strings.connectionsPrefix}: ${profile.activeCons}/${profile.maxConnections}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = ElectricCyan
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (!profile.isActive) {
                                            OutlinedButton(onClick = { onSwitchProfile(profile) }) {
                                                Text(strings.selectBtn)
                                            }
                                        }
                                        IconButton(
                                            onClick = {
                                                editingProfileId = profile.id
                                                panelName = profile.name
                                                serverUrl = profile.serverUrl
                                                username = profile.username
                                                password = profile.password
                                            }
                                        ) {
                                            Icon(Icons.Rounded.Edit, contentDescription = strings.xtreamEditTitle)
                                        }
                                        IconButton(onClick = { onDeleteProfile(profile) }) {
                                            Icon(
                                                Icons.Rounded.DeleteOutline,
                                                contentDescription = strings.cancelBtn,
                                                tint = BroadcastCoral
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Player Preferences
        if (activeProfile != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MidnightNavy,
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TvSectionHeader(
                            title = strings.playerSettingsTitle,
                            subtitle = strings.playerSettingsSub
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = activeProfile.liveFormat == "m3u8",
                                onClick = { onUpdateLiveFormat("m3u8") },
                                label = { Text("HLS (.m3u8)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricCyan,
                                    selectedLabelColor = DeepObsidian
                                )
                            )
                            FilterChip(
                                selected = activeProfile.liveFormat == "ts",
                                onClick = { onUpdateLiveFormat("ts") },
                                label = { Text("MPEG-TS (.ts)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricCyan,
                                    selectedLabelColor = DeepObsidian
                                )
                            )
                            OutlinedButton(onClick = onClearHistory) {
                                Icon(Icons.Rounded.DeleteSweep, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(strings.clearHistoryBtn)
                            }
                        }
                    }
                }
            }

            // Section 3.5: Parental Control (PIN-Protected Channel & Category Hiding)
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MidnightNavy,
                    border = BorderStroke(
                        1.dp,
                        when {
                            isParentalLocked -> BroadcastCoral.copy(alpha = 0.65f)
                            isParentalEnabled -> EmeraldLive.copy(alpha = 0.6f)
                            else -> GlassBorder
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TvSectionHeader(
                            title = strings.parentalSectionTitle,
                            subtitle = strings.parentalSectionSub,
                            trailing = {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = when {
                                        isParentalLocked -> BroadcastCoral.copy(alpha = 0.16f)
                                        isParentalEnabled -> EmeraldLive.copy(alpha = 0.16f)
                                        else -> SlateSurface
                                    },
                                    border = BorderStroke(
                                        1.dp,
                                        when {
                                            isParentalLocked -> BroadcastCoral.copy(alpha = 0.6f)
                                            isParentalEnabled -> EmeraldLive.copy(alpha = 0.6f)
                                            else -> GlassBorder
                                        }
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = when {
                                                isParentalLocked -> Icons.Rounded.Lock
                                                isParentalEnabled -> Icons.Rounded.LockOpen
                                                else -> Icons.Rounded.Shield
                                            },
                                            contentDescription = null,
                                            tint = when {
                                                isParentalLocked -> BroadcastCoral
                                                isParentalEnabled -> EmeraldLive
                                                else -> ElectricCyan
                                            },
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = when {
                                                isParentalLocked -> strings.parentalStatusActiveLocked
                                                isParentalEnabled -> strings.parentalStatusActiveUnlocked
                                                else -> strings.parentalStatusDisabled
                                            },
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SlateSurface
                            ) {
                                Text(
                                    text = "${strings.navLive}: $hiddenChannelsCount ${strings.parentalHiddenCountSuffix}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (hiddenChannelsCount > 0) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SlateSurface
                            ) {
                                Text(
                                    text = "${strings.categoriesTitle}: $hiddenCategoriesCount ${strings.parentalHiddenCountSuffix}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (hiddenCategoriesCount > 0) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            when {
                                !hasParentalPin -> {
                                    Button(
                                        onClick = { parentalDialogMode = ParentalPinDialogMode.CREATE },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ElectricCyan,
                                            contentColor = DeepObsidian
                                        ),
                                        modifier = Modifier.testTag("parental_setup_btn")
                                    ) {
                                        Icon(Icons.Rounded.Shield, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(strings.parentalSetupPinBtn, fontWeight = FontWeight.Bold)
                                    }
                                }
                                !isParentalEnabled -> {
                                    Button(
                                        onClick = { onSetParentalEnabled(true) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ElectricCyan,
                                            contentColor = DeepObsidian
                                        ),
                                        modifier = Modifier.testTag("parental_enable_btn")
                                    ) {
                                        Icon(Icons.Rounded.Lock, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(strings.parentalEnableBtn, fontWeight = FontWeight.Bold)
                                    }
                                }
                                isParentalLocked -> {
                                    Button(
                                        onClick = { parentalDialogMode = ParentalPinDialogMode.UNLOCK },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ElectricCyan,
                                            contentColor = DeepObsidian
                                        ),
                                        modifier = Modifier.testTag("parental_unlock_btn")
                                    ) {
                                        Icon(Icons.Rounded.LockOpen, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(strings.parentalUnlockBtn, fontWeight = FontWeight.Bold)
                                    }
                                }
                                else -> {
                                    Button(
                                        onClick = onLockParental,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ElectricCyan,
                                            contentColor = DeepObsidian
                                        ),
                                        modifier = Modifier.testTag("parental_lock_btn")
                                    ) {
                                        Icon(Icons.Rounded.Lock, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(strings.parentalLockBtn, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { parentalDialogMode = ParentalPinDialogMode.CHANGE },
                                        modifier = Modifier.testTag("parental_change_pin_btn")
                                    ) {
                                        Text(strings.parentalChangePinBtn)
                                    }
                                    OutlinedButton(
                                        onClick = { onSetParentalEnabled(false) },
                                        modifier = Modifier.testTag("parental_disable_btn")
                                    ) {
                                        Text(strings.parentalDisableBtn, color = BroadcastCoral)
                                    }
                                }
                            }
                        }

                        if (isParentalLocked) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BroadcastCoral.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, BroadcastCoral.copy(alpha = 0.45f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.Lock,
                                        contentDescription = null,
                                        tint = BroadcastCoral
                                    )
                                    Text(
                                        text = strings.parentalLockedNotice,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White
                                    )
                                }
                            }
                        } else if (allChannels.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                TvSectionHeader(
                                    title = strings.parentalChannelHidingTitle,
                                    subtitle = strings.parentalChannelHidingSub
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = channelHideSearch,
                                        onValueChange = { channelHideSearch = it },
                                        placeholder = { Text(strings.searchChannelHint) },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = SlateSurface,
                                            unfocusedContainerColor = SlateSurface,
                                            focusedBorderColor = ElectricCyan,
                                            unfocusedBorderColor = GlassBorder
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("parental_channel_search")
                                    )
                                    FilterChip(
                                        selected = !showHiddenChannelsOnly,
                                        onClick = { showHiddenChannelsOnly = false },
                                        label = { Text("${strings.parentalFilterAllChannels} (${allChannels.size})") }
                                    )
                                    FilterChip(
                                        selected = showHiddenChannelsOnly,
                                        onClick = { showHiddenChannelsOnly = true },
                                        label = { Text("${strings.parentalFilterHiddenChannels} ($hiddenChannelsCount)") }
                                    )
                                    if (hiddenChannelsCount > 0) {
                                        OutlinedButton(onClick = onUnhideAllChannels) {
                                            Text(strings.parentalUnhideAllBtn)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!isParentalLocked && allChannels.isNotEmpty()) {
                itemsIndexed(
                    filteredParentalChannels,
                    key = { _, ch -> "parental_ch_${ch.id}" }
                ) { _, channel ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (!channel.isHidden) SlateSurface else DeepObsidian,
                        border = BorderStroke(
                            1.dp,
                            if (channel.isHidden) BroadcastCoral.copy(alpha = 0.5f) else GlassBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "#${channel.num} • ${channel.name}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (!channel.isHidden) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (!channel.isHidden) strings.visibleLabel else strings.hiddenLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (!channel.isHidden) EmeraldLive else BroadcastCoral
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (!channel.isHidden) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                                    contentDescription = null,
                                    tint = if (!channel.isHidden) ElectricCyan else BroadcastCoral,
                                    modifier = Modifier.size(18.dp)
                                )
                                Switch(
                                    checked = !channel.isHidden,
                                    onCheckedChange = { onToggleChannelHidden(channel) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = DeepObsidian,
                                        checkedTrackColor = ElectricCyan
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Section 4: Category Organizer Header
            item {
                Surface(
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    color = MidnightNavy,
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TvSectionHeader(
                            title = strings.categoryOrganizerTitle,
                            subtitle = if (isParentalLocked) strings.parentalLockedNotice else strings.categoryOrganizerSub,
                            trailing = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isParentalLocked) {
                                        Icon(
                                            imageVector = Icons.Rounded.Lock,
                                            contentDescription = strings.parentalStatusActiveLocked,
                                            tint = BroadcastCoral
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            if (isParentalLocked) {
                                                parentalDialogMode = ParentalPinDialogMode.UNLOCK
                                            } else {
                                                onSetAllCategoriesVisibility(selectedCategoryTab, true)
                                            }
                                        }
                                    ) {
                                        Text(strings.showAllBtn)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            if (isParentalLocked) {
                                                parentalDialogMode = ParentalPinDialogMode.UNLOCK
                                            } else {
                                                onSetAllCategoriesVisibility(selectedCategoryTab, false)
                                            }
                                        }
                                    ) {
                                        Text(strings.hideAllBtn)
                                    }
                                }
                            }
                        )

                        TabRow(
                            selectedTabIndex = selectedCategoryTab.ordinal,
                            containerColor = SlateSurface,
                            contentColor = ElectricCyan
                        ) {
                            Tab(
                                selected = selectedCategoryTab == ContentType.LIVE,
                                onClick = { selectedCategoryTab = ContentType.LIVE },
                                text = { Text("${strings.navLive} (${liveCategories.size})") }
                            )
                            Tab(
                                selected = selectedCategoryTab == ContentType.VOD,
                                onClick = { selectedCategoryTab = ContentType.VOD },
                                text = { Text("${strings.navMovies} (${vodCategories.size})") }
                            )
                            Tab(
                                selected = selectedCategoryTab == ContentType.SERIES,
                                onClick = { selectedCategoryTab = ContentType.SERIES },
                                text = { Text("${strings.navSeries} (${seriesCategories.size})") }
                            )
                        }

                        OutlinedTextField(
                            value = categorySearchQuery,
                            onValueChange = { categorySearchQuery = it },
                            placeholder = { Text(strings.filterCategoryHint) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SlateSurface,
                                unfocusedContainerColor = SlateSurface,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            itemsIndexed(filteredCategories, key = { _, item -> item.id }) { idx, category ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (category.isVisible) SlateSurface else DeepObsidian,
                    border = BorderStroke(
                        1.dp,
                        if (category.isPinned) AmberGold.copy(alpha = 0.6f) else GlassBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = if (category.isVisible) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (category.isPinned) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = buildString {
                                    append(if (category.isVisible) strings.visibleLabel else strings.hiddenLabel)
                                    if (category.isPinned) append(" • ${strings.pinnedLabel}")
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = if (category.isPinned) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (categorySearchQuery.isBlank()) {
                                IconButton(
                                    onClick = { onMoveCategory(filteredCategories, idx, -1) },
                                    enabled = idx > 0
                                ) {
                                    Icon(Icons.Rounded.ArrowUpward, contentDescription = null)
                                }
                                IconButton(
                                    onClick = { onMoveCategory(filteredCategories, idx, +1) },
                                    enabled = idx < filteredCategories.lastIndex
                                ) {
                                    Icon(Icons.Rounded.ArrowDownward, contentDescription = null)
                                }
                            }
                            IconButton(onClick = { onToggleCategoryPin(category) }) {
                                Icon(
                                    Icons.Rounded.PushPin,
                                    contentDescription = strings.pinnedLabel,
                                    tint = if (category.isPinned) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = category.isVisible,
                                onCheckedChange = {
                                    if (isParentalLocked) {
                                        parentalDialogMode = ParentalPinDialogMode.UNLOCK
                                    } else {
                                        onToggleCategoryVisibility(category)
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = DeepObsidian,
                                    checkedTrackColor = ElectricCyan
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(epochSeconds: Long?, strings: AppStrings): String {
    if (epochSeconds == null || epochSeconds <= 0L) return strings.unlimited
    return runCatching {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        sdf.format(Date(epochSeconds * 1000L))
    }.getOrDefault(strings.unknown)
}
