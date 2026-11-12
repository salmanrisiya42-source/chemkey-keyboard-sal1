package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val ChemistryDarkColorScheme = darkColorScheme(
    primary = ChemYellow,
    onPrimary = CarbonBlack,
    primaryContainer = ChemYellowMuted,
    onPrimaryContainer = ChemYellow,
    secondary = LabTeal,
    onSecondary = CarbonBlack,
    secondaryContainer = ColorTokens.TealContainer,
    onSecondaryContainer = LabTeal,
    tertiary = LabCyan,
    onTertiary = CarbonBlack,
    background = CarbonBlack,
    onBackground = PureWhite,
    surface = CarbonSurface,
    onSurface = PureWhite,
    surfaceVariant = CarbonSurfaceElevated,
    onSurfaceVariant = MutedSlate,
    outline = CarbonOutline,
    outlineVariant = CarbonKeySurface,
    error = LabCoral,
    onError = CarbonBlack
)

private object ColorTokens {
    val TealContainer = androidx.compose.ui.graphics.Color(0xFF10302E)
}

val ChemistryShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun ChemistryKeyboardTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ChemistryDarkColorScheme,
        typography = Typography,
        shapes = ChemistryShapes,
        content = content
    )
}
