package com.example.seguimiento.features.register

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seguimiento.R
import com.example.seguimiento.core.utils.CampoValidado
import com.example.seguimiento.core.utils.ResultadoPeticion
import kotlinx.coroutines.launch

// Colores oficiales
val NaranjaApp = Color(0xFFE67E22)
val CafeApp = Color(0xFF5D2E17)

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateToFinalize: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val resultadoRegistro by viewModel.resultadoRegistro.collectAsState()

    // Observar el resultado del registro
    LaunchedEffect(resultadoRegistro) {
        when (val resultado = resultadoRegistro) {
            is ResultadoPeticion.Exito -> {
                scope.launch {
                    snackbarHostState.showSnackbar(resultado.mensaje)
                    onNavigateToFinalize()
                }
            }
            is ResultadoPeticion.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(resultado.mensaje)
                }
            }
            null -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. FONDO
        Image(
            painter = painterResource(id = R.drawable.fondo2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // LOGO PETADOPTA
            Image(
                painter = painterResource(id = R.drawable.petadopticono),
                contentDescription = "Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(320.dp)
                    .padding(top = 10.dp)
            )

            // CONTENEDOR FORMULARIO
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-110).dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Únete a la Familia",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = CafeApp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CampoTextoStyle("Nombre completo", viewModel.nombre, Icons.Default.Person)
                    CampoTextoStyle("Ciudad", viewModel.ciudad, Icons.Default.LocationCity)
                    CampoTextoStyle("Dirección", viewModel.direccion, Icons.Default.Home)
                    CampoTextoStyle("Correo electrónico", viewModel.correo, Icons.Default.Email)
                    CampoTextoStyle("Contraseña", viewModel.contrasena, Icons.Default.Lock, true)
                    CampoTextoStyle("Confirmar contraseña", viewModel.confirmarContrasena, Icons.Default.LockReset, true)

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.registrar()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NaranjaApp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("REGISTRARME", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
            
            TextButton(
                onClick = { onNavigateToLogin() }, 
                modifier = Modifier.offset(y = (-100).dp)
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión", color = Color(0xFFD37506), fontWeight = FontWeight.ExtraBold)
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun CampoTextoStyle(
    etiqueta: String,
    campo: CampoValidado<String>,
    icono: ImageVector,
    esContrasena: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = campo.value,
            onValueChange = { campo.onChange(it) },
            label = { Text(etiqueta) },
            leadingIcon = { Icon(icono, null, tint = NaranjaApp) },
            visualTransformation = if (esContrasena) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            isError = campo.error != null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NaranjaApp,
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = NaranjaApp,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        campo.error?.let { errorText ->
            Text(
                text = errorText,
                color = Color.Red,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 12.dp, top = 2.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}
