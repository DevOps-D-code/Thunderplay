package com.thunderplay.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.thunderplay.R

// Using system fonts initially - can replace with custom Google Fonts
val ThunderplayFontFamily = FontFamily.Default

val Typography = Typography(
    // Display - Large hero text (Song Titles on Full Screen)
    displayLarge = TextStyle(
        fontFamily = ThunderplayFontFamily,
        fontWeight = FontWeight.Bold, // Heavy impact
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-1).sp // Tighter for large text (Modern look)
    ),
    displayMedium = TextStyle(
        fontFamily = ThunderplayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.5).sp
    ),
    displaySmall = TextStyle(
        fontFamily = ThunderplayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    
    // Headlines
    headlineLarge = TextStyle(
        fontFamily = ThunderplayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = ThunderplayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = ThunderplayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    
    // Titles
    titleLarge = TextStyle(
        fontFamily = ThunderplayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle( // Used for "NOW PLAYING" labels
        fontFamily = ThunderplayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 24.sp,
        letterSpacing = 2.sp // Wide spacing for premium "Label" look
    ),
    titleSmall = TextStyle(
        fontFamily = ThunderplayFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // Body
    bodyLarge = TextStyle( // Artist names
        fontFamily = ThunderplayFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = ThunderplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = ThunderplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    
    // Labels
    labelLarge = TextStyle(
        fontFamily = ThunderplayFontFamily,
        fontWeight = FontWeight.ExtraBold, // Very buttons
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 1.sp
    ),
    labelMedium = TextStyle( // Timestamps
        fontFamily = ThunderplayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.sp // Mono-like spacing for numbers
    ),
    labelSmall = TextStyle(
        fontFamily = ThunderplayFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
