package com.example.seguimiento.features.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: (isAdmin: Boolean) -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val imageResourceId = remember(context) {
        context.resources.getIdentifier("login", "drawable", context.packageName)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Imagen de fondo
            if (imageResourceId != 0) {
                Image(
                    painter = painterResource(id = imageResourceId),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }

            // 2. Contenido
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Bajado un poco (de 280.dp a 310.dp) para equilibrar con el icono
                Spacer(modifier = Modifier.height(310.dp))

                // Campo Usuario
                CustomInputField(
                    value = viewModel.email.value,
                    onValueChange = { viewModel.email.onChange(it) },
                    placeholder = "usuario",
                    icon = Icons.Default.Person,
                    error = viewModel.email.error
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Contraseña
                CustomInputField(
                    value = viewModel.password.value,
                    onValueChange = { viewModel.password.onChange(it) },
                    placeholder = "contraseña",
                    icon = Icons.Default.Lock,
                    isPassword = true,
                    error = viewModel.password.error
                )

                // Texto Olvidó Contraseña - Ahora con color Naranja para mayor visibilidad sobre el fondo
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = Color(0xFFD37506), 
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 10.dp)
                        .clickable { onNavigateToForgotPassword() }
                )

                Spacer(modifier = Modifier.height(25.dp))

                // Botón Iniciar Sesión
                Button(
                    onClick = {
                        if (viewModel.isFormValid) {
                            val user = viewModel.email.value
                            val pass = viewModel.password.value

                            scope.launch {
                                when {
                                    user == "admin@gmail.com" && pass == "admin" -> {
                                        onLoginSuccess(true)
                                    }
                                    user == "brianmeza1928@gmail.com" && pass == "123456" -> {
                                        onLoginSuccess(false)
                                    }
                                    else -> {
                                        snackbarHostState.showSnackbar("❌ Credenciales incorrectas")
                                    }
                                }
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("⚠️ Completa los campos correctamente")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD37506)
                    )
                ) {
                    Text(text = "Iniciar Sesión", fontSize = 18.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Texto de registro - También resaltado para que no se pierda
                Row(
                    modifier = Modifier.padding(bottom = 30.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "¿No tienes cuenta? ", color = Color.White, fontSize = 14.sp)
                    Text(
                        text = "Regístrate",
                        color = Color(0xFFD37506),
                        fontWeight = FontWeight.ExtraBold,
                        textDecoration = TextDecoration.Underline,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onNavigateToRegister() }
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    error: String? = null // Recibe el error del ViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp) // Pequeño margen entre campos
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (error != null) Color.Red else Color(0xFF7B61FF)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White, // Mantiene el fondo blanco aunque haya error
                focusedIndicatorColor = if (error != null) Color.Red else Color.Transparent,
                unfocusedIndicatorColor = if (error != null) Color.Red else Color.Transparent,
                cursorColor = Color.Black,
                errorIndicatorColor = Color.Red
            ),
            isError = error != null,
            singleLine = true
        )

        // Mensaje de error visible en ROJO
        if (error != null) {
            Text(
                text = error,
                color = Color(0xFFF44336), // Rojo Material Design visible
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}
