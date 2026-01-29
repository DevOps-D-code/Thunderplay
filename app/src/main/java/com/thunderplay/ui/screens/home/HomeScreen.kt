package com.thunderplay.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.thunderplay.domain.model.Track
import com.thunderplay.ui.components.GlassCard
import com.thunderplay.ui.components.GlassIconButton
import com.thunderplay.ui.components.GlowButton
import com.thunderplay.ui.theme.Cyan500
import com.thunderplay.ui.theme.DeepSpace
import com.thunderplay.ui.theme.GlassWhite
import com.thunderplay.ui.theme.GradientVioletCyan
import com.thunderplay.ui.theme.Pink400
import com.thunderplay.ui.theme.SpaceGray100
import com.thunderplay.ui.theme.TextMuted
import com.thunderplay.ui.theme.TextPrimary
import com.thunderplay.ui.theme.TextSecondary
import com.thunderplay.ui.theme.Violet600

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToNowPlaying: (String) -> Unit,
    onNavigateToLibrary: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    HomeScreenContent(
        uiState = uiState,
        onNavigateToSearch = onNavigateToSearch,
        onNavigateToNowPlaying = onNavigateToNowPlaying,
        onNavigateToLibrary = onNavigateToLibrary
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToSearch: () -> Unit,
    onNavigateToNowPlaying: (String) -> Unit,
    onNavigateToLibrary: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepSpace, SpaceGray100, DeepSpace)
                )
            )
    ) {
        // Background gradient orbs for visual interest
        BackgroundOrbs()
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header
            item {
                HomeHeader(
                    greeting = uiState.greeting,
                    onSearchClick = onNavigateToSearch,
                    onLibraryClick = onNavigateToLibrary
                )
            }
            
            // Hero section
            item {
                HeroSection(onExploreClick = onNavigateToSearch)
            }
            
            // Quick Play section
            item {
                SectionTitle("Quick Play")
                if (uiState.error != null) {
                   Box(modifier = Modifier.padding(16.dp).background(Color.Red.copy(alpha=0.2f)).padding(16.dp)) {
                       Text("Error: ${uiState.error}", color = Color.White)
                   }
                }
                QuickPlayRow(onNavigateToSearch)
            }
            
            // Trending section with real data
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle("Trending Now")
                if (uiState.isLoading) {
                    LoadingRow()
                } else {
                    TrendingRow(
                        tracks = uiState.trendingTracks,
                        onTrackClick = onNavigateToNowPlaying
                    )
                }
            }
            
            // New Releases with real data
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle("New Releases")
                if (uiState.isLoading) {
                    LoadingRow()
                } else {
                    NewReleasesRow(
                        tracks = uiState.newReleases,
                        onTrackClick = onNavigateToNowPlaying
                    )
                }
            }
        }
    }
}

@Composable
private fun BackgroundOrbs() {
    // Use simple gradients instead of blur for performance
    Box(modifier = Modifier.fillMaxSize()) {
        // Violet gradient (no blur - much faster)
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Violet600.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .align(Alignment.TopEnd)
        )
        // Cyan gradient
        Box(
            modifier = Modifier
                .size(180.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Cyan500.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .align(Alignment.BottomStart)
        )
    }
}

@Composable
private fun HomeHeader(
    greeting: String,
    onSearchClick: () -> Unit,
    onLibraryClick: () -> Unit // Kept for interface compatibility but might be unused in header now
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Row: Greeting & Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "Thunderplay",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            
            GlassIconButton(onClick = { /* Settings TODO */ }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = TextPrimary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Fast Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(GlassWhite)
                .clickable(onClick = onSearchClick)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Search songs, artists...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun HeroSection(onExploreClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 28.dp,
            backgroundColor = Color(0x1AFFFFFF)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Discover New Music",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Explore millions of tracks from around the world",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(20.dp))
                GlowButton(
                    text = "Start Listening",
                    onClick = onExploreClick
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun QuickPlayRow(onNavigateToSearch: () -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(listOf("Pop Hits", "Chill Vibes", "Workout", "Focus")) { genre ->
            QuickPlayChip(genre, onClick = onNavigateToSearch)
        }
    }
}

@Composable
private fun QuickPlayChip(label: String, onClick: () -> Unit) {
    GlassCard(
        onClick = onClick,
        cornerRadius = 50.dp,
        modifier = Modifier.height(44.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(GradientVioletCyan)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun LoadingRow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Violet600, strokeWidth = 3.dp)
    }
}

@Composable
private fun TrendingRow(
    tracks: List<Track>,
    onTrackClick: (String) -> Unit
) {
    if (tracks.isEmpty()) {
        PlaceholderRow()
        return
    }
    
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(tracks, key = { it.id }) { track ->
            TrackCard(
                track = track,
                onClick = { onTrackClick(track.id) }
            )
        }
    }
}

@Composable
private fun NewReleasesRow(
    tracks: List<Track>,
    onTrackClick: (String) -> Unit
) {
    if (tracks.isEmpty()) {
        PlaceholderRow()
        return
    }
    
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(tracks, key = { it.id }) { track ->
            TrackCard(
                track = track,
                onClick = { onTrackClick(track.id) }
            )
        }
    }
}

@Composable
private fun PlaceholderRow() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(5) { index ->
            PlaceholderCard(index)
        }
    }
}

@Composable
private fun TrackCard(
    track: Track,
    onClick: () -> Unit
) {
    GlassCard(
        onClick = onClick,
        cornerRadius = 20.dp,
        modifier = Modifier.width(160.dp)
    ) {
        Column {
            // Use lower quality image for list (imageUrl instead of imageUrlHigh)
            AsyncImage(
                model = track.imageUrl.ifEmpty { track.imageUrlHigh },
                contentDescription = track.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = track.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = track.artist,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PlaceholderCard(index: Int) {
    val gradientColors = when (index % 3) {
        0 -> listOf(Violet600, Pink400)
        1 -> listOf(Cyan500, Violet600)
        else -> listOf(Pink400, Cyan500)
    }
    
    GlassCard(
        onClick = { },
        cornerRadius = 20.dp,
        modifier = Modifier.width(160.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Track ${index + 1}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = "Artist Name",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}
