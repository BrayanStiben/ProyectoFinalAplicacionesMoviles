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
import androidx.compose.material.icons.filled.Check
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
import com.example.seguimiento.R
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
        loginState?.let { state ->
            when (state) {
                is LoginResult.Success -> {
                    onLoginSuccess(state.isAdmin)
                    viewModel.resetLoginState()
                }
                is LoginResult.Error -> {
                    val errorMessage = context.resources.getString(state.messageResId)
                    val prefix = context.resources.getString(R.string.login_error_prefix, errorMessage)
                    snackbarHostState.showSnackbar(prefix)
                    viewModel.resetLoginState()
                }
            }
        }
    }

    val imageResourceId = remember(context) {
        val id = context.resources.getIdentifier("login", "drawable", context.packageName)
        if (id != 0) id else R.drawable.petadopticono
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = imageResourceId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            LanguageSelector(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp),
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
                    placeholder = stringResource(id = R.string.login_user_placeholder),
                    icon = Icons.Default.Person,
                    errorResId = viewModel.email.errorResId
                )

                Spacer(modifier = Modifier.height(12.dp))

                CustomInputField(
                    value = viewModel.password.value,
                    onValueChange = { viewModel.password.onChange(it) },
                    placeholder = stringResource(id = R.string.login_password_placeholder),
                    icon = Icons.Default.Lock,
                    isPassword = true,
                    errorResId = viewModel.password.errorResId
                )

                Text(
                    text = stringResource(id = R.string.login_forgot_password),
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
                            val msg = context.resources.getString(R.string.login_form_invalid)
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
                    Text(text = stringResource(id = R.string.login_button), fontSize = 18.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.padding(bottom = 30.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.login_no_account), color = Color.White, fontSize = 14.sp)
                    Text(
                        text = stringResource(id = R.string.login_register_link),
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
    val isEnglish = currentLocale.language == "en"

    Box(modifier = modifier) {
        Card(
            onClick = { expanded = true },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEnglish) "🇺🇸" else "🇪🇸",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isEnglish) "EN" else "ES",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF333333)
                    )
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color(0xFFD37506),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .width(160.dp)
        ) {
            DropdownMenuItem(
                text = { 
                    Text(
                        text = "Español", 
                        fontWeight = if (!isEnglish) FontWeight.Bold else FontWeight.Normal,
                        color = if (!isEnglish) Color(0xFFD37506) else Color.Black
                    ) 
                },
                leadingIcon = { Text("🇪🇸", fontSize = 22.sp) },
                trailingIcon = {
                    if (!isEnglish) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFFD37506),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                onClick = {
                    expanded = false
                    if (isEnglish) updateLocale(context, "es")
                }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp), color = Color.LightGray.copy(alpha = 0.5f))
            DropdownMenuItem(
                text = { 
                    Text(
                        text = "English", 
                        fontWeight = if (isEnglish) FontWeight.Bold else FontWeight.Normal,
                        color = if (isEnglish) Color(0xFFD37506) else Color.Black
                    ) 
                },
                leadingIcon = { Text("🇺🇸", fontSize = 22.sp) },
                trailingIcon = {
                    if (isEnglish) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFFD37506),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                onClick = {
                    expanded = false
                    if (!isEnglish) updateLocale(context, "en")
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
                    tint = if (error != null) Color.Red else Color(0xFFD37506)
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
