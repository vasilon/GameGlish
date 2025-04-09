// Kotlin
package com.example.gameglish.ui.view

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gameglish.R
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.repository.RepositoryUsuario
import com.example.gameglish.ui.navigation.Screen
import com.example.gameglish.ui.viewmodel.LoginState
import com.example.gameglish.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavHostController,
    onNavigateToRegister: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()
    val isFirstLogin by loginViewModel.isFirstLogin.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    val gradientBackground = Brush.linearGradient(
        colors = listOf(Color(0xFF2be4dc), Color(0xFF243484))
    )


    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo Blanco
        Box(modifier = Modifier.fillMaxSize().background(Color.White))

        val gradientRadius = 800f

        // Esquinas de colores
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF2382C5), Color.Transparent),
                    center = Offset(0f, 0f),
                    radius = gradientRadius
                )
            )
        )


        // Contenido principal centrado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
//            Image(
//                painter = painterResource(id = R.drawable.placeholder_logo),
//                contentDescription = "GruponexcomLogo",
//                modifier = Modifier.size(200.dp).padding(bottom = 16.dp)
//            )

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF243484)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Electrónico") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF243484),
                            unfocusedBorderColor = Color(0xFF243484)
                        )

                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF243484),
                            unfocusedBorderColor = Color(0xFF243484)
                        ),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            loginViewModel.iniciarSesion(email, password)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF243484),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Iniciar Sesión")
                    }

                    // Manejo del estado de login
                    LaunchedEffect(loginState) {
                        when (loginState) {
                            LoginState.Success -> {
                                if (isFirstLogin == true) {
                                    navController.navigate(Screen.FirstTimeLogin.route) {
                                        popUpTo("authFlow") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("mainFlow") {
                                        popUpTo("authFlow") { inclusive = true }
                                    }
                                }
                                loginViewModel.resetState() // Resetear el estado después de manejarlo
                            }
                            LoginState.Error -> {
                                Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                                loginViewModel.resetState()
                            }
                            else -> { /* No hacer nada en Idle o Loading */ }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFF243484)
                        )
                    ) {
                        Text("¿No tienes cuenta? Regístrate", color = Color(0xFF243484))
                    }
                }
            }
        }
    }
}