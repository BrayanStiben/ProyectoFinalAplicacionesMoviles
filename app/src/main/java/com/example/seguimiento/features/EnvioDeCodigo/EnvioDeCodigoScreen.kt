package com.example.seguimiento.features.EnvioDeCodigo

import com.example.seguimiento.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvioDeCodigoScreen(
    viewModel: EnvioDeCodigoViewModel = hiltViewModel(),
    onCodeVerified: () -> Unit = {},
    onBackToLogin: () -> Unit = {},
    onNavigateToHome: () -> Unit = {} // Nuevo parámetro para ir al Home
) {
    val focusManager = LocalFocusManager.current
    val focusRequesters = remember { List(4) { FocusRequester() } }
    val scrollState = rememberScrollState()

    // --- DIÁLOGO MÁGICO "HOLA CON DORIAN" ---
    if (viewModel.mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { viewModel.mostrarDialogo = false },
            confirmButton = {
                Button(
                    onClick = { 
                        viewModel.mostrarDialogo = false
                        // Ahora al pulsar "¡Guau!" va al Home
                        onNavigateToHome()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF37021))
                ) {
                    Text("¡Guau!", color = Color.White)
                }
            },
            title = { Text("Mensaje Especial", fontWeight = FontWeight.Bold) },
            text = { Text("hola con dorian", fontSize = 20.sp, fontWeight = FontWeight.Medium) },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color(0xFFFDF5E6)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. FONDO
        Image(
            painter = painterResource(id = R.drawable.fondo2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 2. LOGO GIGANTE
            Image(
                painter = painterResource(id = R.drawable.petadopticono),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                contentScale = ContentScale.Fit
            )

            // 3. TARJETA
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF5E6).copy(alpha = 0.98f)),
                elevation = CardDefaults.cardElevation(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-100).dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Paso 2: Verificación de Identidad",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF333333),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Ingresa el código de 4 dígitos enviado a su correo electrónico.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 12.dp),
                        lineHeight = 20.sp
                    )

                    // Bloque de Inputs
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        viewModel.codigo.forEachIndexed { index, digit ->
                            OutlinedTextField(
                                value = digit,
                                onValueChange = { valor ->
                                    if (valor.length <= 1) {
                                        viewModel.onDigitChange(index, valor)
                                        if (valor.isNotEmpty() && index < 3) {
                                            focusRequesters[index + 1].requestFocus()
                                        } else if (valor.isNotEmpty() && index == 3) {
                                            focusManager.clearFocus()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .width(62.dp)
                                    .height(75.dp)
                                    .focusRequester(focusRequesters[index])
                                    .onKeyEvent { event ->
                                        if (event.type == KeyEventType.KeyDown && event.key == Key.Backspace) {
                                            if (digit.isEmpty() && index > 0) {
                                                focusRequesters[index - 1].requestFocus()
                                                true
                                            } else false
                                        } else false
                                    },
                                textStyle = LocalTextStyle.current.copy(
                                    textAlign = TextAlign.Center,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFFFF4C2),
                                    unfocusedContainerColor = Color(0xFFFFF4C2),
                                    focusedBorderColor = Color(0xFFF37021),
                                    unfocusedBorderColor = Color.LightGray
                                )
                            )
                        }
                    }

                    Text(
                        text = buildAnnotatedString {
                            append("¿No recibiste el código? ")
                            withStyle(style = SpanStyle(color = Color(0xFFF37021), fontWeight = FontWeight.Bold)) {
                                append("Reenviar en ${viewModel.segundosRestantes}s")
                            }
                        },
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // EL BOTÓN DE CONFIRMACIÓN
                    Button(
                        onClick = { viewModel.confirmarIdentidad() },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF37021)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Confirmar Identidad", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // 4. VOLVER AL INICIO
            Text(
                text = buildAnnotatedString {
                    append("¿Volver al ")
                    withStyle(style = SpanStyle(color = Color(0xFFF37021), fontWeight = FontWeight.Bold)) {
                        append("inicio de sesión")
                    }
                    append("?")
                },
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .clickable { onBackToLogin() }
            )
        }
    }
}
