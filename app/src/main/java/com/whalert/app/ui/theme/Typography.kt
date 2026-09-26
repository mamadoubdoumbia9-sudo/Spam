package com.whalert.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * WhAlert typography configuration
 */
object WhAlertTypography {
    // Define font families
    val Roboto = FontFamily(
        Font(android.R.font.roboto, FontWeight.Normal),
        Font(android.R.font.roboto_bold, FontWeight.Bold),
        Font(android.R.font.roboto_medium, FontWeight.Medium),
        Font(android.R.font.roboto_light, FontWeight.Light),
        Font(android.R.font.roboto_thin, FontWeight.Thin)
    )

    val typography = Typography(
        // Headlines
        headlineLarge = TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),

        // Titles
        titleLarge = TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.1.sp
        ),

        // Body
        bodyLarge = TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),

        // Labels
        labelLarge = TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        labelSmall = TextStyle(
            fontFamily = Roboto,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        )
    )
}

// Extend Typography with custom styles
val Typography.customHeadline1: TextStyle
    @Composable get() = TextStyle(
        fontFamily = WhAlertTypography.Roboto,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.25).sp
    )

val Typography.customHeadline2: TextStyle
    @Composable get() = TextStyle(
        fontFamily = WhAlertTypography.Roboto,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 30.sp,
        lineHeight = 38.sp,
        letterSpacing = 0.sp
    )

val Typography.customHeadline3: TextStyle
    @Composable get() = TextStyle(
        fontFamily = WhAlertTypography.Roboto,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    )

val Typography.customHeadline4: TextStyle
    @Composable get() = TextStyle(
        fontFamily = WhAlertTypography.Roboto,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )

val Typography.customHeadline5: TextStyle
    @Composable get() = TextStyle(
        fontFamily = WhAlertTypography.Roboto,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    )

val Typography.customHeadline6: TextStyle
    @Composable get() = TextStyle(
        fontFamily = WhAlertTypography.Roboto,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    )

val Typography.customSubtitle1: TextStyle
    @Composable get() = TextStyle(
        fontFamily = WhAlertTypography.Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.15.sp
    )

val Typography.customSubtitle2: TextStyle
    @Composable get() = TextStyle(
        fontFamily = WhAlertTypography.Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    )

val Typography.customBody1: TextStyle
    @Composable get() = TextStyle(
        fontFamily = WhAlertTypography.Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

val Typography.customBody2: TextStyle
    @Composable get() = TextStyle(
        fontFamily = WhAlertTypography.Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )

val Typography.customButton: TextStyle
    @Composable get() = TextStyle(
        fontFamily = WhAlertTypography.Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )

val Typography.customCaption: TextStyle
    @Composable get() = TextStyle(
        fontFamily = WhAlertTypography.Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )

val Typography.customOverline: TextStyle
    @Composable get() = TextStyle(
        fontFamily = WhAlertTypography.Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.5.sp
    )
