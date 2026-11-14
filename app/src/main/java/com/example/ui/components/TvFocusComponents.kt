package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BroadcastCoral
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElevatedSlate
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.SlateSurface

@Composable
fun TvFocusableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(14.dp),
    containerColor: Color = SlateSurface,
    focusedContainerColor: Color = ElevatedSlate,
    isSelected: Boolean = false,
    focusScale: Float = 1.035f,
    content: @Composable BoxScope.(isFocused: Boolean) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) focusScale else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tv_card_scale"
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            isFocused -> ElectricCyan
            isSelected -> ElectricCyan.copy(alpha = 0.65f)
            else -> GlassBorder.copy(alpha = 0.55f)
        },
        label = "tv_card_border"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isFocused || isSelected) focusedContainerColor else containerColor,
        label = "tv_card_bg"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .onFocusChanged { isFocused = it.isFocused }
            .clip(shape)
            .border(
                width = if (isFocused) 2.5.dp else 1.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = ElectricCyan),
                onClick = onClick
            )
            .focusable(interactionSource = interactionSource),
        shape = shape,
        color = bgColor,
        tonalElevation = if (isFocused) 8.dp else 2.dp
    ) {
        Box(content = { content(isFocused) })
    }
}

@Composable
fun TvSectionHeader(
    title: String,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (trailing != null) {
            Spacer(modifier = Modifier.width(12.dp))
            trailing()
        }
    }
}

@Composable
fun TvCategoryRowItem(
    name: String,
    count: Int?,
    isSelected: Boolean,
    isPinned: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TvFocusableCard(
        onClick = onClick,
        isSelected = isSelected,
        focusScale = 1.02f,
        shape = RoundedCornerShape(12.dp),
        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else SlateSurface.copy(alpha = 0.7f),
        focusedContainerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else ElevatedSlate,
        modifier = modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
    ) { _ ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isSelected) ElectricCyan else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isSelected || isPinned) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (count != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DeepObsidian.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TvChannelCard(
    channelNumber: Int,
    name: String,
    logoUrl: String,
    isFavorite: Boolean,
    onPlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    TvFocusableCard(
        onClick = onPlay,
        modifier = modifier
            .fillMaxWidth()
            .testTag("channel_card_$channelNumber")
    ) { isFocused ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DeepObsidian),
                contentAlignment = Alignment.Center
            ) {
                if (logoUrl.isNotBlank()) {
                    AsyncImage(
                        model = logoUrl,
                        contentDescription = "$name logosu",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.LiveTv,
                        contentDescription = null,
                        tint = ElectricCyan.copy(alpha = 0.7f),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ElevatedSlate
                    ) {
                        Text(
                            text = "#$channelNumber",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BroadcastCoral.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = "CANLI",
                            style = MaterialTheme.typography.labelSmall,
                            color = BroadcastCoral,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("fav_channel_$channelNumber")
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                    contentDescription = if (isFavorite) "Favorilerden çıkar" else "Favorilere ekle",
                    tint = if (isFavorite) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isFocused) {
                Surface(
                    shape = CircleShape,
                    color = ElectricCyan,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = "Oynat",
                            tint = DeepObsidian,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TvPosterCard(
    title: String,
    posterUrl: String,
    rating: String,
    badge: String?,
    isFavorite: Boolean,
    fallbackIcon: ImageVector = Icons.Rounded.Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TvFocusableCard(
        onClick = onClick,
        modifier = modifier
    ) { isFocused ->
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.68f)
                    .background(DeepObsidian)
            ) {
                if (posterUrl.isNotBlank()) {
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = fallbackIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.Transparent,
                                    DeepObsidian.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                // Rating / Favorite top row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val parsedRating = rating.toDoubleOrNull()
                    if (parsedRating != null && parsedRating > 0.0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DeepObsidian.copy(alpha = 0.82f),
                            border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = String.format("%.1f", parsedRating),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (isFavorite) {
                        Surface(
                            shape = CircleShape,
                            color = DeepObsidian.copy(alpha = 0.8f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = "Favori",
                                tint = AmberGold,
                                modifier = Modifier
                                    .padding(5.dp)
                                    .size(14.dp)
                            )
                        }
                    }
                }

                if (!badge.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isFocused) ElectricCyan else ElevatedSlate.copy(alpha = 0.9f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = badge.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isFocused) DeepObsidian else ElectricCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isFocused) ElectricCyan else MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2
                )
            }
        }
    }
}

@Composable
fun TvStatChip(
    label: String,
    value: String,
    accent: Color = ElectricCyan,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = SlateSurface,
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = accent,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TvEmptyStateCard(
    title: String,
    description: String,
    icon: ImageVector = Icons.Rounded.Tv,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SlateSurface.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (actionLabel != null && onAction != null) {
                Spacer(modifier = Modifier.height(18.dp))
                TvFocusableCard(
                    onClick = onAction,
                    containerColor = ElectricCyan,
                    focusedContainerColor = ElectricCyan,
                    modifier = Modifier.testTag("empty_state_action_btn")
                ) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = DeepObsidian,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}
