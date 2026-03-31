package ca.uqac.vistudia.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = orange,
    secondary = blue_foncee,
    tertiary = blue_clair,
    background = fond_gris,
    surface = blanc,
    onPrimary = blanc,
    onSecondary = blanc,
    onBackground = gris_fonce,
    onSurface = gris_fonce,
)

@Composable
fun VistudiaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}