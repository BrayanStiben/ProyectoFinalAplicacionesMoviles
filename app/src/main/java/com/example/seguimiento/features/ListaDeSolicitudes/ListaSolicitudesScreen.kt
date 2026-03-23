
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CrueltyFree
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.seguimiento.features.ListaDeSolicitudes.AdopcionViewModel
import com.example.seguimiento.features.ListaDeSolicitudes.DatosMascota
import androidx.compose.ui.res.painterResource // Para leer el drawable
import androidx.compose.foundation.Image
import com.example.seguimiento.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seguimiento.features.EnvioDeCodigo.EnvioDeCodigoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvioDeCodigoScreen(viewModel: EnvioDeCodigoViewModel = viewModel()) {
    val focusManager = LocalFocusManager.current
    val focusRequesters = remember { List(4) { FocusRequester() } }
    val scrollState = rememberScrollState()

    // --- VENTANA EMERGENTE (Dialogo Jocoso) ---
    if (viewModel.mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { viewModel.mostrarDialogo = false },
            confirmButton = {
                TextButton(onClick = { viewModel.mostrarDialogo = false }) {
                    Text("OK", color = Color(0xFFF37021), fontWeight = FontWeight.Bold)
                }
            },
            title = { Text("¡Sorpresa!", fontWeight = FontWeight.Bold) },
            text = { Text("Hola con Dorian 🐶✨", fontSize = 18.sp) },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color(0xFFFDF5E6)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. FONDO
        Image(
            painter = painterResource(id = R.drawable.fondosolicitud),
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
            Spacer(modifier = Modifier.height(20.dp))

            // 2. LOGO GIGANTE
            Image(
                painter = painterResource(id = R.drawable.petadopticono),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                contentScale = ContentScale.Fit
            )

            // 3. TARJETA (CASA) PEGADA
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF5E6).copy(alpha = 0.98f)),
                elevation = CardDefaults.cardElevation(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-85).dp) // Sube para pegarse al logo
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Paso 2: Verificación de Identidad",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF333333)
                    )

                    Text(
                        text = "Ingresa el código de 4 dígitos enviado a su correo electrónico.",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
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
                                    if (valor.length <= 1 && (valor.isEmpty() || valor.all { it.isDigit() })) {
                                        viewModel.onDigitChange(index, valor)
                                        if (valor.isNotEmpty() && index < 3) {
                                            focusRequesters[index + 1].requestFocus()
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
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
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
                    .padding(bottom = 30.dp)
                    .clickable { /* Acción Volver */ }
            )
        }
    }
}