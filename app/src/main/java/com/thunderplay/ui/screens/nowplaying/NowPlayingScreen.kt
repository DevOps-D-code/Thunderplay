package com.thunderplay.ui.screens.nowplaying

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.palette.graphics.Palette
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.BitmapImage
import com.thunderplay.player.controller.RepeatMode
import com.thunderplay.ui.components.GlassIconButton
import com.thunderplay.ui.theme.*
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    onNavigateBack: () -> Unit,
    viewModel: NowPlayingViewModel = hiltViewModel()
) {
    val track by viewModel.track.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // UI Event Collection (Toasts)
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    // Dynamic Colors State
    var dominantColor by remember { mutableStateOf(Violet600) }
    var vibrantColor by remember { mutableStateOf(Cyan500) }
    
    // Animate colors for smooth transitions
    val animatedDominant by animateColorAsState(targetValue = dominantColor, label = "dominant")
    val animatedVibrant by animateColorAsState(targetValue = vibrantColor, label = "vibrant")

    // Extract colors when track changes
    LaunchedEffect(track) {
        track?.let { currentTrack ->
            if (currentTrack.imageUrlHigh.isNotEmpty()) {
                launch(Dispatchers.IO) {
                    val loader = coil3.ImageLoader(context)
                    val request = ImageRequest.Builder(context)
                        .data(currentTrack.imageUrlHigh)
                        .allowHardware(false) // Required for Palette
                        .build()
                    
                    val result = loader.execute(request)
                    val bitmap = (result.image as? BitmapImage)?.bitmap
                    
                    bitmap?.let { bmp ->
                        Palette.from(bmp).generate { palette ->
                            palette?.let { p ->
                                dominantColor = Color(p.getDominantColor(Violet600.toArgb()))
                                vibrantColor = Color(p.getVibrantColor(Cyan500.toArgb()))
                            }
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
    ) {
        // Dynamic Background Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            animatedDominant.copy(alpha = 0.3f), // Top tint
                            DeepSpace,
                            DeepSpace
                        )
                    )
                )
        )

        // Background Blur
        track?.let { currentTrack ->
             AsyncImage(
                model = currentTrack.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(60.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.2f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(top = 64.dp), // Leave space for floating header
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Removed header from here - now floating

            Spacer(modifier = Modifier.height(16.dp))

            when {
                error != null -> {
                    // Error State
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                         Column(horizontalAlignment = Alignment.CenterHorizontally) {
                             Icon(
                                 imageVector = Icons.Default.ErrorOutline, 
                                 contentDescription = null,
                                 tint = Color.Red,
                                 modifier = Modifier.size(48.dp)
                             )
                             Spacer(modifier = Modifier.height(16.dp))
                             Text(
                                 text = "Error loading track",
                                 style = MaterialTheme.typography.titleMedium,
                                 color = Color.Red
                             )
                             Text(
                                 text = error ?: "Unknown error",
                                 style = MaterialTheme.typography.bodyMedium,
                                 color = TextSecondary,
                                 textAlign = TextAlign.Center,
                                 modifier = Modifier.padding(horizontal = 32.dp)
                             )
                             Spacer(modifier = Modifier.height(24.dp))
                             Button(
                                 onClick = { viewModel.retry() },
                                 colors = ButtonDefaults.buttonColors(containerColor = animatedVibrant)
                             ) {
                                 Text("Retry")
                             }
                         }
                    }
                }
                track == null -> {
                     // Loading State
                     Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                         CircularProgressIndicator(color = animatedVibrant)
                     }
                }
                else -> {
                    // Content State
                    track?.let { currentTrack ->
                        // Album Art
                        AlbumArtWithGlow(
                            imageUrl = currentTrack.imageUrlHigh,
                            isPlaying = playerState.isPlaying,
                            glowColor = animatedVibrant,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        // Track Info
                        TrackInfo(
                            title = currentTrack.title,
                            artist = currentTrack.artist
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        TrackActions(
                            isLiked = currentTrack.isLiked,
                            isDownloaded = currentTrack.isDownloaded,
                            tint = animatedVibrant,
                            onLike = viewModel::onLikeClick,
                            onDownload = viewModel::onDownloadClick
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Dynamic Visualizer / Slider
                        if (playerState.isPlaying) {
                            WaveformVisualizer(
                                color = animatedVibrant,
                                modifier = Modifier.fillMaxWidth().height(60.dp)
                            )
                        } else {
                           Spacer(Modifier.height(60.dp))
                        }

                        ProgressSlider(
                            progress = viewModel.sliderPosition,
                            onProgressChange = viewModel::onSliderChange,
                            onProgressChangeFinished = viewModel::onSliderChangeFinished,
                            currentPosition = formatTime(
                                (viewModel.sliderPosition * currentTrack.duration * 1000).toLong()
                            ),
                            duration = currentTrack.durationFormatted,
                            activeColor = animatedVibrant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Playback Controls
                        PlaybackControls(
                            isPlaying = playerState.isPlaying,
                            isBuffering = playerState.isBuffering,
                            shuffleEnabled = playerState.shuffleEnabled,
                            repeatMode = playerState.repeatMode,
                            accentColor = animatedDominant,
                            onPlayPause = viewModel::togglePlayPause,
                            onNext = viewModel::skipNext,
                            onPrevious = viewModel::skipPrevious,
                            onShuffle = viewModel::toggleShuffle,
                            onRepeat = viewModel::toggleRepeat
                        )

                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            }
        }

        // Floating Header - draws on TOP of everything (z-order)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.TopStart)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button with solid background for visibility
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(DeepSpace.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Text(
                    text = "NOW PLAYING",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary.copy(alpha = 0.8f)
                )
                
                // Spacer for symmetry
                Box(modifier = Modifier.size(48.dp))
            }
        }
    }
}

@Composable
fun WaveformVisualizer(
    color: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val bars = 30
    val floats = List(bars) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 300 + (index * 50) % 400,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
    }

    Canvas(modifier = modifier) {
        val barWidth = size.width / (bars * 2)
        val maxBarHeight = size.height
        
        floats.forEachIndexed { index, anim ->
            val height = maxBarHeight * anim.value
            val x = index * (barWidth * 2) + barWidth / 2
            val y = (maxBarHeight - height) / 2
            
            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth, height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
            )
        }
    }
}


@Composable
private fun AlbumArtWithGlow(
    imageUrl: String,
    isPlaying: Boolean,
    glowColor: Color,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0.9f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f),
        label = "album_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        // Dynamic Glow
        Box(
            modifier = Modifier
                .size(310.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.5f),
                            glowColor.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        AsyncImage(
            model = imageUrl,
            contentDescription = "Album Art",
            modifier = Modifier
                .size(280.dp)
                .scale(scale)
                .clip(RoundedCornerShape(32.dp)), // More rounded
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun TrackInfo(title: String, artist: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall.copy(fontSize = 28.sp), // Big & Bold
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = artist,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TrackActions(
    isLiked: Boolean,
    isDownloaded: Boolean,
    tint: Color,
    onLike: () -> Unit,
    onDownload: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onDownload) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Download",
                tint = if (isDownloaded) tint else TextSecondary.copy(alpha=0.5f),
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(Modifier.width(48.dp))

        IconButton(onClick = onLike) {
            Icon(
                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Like",
                tint = if (isLiked) Color.Red else TextSecondary.copy(alpha=0.5f),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProgressSlider(
    progress: Float,
    onProgressChange: (Float) -> Unit,
    onProgressChangeFinished: () -> Unit,
    currentPosition: String,
    duration: String,
    activeColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Slider(
            value = progress.coerceIn(0f, 1f),
            onValueChange = onProgressChange,
            onValueChangeFinished = onProgressChangeFinished,
            colors = SliderDefaults.colors(
                thumbColor = activeColor,
                activeTrackColor = activeColor,
                inactiveTrackColor = GlassWhite
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(currentPosition, style = MaterialTheme.typography.labelMedium, color = TextMuted)
            Text(duration, style = MaterialTheme.typography.labelMedium, color = TextMuted)
        }
    }
}

@Composable
private fun PlaybackControls(
    isPlaying: Boolean,
    isBuffering: Boolean,
    shuffleEnabled: Boolean,
    repeatMode: RepeatMode,
    accentColor: Color,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onShuffle) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = "Shuffle",
                tint = if (shuffleEnabled) accentColor else TextMuted,
                modifier = Modifier.size(24.dp)
            )
        }

        GlassIconButton(onClick = onPrevious, size = 56) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Previous",
                tint = TextPrimary,
                modifier = Modifier.size(28.dp)
            )
        }

        // Play/Pause - Dynamic Color
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(accentColor),
            contentAlignment = Alignment.Center
        ) {
            if (isBuffering) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(32.dp))
            } else {
                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        GlassIconButton(onClick = onNext, size = 56) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next",
                tint = TextPrimary,
                modifier = Modifier.size(28.dp)
            )
        }

        IconButton(onClick = onRepeat) {
            Icon(
                imageVector = when (repeatMode) {
                    RepeatMode.ONE -> Icons.Default.RepeatOne
                    else -> Icons.Default.Repeat
                },
                contentDescription = "Repeat",
                tint = if (repeatMode != RepeatMode.OFF) accentColor else TextMuted,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
