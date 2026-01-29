package com.thunderplay.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thunderplay.ui.theme.GlassBorder
import com.thunderplay.ui.theme.GlassHighlight
import com.thunderplay.ui.theme.GlassWhite

/**
 * Glassmorphism card component with frosted glass effect.
 * Features:
 * - Semi-transparent background
 * - Subtle border glow
 * - Press animation with spring physics
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    cornerRadius: Dp = 20.dp,
    backgroundColor: Color = GlassWhite,
    borderColor: Color = GlassBorder,
    borderWidth: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "glass_card_scale"
    )
    
    val shape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = backgroundColor.alpha * 0.7f)
                    )
                )
            )
            .border(
                width = borderWidth,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        borderColor,
                        borderColor.copy(alpha = borderColor.alpha * 0.3f)
                    )
                ),
                shape = shape
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(16.dp),
        content = content
    )
}

/**
 * Elevated glass card with glow effect (optimized - no blur)
 */
@Composable
fun GlassCardElevated(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    glowColor: Color = Color(0x337C3AED), // Violet glow
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        // Simple glow layer without blur
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(2.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(glowColor, Color.Transparent)
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
        )
        
        // Glass card
        GlassCard(
            modifier = Modifier.matchParentSize(),
            onClick = onClick,
            cornerRadius = 24.dp,
            backgroundColor = GlassWhite.copy(alpha = 0.12f),
            content = content
        )
    }
}
