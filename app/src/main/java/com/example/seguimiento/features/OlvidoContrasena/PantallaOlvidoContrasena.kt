package com.example.seguimiento.features.OlvidoContrasena

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seguimiento.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OlvidoContrasenaScreen(
    viewModel: OlvidoContrasenaViewModel = hiltViewModel(),
    onNavigateToCode: (String) -> Unit = {}
) {
    val email by viewModel.email.collectAsState()
    val esValido by viewModel.esEmailValido.collectAsState()
    val codigoGenerado by viewModel.codigoGenerado.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Pop-up con el código generado
    if (codigoGenerado != null) {
        AlertDialog(
            onDismissRequest = { viewModel.limpiarCodigo() },
            confirmButton = {
                Button(
                    onClick = { 
                        val cod = codigoGenerado
                        viewModel.limpiarCodigo()
                        onNavigateToCode(email) 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE69160))
                ) {
                    Text("Entendido", color = Color.White)
                }
            },
            title = { Text("Código de Verificación", fontWeight = FontWeight.Bold) },
            text = { 
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Tu código para restablecer la contraseña es:", textAlign = TextAlign.Center)
                    Text(
                        text = codigoGenerado!!, 
                        fontSize = 32.sp, 
                        fontWeight = FontWeight.ExtraBold, 
                        color = Color(0xFFE69160),
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.petadopticono),
                        contentDescription = null,
                        modifier = Modifier.size(350.dp).padding(top = 20.dp)
                    )

                    Card(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                            .offset(y = (-80).dp),
                        shape = RoundedCornerShape(40.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                        elevation = CardDefaults.cardElevation(15.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 25.dp, vertical = 30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_lock_idle_lock),
                                contentDescription = null,
                                tint = if (esValido) Color(0xFFE6D2B5) else Color(0xFFD32F2F),
                                modifier = Modifier.size(90.dp)
                            )

                            Text(
                                text = "Recuperar Contraseña",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF333333)
                            )

                            Text(
                                text = "Ingresa tu correo y te mostraremos un código de seguridad.",
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 15.dp)
                            )

                            OutlinedTextField(
                                value = email,
                                onValueChange = { viewModel.onEmailChanged(it) },
                                isError = !esValido,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(65.dp)
                                    .background(
                                        if (esValido) Color(0xFFF7E9D7) else Color(0xFFFFEBEE),
                                        RoundedCornerShape(20.dp)
                                    ),
                                shape = RoundedCornerShape(20.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = if (esValido) Color.Transparent else Color.Red,
                                    unfocusedIndicatorColor = if (esValido) Color.Transparent else Color.Red,
                                    errorIndicatorColor = Color.Red
                                ),
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (esValido) Icons.Default.CheckCircle else Icons.Default.Error,
                                        contentDescription = null,
                                        tint = if (esValido) Color(0xFF4CAF50) else Color.Red,
                                        modifier = Modifier.size(30.dp)
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(25.dp))

                            Button(
                                onClick = {
                                    scope.launch {
                                        if (viewModel.ejecutarValidacionFinal()) {
                                            viewModel.generarCodigo()
                                        } else {
                                            snackbarHostState.showSnackbar("Error: Correo inválido")
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(60.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE69160))
                            ) {
                                Text("Obtener Código", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
