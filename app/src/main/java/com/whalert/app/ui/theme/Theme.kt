package com.whalert.app.ui.theme

import android.app.Activity
nimport android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * WhAlert theme configuration
 */
object WhAlertTheme {
    val colorSchemeLight = lightColorScheme(
        primary = WhAlertColors.Primary,
        primaryContainer = WhAlertColors.PrimaryContainer,
        onPrimary = WhAlertColors.OnPrimary,
        onPrimaryContainer = WhAlertColors.OnPrimaryContainer,
        secondary = WhAlertColors.Secondary,
        secondaryContainer = WhAlertColors.SecondaryContainer,
        onSecondary = WhAlertColors.OnSecondary,
        onSecondaryContainer = WhAlertColors.OnSecondaryContainer,
        tertiary = WhAlertColors.Tertiary,
        tertiaryContainer = WhAlertColors.TertiaryContainer,
        onTertiary = WhAlertColors.OnTertiary,
        onTertiaryContainer = WhAlertColors.OnTertiaryContainer,
        error = WhAlertColors.Error,
        errorContainer = WhAlertColors.ErrorContainer,
        onError = WhAlertColors.OnError,
        onErrorContainer = WhAlertColors.OnErrorContainer,
        background = WhAlertColors.Background,
        onBackground = WhAlertColors.OnBackground,
        surface = WhAlertColors.Surface,
        onSurface = WhAlertColors.OnSurface,
        surfaceVariant = WhAlertColors.SurfaceVariant,
        onSurfaceVariant = WhAlertColors.OnSurfaceVariant,
        outline = WhAlertColors.Outline,
        outlineVariant = WhAlertColors.OutlineVariant,
        scrim = WhAlertColors.Scrim,
        inverseSurface = WhAlertColors.InverseSurface,
        inverseOnSurface = WhAlertColors.InverseOnSurface,
        inversePrimary = WhAlertColors.InversePrimary
    )

    val colorSchemeDark = darkColorScheme(
        primary = WhAlertColors.PrimaryDark,
        primaryContainer = WhAlertColors.PrimaryContainerDark,
        onPrimary = WhAlertColors.OnPrimaryDark,
        onPrimaryContainer = WhAlertColors.OnPrimaryContainerDark,
        secondary = WhAlertColors.SecondaryDark,
        secondaryContainer = WhAlertColors.SecondaryContainerDark,
        onSecondary = WhAlertColors.OnSecondaryDark,
        onSecondaryContainer = WhAlertColors.OnSecondaryContainerDark,
        tertiary = WhAlertColors.TertiaryDark,
        tertiaryContainer = WhAlertColors.TertiaryContainerDark,
        onTertiary = WhAlertColors.OnTertiaryDark,
        onTertiaryContainer = WhAlertColors.OnTertiaryContainerDark,
        error = WhAlertColors.ErrorDark,
        errorContainer = WhAlertColors.ErrorContainerDark,
        onError = WhAlertColors.OnErrorDark,
        onErrorContainer = WhAlertColors.OnErrorContainerDark,
        background = WhAlertColors.BackgroundDark,
        onBackground = WhAlertColors.OnBackgroundDark,
        surface = WhAlertColors.SurfaceDark,
        onSurface = WhAlertColors.OnSurfaceDark,
        surfaceVariant = WhAlertColors.SurfaceVariantDark,
        onSurfaceVariant = WhAlertColors.OnSurfaceVariantDark,
        outline = WhAlertColors.OutlineDark,
        outlineVariant = WhAlertColors.OutlineVariantDark,
        scrim = WhAlertColors.ScrimDark,
        inverseSurface = WhAlertColors.InverseSurfaceDark,
        inverseOnSurface = WhAlertColors.InverseOnSurfaceDark,
        inversePrimary = WhAlertColors.InversePrimaryDark
    )
}

/**
 * Custom color definitions for WhAlert
 */
object WhAlertColors {
    // Light theme colors
    val Primary = 0xFF6200EE.toInt()
    val PrimaryContainer = 0xFFEADDFF.toInt()
    val OnPrimary = 0xFFFFFFFF.toInt()
    val OnPrimaryContainer = 0xFF1F0066.toInt()
    
    val Secondary = 0xFF03DAC6.toInt()
    val SecondaryContainer = 0xFF80F2E6.toInt()
    val OnSecondary = 0xFFFFFFFF.toInt()
    val OnSecondaryContainer = 0xFF00363C.toInt()
    
    val Tertiary = 0xFFFF4081.toInt()
    val TertiaryContainer = 0xFFFFD9E3.toInt()
    val OnTertiary = 0xFFFFFFFF.toInt()
    val OnTertiaryContainer = 0xFF5F142B.toInt()
    
    val Error = 0xFFB3261E.toInt()
    val ErrorContainer = 0xFFF9DEDC.toInt()
    val OnError = 0xFFFFFFFF.toInt()
    val OnErrorContainer = 0xFF410E0B.toInt()
    
    val Background = 0xFFFFFFFF.toInt()
    val OnBackground = 0xFF1C1B1F.toInt()
    val Surface = 0xFFFFFFFF.toInt()
    val OnSurface = 0xFF1C1B1F.toInt()
    val SurfaceVariant = 0xFFF5F5F5.toInt()
    val OnSurfaceVariant = 0xFF49454F.toInt()
    
    val Outline = 0xFF79747E.toInt()
    val OutlineVariant = 0xFFCAC4D0.toInt()
    val Scrim = 0xFF000000.toInt()
    val InverseSurface = 0xFF313033.toInt()
    val InverseOnSurface = 0xFFF5F5F5.toInt()
    val InversePrimary = 0xFFD0BCFF.toInt()
    
    // Dark theme colors
    val PrimaryDark = 0xFFD0BCFF.toInt()
    val PrimaryContainerDark = 0xFF4F3789.toInt()
    val OnPrimaryDark = 0xFF21005D.toInt()
    val OnPrimaryContainerDark = 0xFFEADDFF.toInt()
    
    val SecondaryDark = 0xFF89E0D6.toInt()
    val SecondaryContainerDark = 0xFF004D40.toInt()
    val OnSecondaryDark = 0xFF001F1B.toInt()
    val OnSecondaryContainerDark = 0xFF80F2E6.toInt()
    
    val TertiaryDark = 0xFFFFB3C7.toInt()
    val TertiaryContainerDark = 0xFF7D2742.toInt()
    val OnTertiaryDark = 0xFF6B002B.toInt()
    val OnTertiaryContainerDark = 0xFFFFD9E3.toInt()
    
    val ErrorDark = 0xFFFFB4AB.toInt()
    val ErrorContainerDark = 0xFF93000A.toInt()
    val OnErrorDark = 0xFF690005.toInt()
    val OnErrorContainerDark = 0xFFFFB4AB.toInt()
    
    val BackgroundDark = 0xFF1C1B1F.toInt()
    val OnBackgroundDark = 0xFFE6E1E5.toInt()
    val SurfaceDark = 0xFF1C1B1F.toInt()
    val OnSurfaceDark = 0xFFE6E1E5.toInt()
    val SurfaceVariantDark = 0xFF49454F.toInt()
    val OnSurfaceVariantDark = 0xFFCAC4D0.toInt()
    
    val OutlineDark = 0xFF938F99.toInt()
    val OutlineVariantDark = 0xFF49454F.toInt()
    val ScrimDark = 0xFF000000.toInt()
    val InverseSurfaceDark = 0xFFE6E1E5.toInt()
    val InverseOnSurfaceDark = 0xFF313033.toInt()
    val InversePrimaryDark = 0xFF6200EE.toInt()
}

@Composable
fun WhAlertTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> WhAlertTheme.colorSchemeDark
        else -> WhAlertTheme.colorSchemeLight
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WhAlertTypography,
        content = content
    )
}
