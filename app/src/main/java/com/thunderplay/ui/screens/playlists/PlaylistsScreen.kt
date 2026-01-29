package com.thunderplay.ui.screens.playlists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.thunderplay.ui.theme.GradientVioletCyan

@Composable
fun PlaylistsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(GradientVioletCyan)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Playlists", color = Color.White)
    }
}
