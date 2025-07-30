package dev.belalkhan.gemininanoworkshop.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


import androidx.compose.ui.graphics.Color

// Seed color based on primary blue used in tab highlight and icons
val seed = Color(0xFF2E5AAC)

// Light Theme Colors
val md_theme_light_primary = Color(0xFF2E5AAC)  // Deep blue (selected tab & icons)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFD6E2FF)
val md_theme_light_onPrimaryContainer = Color(0xFF001A41)

val md_theme_light_secondary = Color(0xFF5B6C8F)  // Muted blue-gray for icons/text
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFDDE2F8)
val md_theme_light_onSecondaryContainer = Color(0xFF161E32)

val md_theme_light_tertiary = Color(0xFFCE9C67)  // Warm accent (for avatars or highlights)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFFFDCC0)
val md_theme_light_onTertiaryContainer = Color(0xFF3B2005)

val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)

val md_theme_light_outline = Color(0xFFCAC4D0)
val md_theme_light_outlineVariant = Color(0xFFE7E0EC)

val md_theme_light_background = Color(0xFFF6F8FC)  // Slight off-white background
val md_theme_light_onBackground = Color(0xFF1B1B1F)

val md_theme_light_surface = Color(0xFFFFFFFF)
val md_theme_light_onSurface = Color(0xFF1B1B1F)
val md_theme_light_surfaceVariant = Color(0xFFE1E6F0)
val md_theme_light_onSurfaceVariant = Color(0xFF43474E)

val md_theme_light_inverseSurface = Color(0xFF2F3033)
val md_theme_light_inverseOnSurface = Color(0xFFF1F0F4)
val md_theme_light_inversePrimary = Color(0xFFAFC6FF)

val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = md_theme_light_primary
val md_theme_light_scrim = Color(0xFF000000)


// Dark Theme Colors
val md_theme_dark_primary = Color(0xFFAFC6FF)
val md_theme_dark_onPrimary = Color(0xFF003060)
val md_theme_dark_primaryContainer = Color(0xFF00478A)
val md_theme_dark_onPrimaryContainer = Color(0xFFD6E2FF)

val md_theme_dark_secondary = Color(0xFFBBC6E0)
val md_theme_dark_onSecondary = Color(0xFF283044)
val md_theme_dark_secondaryContainer = Color(0xFF404A60)
val md_theme_dark_onSecondaryContainer = Color(0xFFDDE2F8)

val md_theme_dark_tertiary = Color(0xFFFFB77F)
val md_theme_dark_onTertiary = Color(0xFF552D00)
val md_theme_dark_tertiaryContainer = Color(0xFF7A4500)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFDCC0)

val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)

val md_theme_dark_outline = Color(0xFF8E8E99)
val md_theme_dark_outlineVariant = Color(0xFF44474E)

val md_theme_dark_background = Color(0xFF121418)
val md_theme_dark_onBackground = Color(0xFFE3E2E6)

val md_theme_dark_surface = Color(0xFF1C1E23)
val md_theme_dark_onSurface = Color(0xFFE3E2E6)
val md_theme_dark_surfaceVariant = Color(0xFF43474E)
val md_theme_dark_onSurfaceVariant = Color(0xFFC4C6CF)

val md_theme_dark_inverseSurface = Color(0xFFE3E2E6)
val md_theme_dark_inverseOnSurface = Color(0xFF2F3033)
val md_theme_dark_inversePrimary = Color(0xFF2E5AAC)

val md_theme_dark_shadow = Color(0xFF000000)
val md_theme_dark_surfaceTint = md_theme_dark_primary
val md_theme_dark_scrim = Color(0xFF000000)
