package com.example.seguimiento.features.login

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: (isAdmin: Boolean) -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {}
) {
    val loginState by viewModel.loginState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginResult.Success -> {
                onLoginSuccess(state.isAdmin)
                viewModel.resetLoginState()
            }
            is LoginResult.Error -> {
                val errorMessage = context.getString(state.messageResId)
                val prefix = context.getString(com.example.seguimiento.R.string.login_error_prefix, errorMessage)
                scope.launch {
                    snackbarHostState.showSnackbar(prefix)
                }
                viewModel.resetLoginState()
            }
            null -> {}
        }
    }

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
            if (imageResourceId != 0) {
                Image(
                    painter = painterResource(id = imageResourceId),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }

            // Selector de Idioma con Banderas
            LanguageSelector(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                context = context
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(310.dp))

                CustomInputField(
                    value = viewModel.email.value,
                    onValueChange = { viewModel.email.onChange(it) },
                    placeholder = stringResource(id = com.example.seguimiento.R.string.login_user_placeholder),
                    icon = Icons.Default.Person,
                    errorResId = viewModel.email.errorResId
                )

                Spacer(modifier = Modifier.height(12.dp))

                CustomInputField(
                    value = viewModel.password.value,
                    onValueChange = { viewModel.password.onChange(it) },
                    placeholder = stringResource(id = com.example.seguimiento.R.string.login_password_placeholder),
                    icon = Icons.Default.Lock,
                    isPassword = true,
                    errorResId = viewModel.password.errorResId
                )

                Text(
                    text = stringResource(id = com.example.seguimiento.R.string.login_forgot_password),
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

                Button(
                    onClick = {
                        if (viewModel.isFormValid) {
                            viewModel.login()
                        } else {
                            val msg = context.getString(com.example.seguimiento.R.string.login_form_invalid)
                            scope.launch {
                                snackbarHostState.showSnackbar(msg)
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
                    Text(text = stringResource(id = com.example.seguimiento.R.string.login_button), fontSize = 18.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.padding(bottom = 30.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = com.example.seguimiento.R.string.login_no_account), color = Color.White, fontSize = 14.sp)
                    Text(
                        text = stringResource(id = com.example.seguimiento.R.string.login_register_link),
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

@Composable
fun LanguageSelector(modifier: Modifier = Modifier, context: Context) {
    var expanded by remember { mutableStateOf(false) }
    val currentLocale = context.resources.configuration.locales[0]
    
    // Texto y Bandera según idioma actual
    val (label, flagEmoji) = if (currentLocale.language == "en") {
        "English" to "🇺🇸"
    } else {
        "Español" to "🇪🇸"
    }

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier.clickable { expanded = true },
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.9f),
            shadowElevation = 6.dp,
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = flagEmoji, fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text(text = label, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            DropdownMenuItem(
                leadingIcon = { Text("🇪🇸", fontSize = 18.sp) },
                text = { Text("Español") },
                onClick = {
                    expanded = false
                    updateLocale(context, "es")
                }
            )
            DropdownMenuItem(
                leadingIcon = { Text("🇺🇸", fontSize = 18.sp) },
                text = { Text("English") },
                onClick = {
                    expanded = false
                    updateLocale(context, "en")
                }
            )
        }
    }
}

private fun updateLocale(context: Context, lang: String) {
    val sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    sharedPref.edit().putString("language", lang).apply()

    val locale = Locale(lang)
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    if (context is Activity) {
        context.recreate()
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
    errorResId: Int? = null
) {
    val error = errorResId?.let { stringResource(id = it) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
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
                errorContainerColor = Color.White,
                focusedIndicatorColor = if (error != null) Color.Red else Color.Transparent,
                unfocusedIndicatorColor = if (error != null) Color.Red else Color.Transparent,
                cursorColor = Color.Black,
                errorIndicatorColor = Color.Red
            ),
            isError = error != null,
            singleLine = true
        )

        if (error != null) {
            Text(
                text = error,
                color = Color(0xFFF44336),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}
