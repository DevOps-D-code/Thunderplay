package com.thunderplay.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thunderplay.domain.model.Track
import com.thunderplay.ui.components.GlassCard
import com.thunderplay.ui.theme.Cyan500
import com.thunderplay.ui.theme.DeepSpace
import com.thunderplay.ui.theme.TextPrimary
import com.thunderplay.ui.theme.Violet600

@Composable
fun LibraryScreen(
    onNavigateBack: () -> Unit,
    onTrackClick: (String) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val likedTracks by viewModel.likedTracks.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
    ) {
        // Gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Violet600.copy(alpha = 0.1f),
                            DeepSpace
                        )
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "Your Library",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // List
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (likedTracks.isEmpty()) {
                    item {
                        Text(
                            text = "No liked tracks yet. Go explore!",
                            color = TextPrimary.copy(alpha = 0.5f),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                
                items(likedTracks) { track ->
                    LibraryTrackItem(track = track, onClick = { onTrackClick(track.id) })
                }
            }
        }
    }
}

@Composable
fun LibraryTrackItem(
    track: Track,
    onClick: () -> Unit
) {
    GlassCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = track.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary.copy(alpha = 0.7f)
                )
            }
            if (track.isDownloaded) {
                 Text(text = "⬇", color = Cyan500)
            }
        }
    }
}
