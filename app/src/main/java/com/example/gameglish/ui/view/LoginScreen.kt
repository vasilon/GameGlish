package com.example.gameglish.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gameglish.ui.viewmodel.LoginState
import com.example.gameglish.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState = loginViewModel.loginState.collectAsState()


    // Definir el degradado de fondo
    val gradientBackground = Brush.linearGradient(
        colors = listOf(Color(0xFF2be4dc), Color(0xFF243484))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground), // Aplicar el fondo degradado
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
                text = "GameGlish",
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

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { loginViewModel.iniciarSesion(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { loginViewModel.registrarUsuario(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (loginState.value) {
                LoginState.Success -> {
                    onLoginSuccess()
                }
                LoginState.Error -> {
                    Text("Error al iniciar sesión o registrar usuario", color = Color.Red)
                }
                LoginState.Loading -> {
                    CircularProgressIndicator()
                }
                else -> {
                }
            }



        }

    }

}
