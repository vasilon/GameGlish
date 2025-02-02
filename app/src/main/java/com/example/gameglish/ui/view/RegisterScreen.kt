package com.example.gameglish.ui.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lint.kotlin.metadata.Visibility
import com.example.gameglish.ui.viewmodel.LoginState
import com.example.gameglish.ui.viewmodel.LoginViewModel


@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val loginState by loginViewModel.loginState.collectAsState()
    val context = LocalContext.current

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Regístrate",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF243484)
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Campo de contraseña con botón para ver contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Campo de confirmación de contraseña con botón para ver contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirma tu contraseña") },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { loginViewModel.registrarUsuario(email, password, confirmPassword) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onRegisterSuccess,  // Volver al login
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (loginState) {
                LoginState.Success -> {
                    Toast.makeText(context, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                    LaunchedEffect(loginState) {
                        kotlinx.coroutines.delay(1500)
                        loginViewModel.resetState()
                        onRegisterSuccess()
                    }
                }
                LoginState.Error -> {
                    Toast.makeText(context, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                }
                LoginState.Loading -> {
                    CircularProgressIndicator()
                }
                else -> {}
            }
        }
    }
}
