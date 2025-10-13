package android.bootcamp.mapscompose.presentation.screens.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LocationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
        shape = CircleShape,
        contentColor = MaterialTheme.colorScheme.onSurface,
        containerColor = MaterialTheme.colorScheme.surface

    ) {
        Icon(
            imageVector = Icons.Default.MyLocation,
            contentDescription = "Centrar mi ubicación"
        )
    }
}