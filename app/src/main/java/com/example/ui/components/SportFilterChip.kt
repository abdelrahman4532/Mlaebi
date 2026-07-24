package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.SportsFootball
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SportType

@Composable
fun SportFilterRow(
    selectedSport: SportType,
    onSportSelected: (SportType) -> Unit,
    modifier: Modifier = Modifier
) {
    val sports = SportType.values()

    LazyRow(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(sports) { sport ->
            SportFilterChip(
                sport = sport,
                isSelected = sport == selectedSport,
                onClick = { onSportSelected(sport) }
            )
        }
    }
}

@Composable
fun SportFilterChip(
    sport: SportType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        label = "chipBg"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        label = "chipText"
    )

    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (sport) {
                SportType.ALL -> {
                    Icon(
                        imageVector = Icons.Default.Sports,
                        contentDescription = "All Sports",
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                SportType.FOOTBALL -> {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = "Football",
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                SportType.TENNIS -> {
                    Icon(
                        imageVector = Icons.Default.SportsTennis,
                        contentDescription = "Tennis",
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                SportType.PADEL -> {
                    Icon(
                        imageVector = Icons.Default.SportsFootball,
                        contentDescription = "Padel",
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = sport.titleEn,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

