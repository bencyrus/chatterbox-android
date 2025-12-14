package com.chatterboxtalk.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Android mirror of iOS `AppColors` in DesignSystem.swift.
 * App-wide color palette.
 */
object AppColors {
    // MARK: - Brand Colors
    
    /** Green accent color - used for success indicators. */
    val Green = Color(0xFFb3cbc0)

    /** Darker green accent for status bars / stronger accents. */
    val DarkGreen = Color(0xFF7fa395)

    /** Blue secondary accent color. */
    val Blue = Color(0xFFbec8e3)

    /** Darker blue accent for buttons / stronger accents. */
    val DarkBlue = Color(0xFF8f9bb8)

    /** Beige - used for detail cards and error indicators. */
    val Beige = Color(0xFFf7e4d6)

    /** Dark Beige - used for cue card backgrounds. */
    val DarkBeige = Color(0xFFe5d2c4)

    /** Sand - primary app background. */
    val Sand = Color(0xFFeeeee6)

    /**
     * Page background token (keep aligned with iOS `AppColors.pageBackground`).
     *
     * iOS uses `beige.opacity(0.4)` which is composited over the default system background.
     * Pre-composited result of beige(#F7E4D6) @ 40% over white ≈ #FCF5F0.
     */
    val PageBackground = Color(0xFFFCF5F0)
    
    // MARK: - Recording UI Colors
    
    /** Recording red - primary recording indicator. */
    val RecordingRed = Color(0xFFE74C3C)
    
    /** Recording red light - delete actions. */
    val RecordingRedLight = Color(0xFFd98f8f)
    
    /** Recording red dark - text on light recording backgrounds. */
    val RecordingRedDark = Color(0xFFC0392B)
    
    /** Recording background - pause state background. */
    val RecordingBackground = Color(0xFFE5C4B8)
    
    // MARK: - System Colors
    
    /** System gray for neutral UI elements. */
    val SystemGray = Color(0xFFAEAEB2)
    
    // MARK: - Error Colors
    
    /** Error background - destructive actions. */
    val ErrorBackground = Color(0xFFB31919)
    
    // MARK: - Text Colors
    
    /** Primary text color. */
    val TextPrimary = Color.Black
    
    /** Secondary text color (60% opacity). */
    val TextSecondary = Color.Black.copy(alpha = 0.6f)
    
    /** Tertiary text color (70% opacity). */
    val TextTertiary = Color.Black.copy(alpha = 0.7f)
    
    /** Quaternary text color (80% opacity). */
    val TextQuaternary = Color.Black.copy(alpha = 0.8f)

    /** Contrast text for dark backgrounds. */
    val TextContrast = Color.White
    
    // MARK: - Semantic Colors
    
    /** Card background (alias for darkBeige). */
    val CardBackground = DarkBeige
    
    /** Surface light (alias for beige). */
    val SurfaceLight = Beige
    
    /** Input field background. */
    val InputBackground = DarkBeige
    
    /** Border color for neutral elements. */
    val BorderNeutral = Color.Gray.copy(alpha = 0.3f)
    
    /** Badge background - neutral. */
    val BadgeBackground = Color.Gray.copy(alpha = 0.2f)
    
    /** Divider or separator color. */
    val Divider = Color.Black.copy(alpha = 0.1f)
    
    /** Overlay background for modals. */
    val OverlayBackground = Color.Black.copy(alpha = 0.4f)
    
    /** Shadow color. */
    val Shadow = Color.Black.copy(alpha = 0.15f)
}

