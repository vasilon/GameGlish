// Kotlin
package com.example.gameglish.ui.view.auth

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.contentcapture.ContentCaptureManager.Companion.isEnabled
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gameglish.R
import com.example.gameglish.ui.navigation.Screen
import com.example.gameglish.ui.viewmodel.LoginState
import com.example.gameglish.ui.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException

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
    var showForgotDialog by remember { mutableStateOf(false) }
    // ----------- UI -----------
    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary.copy(alpha = .6f),
            MaterialTheme.colorScheme.surface
        )
    )

    val token = stringResource(R.string.default_web_client_id)  // ID de cliente OAuth2 (web) de Firebase
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(token)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    // Launcher para la actividad de Sign-In de Google
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                val idToken = account.idToken
                if (idToken != null) {
                    // Pasar el token a ViewModel para autenticación Firebase
                    loginViewModel.signInWithGoogle(idToken)
                }
            }
        } catch (e: ApiException) {

            Toast.makeText(context, "Error de autenticación: ${e.message}", Toast.LENGTH_LONG).show()

        }
    }

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

                    GoogleSignInButton(
                        onClick = { launcher.launch(googleSignInClient.signInIntent) },
                        enabled = loginState != LoginState.Loading  // lo desactivamos mientras carga
                    )



                    Spacer(Modifier.height(12.dp))

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
                        TextButton(onClick = { showForgotDialog = true }) {
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

    if (showForgotDialog) {
        ForgotDialog(
            emailDefault = email,                // usa el campo ya escrito
            onDismiss = { showForgotDialog = false },
            onSend = { correo ->
                showForgotDialog = false
                loginViewModel.enviarCorreoReset(correo)
            }
        )
    }

    LaunchedEffect(loginState) {
        when (loginState) {
            LoginState.PasswordResetSent -> {
                Toast.makeText(
                    context,
                    "Te hemos enviado un enlace para restablecer la contraseña.\n" +
                            "Si no lo ves, revisa la carpeta Spam.",
                    Toast.LENGTH_LONG
                ).show()
                loginViewModel.resetState()
            }
            LoginState.Error -> {
                Toast.makeText(context,
                    "Ha ocurrido un error. Inténtalo de nuevo.",
                    Toast.LENGTH_LONG).show()
                loginViewModel.resetState()
            }
            else -> Unit
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

@Composable
private fun ForgotDialog(
    emailDefault: String,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit
) {
    var email by remember { mutableStateOf(emailDefault) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restablecer contraseña") },
        text = {
            Column {
                Text("Introduce tu correo y te enviaremos un enlace de restablecimiento.")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSend(email) }) { Text("Enviar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        factory = { context ->
            SignInButton(context).apply {
                setSize(SignInButton.SIZE_WIDE)
                setColorScheme(SignInButton.COLOR_AUTO)
                setOnClickListener { if (isEnabled) onClick() }
            }
        },
        update = { view ->
            view.isEnabled = enabled
        }
    )
}

