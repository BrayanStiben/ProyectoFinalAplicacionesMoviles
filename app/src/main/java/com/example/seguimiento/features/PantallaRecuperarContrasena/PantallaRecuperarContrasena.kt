package com.example.seguimiento.features.PantallaRecuperarContrasena

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.res.painterResource

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seguimiento.R
import kotlinx.coroutines.launch

import android.util.Patterns
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRecuperarContrasena(
    viewModel: RecuperarContrasenaViewModel = viewModel()
) {
    val email by viewModel.email.collectAsState()
    val esValido by viewModel.esEmailValido.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color(0xFF4CB5E6) // Fondo azul vibrante
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
                // LOGO - Grande y centrado
                Image(
                    painter = painterResource(id = R.drawable.petadopticono),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(350.dp) // Tamaño prominente
                        .padding(top = 20.dp)
                )

                // TARJETA DE FORMULARIO - Elevada con offset negativo
                Card(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .offset(y = (-80).dp), // AQUÍ SE SUBE LA CAJA
                    shape = RoundedCornerShape(40.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(15.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 25.dp, vertical = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Icono de Candado
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
                            text = "Por favor ingrese su correo electrónico para recibir un código de verificación.",
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 15.dp)
                        )

                        // Input Email con fondo condicional
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

                        // Botón de Enviar
                        Button(
                            onClick = {
                                scope.launch {
                                    if (viewModel.ejecutarValidacionFinal()) {
                                        snackbarHostState.showSnackbar("Enviando código a $email")
                                    } else {
                                        snackbarHostState.showSnackbar("Error: Correo inválido")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE69160))
                        ) {
                            Text("Enviar Código", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Espacio extra al fondo para que el scroll respire
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}