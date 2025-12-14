package com.chatterboxtalk.UI.Views

import androidx.compose.ui.graphics.Color

/**
 * Android mirror of the iOS `AppColors` palette defined in `DesignSystem.swift`.
 *
 * These values are copied exactly from the Swift hex codes to keep the apps visually aligned.
 * iOS hex values: green=0xb3cbc0, darkGreen=0x7fa395, blue=0xbec8e3, darkBlue=0x8f9bb8,
 * beige=0xf7e4d6, darkBeige=0xe5d2c4, sand=0xeeeee6.
 */
object AppColors {
    // Green accent color - used for success indicators.
    val Green = Color(0xFFB3CBC0)

    // Darker green accent for status bars / stronger accents.
    val DarkGreen = Color(0xFF7FA395)

    // Blue secondary accent color.
    val Blue = Color(0xFFBEC8E3)

    // Darker blue accent for buttons / stronger accents.
    val DarkBlue = Color(0xFF8F9BB8)

    // Beige - used for detail cards and error indicators.
    val Beige = Color(0xFFF7E4D6)

    // Dark Beige - used for cue card backgrounds.
    val DarkBeige = Color(0xFFE5D2C4)

    // Sand - primary app background.
    val Sand = Color(0xFFEEEEE6)

    // Primary text color.
    val TextPrimary = Color.Black

    // Contrast text for dark backgrounds.
    val TextContrast = Color.White
}


