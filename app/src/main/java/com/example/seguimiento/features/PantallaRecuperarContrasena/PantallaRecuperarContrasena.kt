package com.example.seguimiento.features.PantallaRecuperarContrasena

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seguimiento.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRecuperarContrasena(
    email: String = "",
    viewModel: RecuperarContrasenaViewModel = hiltViewModel(),
    onPasswordUpdated: () -> Unit = {}
) {
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val esExitoso by viewModel.esExitoso.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.setEmail(email)
    }

    if (esExitoso) {
        AlertDialog(
            onDismissRequest = { onPasswordUpdated() },
            confirmButton = {
                Button(onClick = { onPasswordUpdated() }) {
                    Text(stringResource(id = R.string.reset_password_btn_go_to_login))
                }
            },
            title = { Text(stringResource(id = R.string.reset_password_success_title)) },
            text = { Text(stringResource(id = R.string.reset_password_success_text)) },
            shape = RoundedCornerShape(20.dp)
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.petadopticono),
                    contentDescription = null,
                    modifier = Modifier.size(300.dp).padding(top = 20.dp)
                )

                Card(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .offset(y = (-50).dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(25.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.reset_password_title),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )

                        Text(
                            text = stringResource(id = R.string.reset_password_instruction, email),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { viewModel.onPasswordChanged(it) },
                            label = { Text(stringResource(id = R.string.reset_password_label_new)) },
                            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFFE69160),
                                unfocusedBorderColor = Color.LightGray
                            )
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                            label = { Text(stringResource(id = R.string.reset_password_label_confirm)) },
                            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFFE69160),
                                unfocusedBorderColor = Color.LightGray
                            )
                        )

                        Spacer(modifier = Modifier.height(25.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    if (viewModel.actualizarContrasena()) {
                                        // El dialogo se encarga
                                    } else {
                                        val msg = context.getString(R.string.reset_password_error_mismatch)
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE69160))
                        ) {
                            Text(stringResource(id = R.string.reset_password_btn_save), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
