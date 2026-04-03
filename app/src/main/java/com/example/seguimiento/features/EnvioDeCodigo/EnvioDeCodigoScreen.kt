package com.example.seguimiento.features.EnvioDeCodigo

import com.example.seguimiento.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvioDeCodigoScreen(
    email: String = "",
    viewModel: EnvioDeCodigoViewModel = hiltViewModel(),
    onCodeVerified: (String) -> Unit = {},
    onBackToLogin: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val focusRequesters = remember { List(4) { FocusRequester() } }
    val scrollState = rememberScrollState()

    if (viewModel.mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { viewModel.mostrarDialogo = false },
            confirmButton = {
                Button(
                    onClick = { 
                        viewModel.mostrarDialogo = false
                        onCodeVerified(email)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF37021))
                ) {
                    Text("Continuar", color = Color.White)
                }
            },
            title = { Text("Código Correcto", fontWeight = FontWeight.Bold) },
            text = { Text("Identidad verificada con éxito. Ahora puedes cambiar tu contraseña.", fontSize = 16.sp) },
            shape = RoundedCornerShape(24.dp)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.petadopticono),
                contentDescription = "Logo",
                modifier = Modifier.fillMaxWidth().height(380.dp),
                contentScale = ContentScale.Fit
            )

            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.98f)),
                elevation = CardDefaults.cardElevation(12.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).offset(y = (-100).dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Verificación de Código",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF333333)
                    )

                    Text(
                        text = "Ingresa el código que te mostramos anteriormente para la cuenta $email",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

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
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .width(62.dp)
                                    .height(75.dp)
                                    .focusRequester(focusRequesters[index]),
                                textStyle = LocalTextStyle.current.copy(
                                    textAlign = TextAlign.Center,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                isError = viewModel.errorCodigo,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    errorContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFFF37021),
                                    unfocusedBorderColor = Color.LightGray
                                )
                            )
                        }
                    }

                    if (viewModel.errorCodigo) {
                        Text("Código incorrecto, intenta de nuevo", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.confirmarIdentidad() },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF37021)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Verificar Código", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Text(
                text = "Volver al inicio de sesión",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 40.dp).clickable { onBackToLogin() }
            )
        }
    }
}
