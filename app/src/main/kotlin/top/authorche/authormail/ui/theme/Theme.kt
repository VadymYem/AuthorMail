package top.authorche.authormail.ui.theme
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val NavyPrimary   = Color(0xFF1A3557)
val EmeraldAccent = Color(0xFF2D7D5A)
val GoldTertiary  = Color(0xFFA07000)

private val Light = lightColorScheme(
    primary = NavyPrimary, onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF), onPrimaryContainer = Color(0xFF001C3E),
    secondary = EmeraldAccent, onSecondary = Color.White,
    secondaryContainer = Color(0xFFB7F0D4), onSecondaryContainer = Color(0xFF00391E),
    tertiary = GoldTertiary, tertiaryContainer = Color(0xFFFFE08A),
    background = Color(0xFFF8F9FC), surface = Color.White,
    surfaceVariant = Color(0xFFE8EDF5), outline = Color(0xFFB0BAD0),
)
private val Dark = darkColorScheme(
    primary = Color(0xFF9DC4FF), onPrimary = Color(0xFF002F67),
    primaryContainer = Color(0xFF133050),
    secondary = Color(0xFF5DDC9A), onSecondary = Color(0xFF003826),
    secondaryContainer = Color(0xFF1A5C3E),
    tertiary = Color(0xFFFFD966), tertiaryContainer = Color(0xFF594100),
    background = Color(0xFF0E1621), surface = Color(0xFF131D2B),
    surfaceVariant = Color(0xFF1C2A3A), outline = Color(0xFF3A4F66),
)

@Composable
fun AuthorMailTheme(darkTheme: Boolean = isSystemInDarkTheme(), dynamicColor: Boolean = true, content: @Composable () -> Unit) {
    val scheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> Dark
        else      -> Light
    }
    val view = LocalView.current
    if (!view.isInEditMode) SideEffect {
        val w = (view.context as Activity).window
        w.statusBarColor = Color.Transparent.toArgb()
        WindowCompat.getInsetsController(w, view).isAppearanceLightStatusBars = !darkTheme
    }
    MaterialTheme(colorScheme = scheme, content = content)
}
