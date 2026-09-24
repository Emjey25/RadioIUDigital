package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StationCategory
import com.example.ui.components.MainPlayerCard
import com.example.ui.components.StationCard
import com.example.ui.components.StudentProfileCard
import com.example.ui.theme.CardDarkElevated
import com.example.ui.theme.CardDarkSurface
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.MagentaPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.viewmodel.RadioUiState
import com.example.viewmodel.RadioViewModel

@Composable
fun HomeScreen(
    uiState: RadioUiState,
    viewModel: RadioViewModel,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val filteredStations = if (uiState.selectedCategory == StationCategory.ALL) {
        uiState.stations
    } else {
        uiState.stations.filter { it.category == uiState.selectedCategory }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp)
    ) {
        // 1. Student Profile Header Card
        item(key = "student_profile_header") {
            StudentProfileCard(
                studentName = uiState.studentName,
                studentSemester = uiState.studentSemester,
                customPhotoUri = uiState.customPhotoUri,
                onTakeFotoClick = { viewModel.setPhotoCaptureDialogVisible(true) }
            )
        }

        // 2. Genre Pills Filter Chips
        item(key = "genre_pills_row") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(StationCategory.entries.toTypedArray()) { cat ->
                    val isSelected = cat == uiState.selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setCategoryFilter(cat) },
                        label = {
                            Text(
                                text = cat.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricPurple,
                            selectedLabelColor = Color.White,
                            containerColor = CardDarkSurface,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) MagentaPrimary else Color.White.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(50)
                    )
                }
            }
        }

        // 3. Central Live Radio 3D Player Card
        item(key = "main_player_card") {
            MainPlayerCard(
                station = uiState.activeStation,
                isPlaying = uiState.isPlaying,
                isMuted = uiState.isMuted,
                elapsedFormatted = viewModel.formatElapsedTime(uiState.elapsedSeconds),
                connectedCastDevice = uiState.connectedCastDevice,
                onTogglePlay = { viewModel.togglePlayPause() },
                onToggleMute = { viewModel.toggleMute() },
                onSkipBackward = { viewModel.skipBackward(10) },
                onSkipForward = { viewModel.skipForward(10) },
                onCastClick = { viewModel.setCastSheetVisible(true) }
            )
        }

        // 4. Catálogo de Emisoras Header Row
        item(key = "catalog_header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Catálogo de Emisoras",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 19.sp,
                            letterSpacing = (-0.4).sp
                        )
                    )

                    // Available Count Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(CardDarkElevated)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${filteredStations.size} disponibles",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Filter button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardDarkSurface)
                        .clickable { viewModel.setFilterSheetVisible(true) }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("catalog_filter_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filtros",
                        tint = MagentaPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (uiState.selectedCategory == StationCategory.ALL) "Filtros" else uiState.selectedCategory.label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MagentaPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        // 5. Station List Cards
        items(
            items = filteredStations,
            key = { it.id }
        ) { station ->
            val isActive = station.id == uiState.activeStation.id
            val isFavorite = uiState.favoriteStationIds.contains(station.id)

            StationCard(
                station = station,
                isActive = isActive,
                isPlaying = uiState.isPlaying,
                isFavorite = isFavorite,
                onSelect = { viewModel.selectStation(station) },
                onToggleFavorite = { viewModel.toggleFavorite(station.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
