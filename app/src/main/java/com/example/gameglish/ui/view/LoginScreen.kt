// Kotlin
package com.example.gameglish.ui.view

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gameglish.R
import com.example.gameglish.ui.navigation.Screen
import com.example.gameglish.ui.viewmodel.LoginState
import com.example.gameglish.ui.viewmodel.LoginViewModel

/**
 * Login screen siguiendo las últimas tendencias de Material 3.
 * - Gradiente dinámico basado en el esquema de color actual.
 * - Tarjeta "glass" con fondo borroso/aclarado.
 * - Campos con *leadingIcon*.
 * - Botón primario ancho y secundario para registro.
 * - Reusa el logo de *ic_launcher_foreground*.
 */
@Composable
fun LoginScreen(
    navController: NavHostController,
    onNavigateToRegister: () -> Unit,
    loginViewModel: LoginViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    // ----------- STATE -----------
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()
    val isFirstLogin by loginViewModel.isFirstLogin.collectAsStateWithLifecycle()

    // ----------- UI -----------
    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary.copy(alpha = .6f),
            MaterialTheme.colorScheme.surface
        )
    )

    Box(
        modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Logo ---
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(32.dp))

            /**
             * Tarjeta central con efecto "glass" (fondo semitransparente)
             */
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(24.dp))

                    // ---- Email ----
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Filled.Email, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                    // ---- Password ----
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(icon, contentDescription = null)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(Modifier.height(24.dp))

                    // ---- Login Button ----
                    Button(
                        onClick = { loginViewModel.iniciarSesion(email, password) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Iniciar sesión", style = MaterialTheme.typography.titleMedium)
                    }

                    // --- Forgot / Register row ---
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        TextButton(onClick = { /* TODO: forgot pass */ }) {
                            Text("¿Olvidaste tu contraseña?", fontSize = 14.sp)
                        }
                        TextButton(onClick = onNavigateToRegister) {
                            Text("Regístrate", fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // -------- Loading overlay --------
        AnimatedVisibility(
            visible = loginState == LoginState.Loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = .35f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }

    // -------- HANDLE LOGIN STATE --------
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
                loginViewModel.resetState()
            }
            LoginState.Error -> {
                Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                loginViewModel.resetState()
            }
            else -> Unit
        }
    }
}
