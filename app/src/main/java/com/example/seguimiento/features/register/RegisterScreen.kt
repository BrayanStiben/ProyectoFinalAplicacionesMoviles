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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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

    val context = androidx.compose.ui.platform.LocalContext.current

    // Observar el resultado del registro
    LaunchedEffect(resultadoRegistro) {
        when (val resultado = resultadoRegistro) {
            is ResultadoPeticion.Exito -> {
                scope.launch {
                    snackbarHostState.showSnackbar(resultado.mensaje)
                    onNavigateToFinalize()
                }
            }
            is ResultadoPeticion.ExitoResId -> {
                val msg = context.getString(resultado.resId, *resultado.args.toTypedArray())
                scope.launch {
                    snackbarHostState.showSnackbar(msg)
                    onNavigateToFinalize()
                }
            }
            is ResultadoPeticion.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(resultado.mensaje)
                }
            }
            is ResultadoPeticion.ErrorResId -> {
                val msg = context.getString(resultado.resId, *resultado.args.toTypedArray())
                scope.launch {
                    snackbarHostState.showSnackbar(msg)
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
                        text = stringResource(id = com.example.seguimiento.R.string.register_title),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = CafeApp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CampoTextoStyle(stringResource(id = com.example.seguimiento.R.string.register_name_label), viewModel.nombre, Icons.Default.Person)
                    CampoTextoStyle(stringResource(id = com.example.seguimiento.R.string.register_email_label), viewModel.correo, Icons.Default.Email)
                    CampoTextoStyle(stringResource(id = com.example.seguimiento.R.string.register_password_label), viewModel.contrasena, Icons.Default.Lock, true)
                    CampoTextoStyle(stringResource(id = com.example.seguimiento.R.string.register_confirm_password_label), viewModel.confirmarContrasena, Icons.Default.LockReset, true)

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.registrar()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NaranjaApp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(stringResource(id = com.example.seguimiento.R.string.register_button), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
            
            TextButton(
                onClick = { onNavigateToLogin() }, 
                modifier = Modifier.offset(y = (-100).dp)
            ) {
                Text(
                    text = stringResource(id = com.example.seguimiento.R.string.register_already_have_account), 
                    color = Color.White, 
                    fontWeight = FontWeight.Black,
                    style = LocalTextStyle.current.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            blurRadius = 8f
                        )
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp) // Elevamos el snackbar para que sea visible
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
            isError = campo.errorResId != null,
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

        campo.errorResId?.let { errorResId ->
            Text(
                text = stringResource(id = errorResId),
                color = Color.Red,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 12.dp, top = 2.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}
