package android.bootcamp.mapscompose.presentation.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddMarkerDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (title: String, snippet: String?) -> Unit
){
    // Estado local para el campo de título
    var title by rememberSaveable { mutableStateOf("") }

    // Estado local para el campo de snippet/descripción
    var snippet by rememberSaveable { mutableStateOf("") }

    // Variable para rastrear si se mostró error de validación
    var showError by rememberSaveable { mutableStateOf(false) }

    if(showDialog){
        AlertDialog(
            onDismissRequest = {
                title = ""
                snippet = ""
                showError = false
                onDismiss()
            },
            title =  {
                Text("Nuevo Marcador")
            },
            text = {
                Column {
                    // Campo obligatorio: título del marcador
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            // Ocultar error cuando el usuario empieza a escribir
                            if (showError && it.isNotBlank()) {
                                showError = false
                            }
                        },
                        label = { Text("Título *") },
                        placeholder = { Text("Ej: Mi restaurante favorito") },
                        isError = showError,
                        supportingText = {
                            if (showError) {
                                Text("El título es obligatorio")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Campo opcional: descripción/snippet del marcador
                    OutlinedTextField(
                        value = snippet,
                        onValueChange = { snippet = it },
                        label = { Text("Descripción (opcional)") },
                        placeholder = { Text("Ej: Excelente comida italiana") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton ={
                Button(
                    onClick = {
                        // Validar que el título no esté vacío
                        if (title.isBlank()) {
                            showError = true
                        } else {
                            // Crear marcador con los datos ingresados
                            onConfirm(
                                title.trim(),
                                snippet.trim().ifBlank { null }
                            )
                            // Limpiar campos después de guardar
                            title = ""
                            snippet = ""
                            showError = false
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        title = ""
                        snippet = ""
                        showError = false
                        onDismiss()
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}