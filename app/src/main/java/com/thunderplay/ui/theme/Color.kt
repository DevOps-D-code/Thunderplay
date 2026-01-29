package com.thunderplay.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Palette - Electric Violet
val Violet100 = Color(0xFFEDE9FE)
val Violet200 = Color(0xFFDDD6FE)
val Violet300 = Color(0xFFC4B5FD)
val Violet400 = Color(0xFFA78BFA)
val Violet500 = Color(0xFF8B5CF6)
val Violet600 = Color(0xFF7C3AED)  // Primary
val Violet700 = Color(0xFF6D28D9)
val Violet800 = Color(0xFF5B21B6)
val Violet900 = Color(0xFF4C1D95)

// Secondary Palette - Cyan
val Cyan100 = Color(0xFFCFFAFE)
val Cyan200 = Color(0xFFA5F3FC)
val Cyan300 = Color(0xFF67E8F9)
val Cyan400 = Color(0xFF22D3EE)
val Cyan500 = Color(0xFF06B6D4)  // Secondary
val Cyan600 = Color(0xFF0891B2)
val Cyan700 = Color(0xFF0E7490)

// Accent - Pink Glow
val Pink300 = Color(0xFFF9A8D4)
val Pink400 = Color(0xFFF472B6)  // Accent
val Pink500 = Color(0xFFEC4899)

// Dark Theme Background
val DeepSpace = Color(0xFF0F0F1A)
val SpaceGray100 = Color(0xFF1A1A2E)
val SpaceGray200 = Color(0xFF16213E)
val SpaceGray300 = Color(0xFF0F3460)

// Glass Surface Colors
val GlassWhite = Color(0x14FFFFFF)  // 8% white
val GlassBorder = Color(0x33FFFFFF)  // 20% white
val GlassHighlight = Color(0x0DFFFFFF)  // 5% white

// Text Colors
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFFCBD5E1)
val TextMuted = Color(0xFF64748B)

// Gradients (for use with Brush.linearGradient)
val GradientVioletCyan = listOf(Violet600, Cyan500)
val GradientVioletPink = listOf(Violet600, Pink400)
val GradientCyanPink = listOf(Cyan500, Pink400)
val GradientDark = listOf(DeepSpace, SpaceGray200)
