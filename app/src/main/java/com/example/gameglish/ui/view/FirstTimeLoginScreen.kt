// Kotlin
package com.example.gameglish.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstTimeLoginScreen(
    onRegisterSuccess: () -> Unit,
    registerUser: (nombre: String, nivelSeleccionado: String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var nivelSeleccionado by remember { mutableStateOf("A1") }
    val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2", "NATIVE")

    // Dropdown menu expanded state
    var expanded by remember { mutableStateOf(false) }

    val gradientBackground = Brush.linearGradient(
        colors = listOf(Color(0xFF2be4dc), Color(0xFF243484))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Complete your Profile",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF243484)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Level dropdown using Material3 ExposedDropdownMenuBox
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = nivelSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Level") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(), // Anchors the dropdown menu to the TextField
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = TextFieldDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    levels.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                nivelSeleccionado = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    // Llama a registerUser con el nombre y nivel seleccionados
                    registerUser(nombre, nivelSeleccionado)
                    // Después de actualizar el perfil, se debería marcar el primer login completo.
                    onRegisterSuccess()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }
        }
    }
}