package com.chatterboxtalk.UI.Theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Android mirror of iOS `Typography.swift`.
 * Provides consistent text styles across the app.
 */
object AppTypography {
    // MARK: - Display
    
    /** Display large - for app branding and large titles. */
    val displayLarge = TextStyle(
        fontSize = 34.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 41.sp
    )
    
    /** Display medium - for section titles. */
    val displayMedium = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 34.sp
    )
    
    // MARK: - Headings
    
    /** Heading large - primary content headings. */
    val headingLarge = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 28.sp
    )
    
    /** Heading medium - subsection headings. */
    val headingMedium = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 25.sp
    )
    
    /** Heading small - card titles and small headings. */
    val headingSmall = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 22.sp
    )
    
    // MARK: - Body
    
    /** Body - standard text. */
    val body = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 22.sp
    )
    
    /** Body medium - slightly smaller body text. */
    val bodyMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 21.sp
    )
    
    // MARK: - Labels
    
    /** Label large - prominent labels. */
    val labelLarge = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp
    )
    
    /** Label medium - standard labels. */
    val labelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp
    )
    
    /** Label small - minimal labels. */
    val labelSmall = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 13.sp
    )
    
    // MARK: - Supporting Text
    
    /** Caption - secondary information. */
    val caption = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp
    )
    
    /** Footnote - fine print. */
    val footnote = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 18.sp
    )
    
    // MARK: - Specialty
    
    /** Monospaced timer - for recording timers. */
    val monospacedTimer = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 43.sp
    )
    
    // Legacy aliases (deprecated in favor of new names above)
    val title = displayMedium
    val heading = headingLarge
}
