package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddLink
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.Refresh
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.CategoryEntity
import com.example.data.local.XtreamProfileEntity
import com.example.data.model.ContentType
import com.example.data.model.XtreamLinkParser
import com.example.ui.components.TvSectionHeader
import com.example.ui.theme.AmberGold
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

@Composable
fun ContentManagerScreen(
    profiles: List<XtreamProfileEntity>,
    activeProfile: XtreamProfileEntity?,
    liveCategories: List<CategoryEntity>,
    vodCategories: List<CategoryEntity>,
    seriesCategories: List<CategoryEntity>,
    isSyncing: Boolean,
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
    modifier: Modifier = Modifier
) {
    var editingProfileId by remember { mutableStateOf<Long?>(null) }
    var smartInput by remember { mutableStateOf("") }
    var panelName by remember { mutableStateOf("") }
    var serverUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var smartParseFeedback by remember { mutableStateOf<String?>(null) }

    var selectedCategoryTab by remember { mutableStateOf(ContentType.LIVE) }
    var categorySearchQuery by remember { mutableStateOf("") }

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
                            "Xtream IPTV Panel Bağlantısı"
                        } else {
                            "Panel Bilgilerini Düzenle"
                        },
                        subtitle = "Xtream kodunu doğrudan yapıştırın veya sunucu bilgilerini tek tek girin"
                    )

                    // Smart Link / Code Paste Box
                    OutlinedTextField(
                        value = smartInput,
                        onValueChange = {
                            smartInput = it
                            smartParseFeedback = null
                        },
                        label = { Text("Hızlı Yapıştır (M3U / Xtream Linki veya Mesaj Metni)") },
                        placeholder = {
                            Text("http://sunucu:8080/get.php?username=...&password=...")
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
                                        smartParseFeedback = "Xtream bilgileri otomatik dolduruldu!"
                                    } else {
                                        smartParseFeedback = "Bağlantı ayrıştırılamadı, alanları manuel doldurabilirsiniz."
                                    }
                                },
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .testTag("smart_parse_button")
                            ) {
                                Icon(Icons.Rounded.AutoFixHigh, contentDescription = null, tint = ElectricCyan)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ayrıştır", color = ElectricCyan)
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
                            label = { Text("Panel Adı (Örn: Ev TV)") },
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
                            label = { Text("Sunucu Adresi (URL:Port)") },
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
                            label = { Text("Kullanıcı Adı") },
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
                            label = { Text("Şifre") },
                            singleLine = true,
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                        contentDescription = "Şifreyi göster/gizle"
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

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
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
                                text = if (isSyncing) "Bağlanıyor..." else "Bağlan ve Senkronize Et",
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
                                Text("İptal")
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
                            title = "Kayıtlı Xtream Panelleri (${profiles.size})",
                            subtitle = "Paneller arasında tek tıkla geçiş yapın veya kataloğu güncelleyin",
                            trailing = {
                                if (activeProfile != null) {
                                    OutlinedButton(
                                        onClick = onSyncActiveProfile,
                                        enabled = !isSyncing
                                    ) {
                                        Icon(Icons.Rounded.Refresh, contentDescription = null, tint = ElectricCyan)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Senkronize Et", color = ElectricCyan)
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
                                                    contentDescription = "Aktif",
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
                                            text = "${profile.serverUrl} • Kullanıcı: ${profile.username}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Durum: ${profile.status} • Bitiş: ${formatDate(profile.expDate)} • Bağlantı: ${profile.activeCons}/${profile.maxConnections}",
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
                                                Text("Seç")
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
                                            Icon(Icons.Rounded.Edit, contentDescription = "Düzenle")
                                        }
                                        IconButton(onClick = { onDeleteProfile(profile) }) {
                                            Icon(
                                                Icons.Rounded.DeleteOutline,
                                                contentDescription = "Sil",
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Oynatıcı ve Akış Ayarları",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Canlı yayın protokolünü seçin (HLS .m3u8 çoğu TV Box için önerilir)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
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
                                Text("Geçmişi Sil")
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
                            title = "Kategori ve İçerik Düzenleyici",
                            subtitle = "İstemediğiniz kategorileri gizleyin, sık kullandıklarınızı başa sabitleyin veya sıralayın",
                            trailing = {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { onSetAllCategoriesVisibility(selectedCategoryTab, true) }
                                    ) {
                                        Text("Tümünü Göster")
                                    }
                                    OutlinedButton(
                                        onClick = { onSetAllCategoriesVisibility(selectedCategoryTab, false) }
                                    ) {
                                        Text("Tümünü Gizle")
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
                                text = { Text("Canlı TV (${liveCategories.size})") }
                            )
                            Tab(
                                selected = selectedCategoryTab == ContentType.VOD,
                                onClick = { selectedCategoryTab = ContentType.VOD },
                                text = { Text("Filmler (${vodCategories.size})") }
                            )
                            Tab(
                                selected = selectedCategoryTab == ContentType.SERIES,
                                onClick = { selectedCategoryTab = ContentType.SERIES },
                                text = { Text("Diziler (${seriesCategories.size})") }
                            )
                        }

                        OutlinedTextField(
                            value = categorySearchQuery,
                            onValueChange = { categorySearchQuery = it },
                            placeholder = { Text("Kategori adı filtrele...") },
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
                                    append(if (category.isVisible) "Görünür" else "Gizli")
                                    if (category.isPinned) append(" • Sabitlendi")
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
                                    Icon(Icons.Rounded.ArrowUpward, contentDescription = "Yukarı taşı")
                                }
                                IconButton(
                                    onClick = { onMoveCategory(filteredCategories, idx, +1) },
                                    enabled = idx < filteredCategories.lastIndex
                                ) {
                                    Icon(Icons.Rounded.ArrowDownward, contentDescription = "Aşağı taşı")
                                }
                            }
                            IconButton(onClick = { onToggleCategoryPin(category) }) {
                                Icon(
                                    Icons.Rounded.PushPin,
                                    contentDescription = "Başa sabitle",
                                    tint = if (category.isPinned) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = category.isVisible,
                                onCheckedChange = { onToggleCategoryVisibility(category) },
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

private fun formatDate(epochSeconds: Long?): String {
    if (epochSeconds == null || epochSeconds <= 0L) return "Sınırsız"
    return runCatching {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale("tr", "TR"))
        sdf.format(Date(epochSeconds * 1000L))
    }.getOrDefault("-")
}
