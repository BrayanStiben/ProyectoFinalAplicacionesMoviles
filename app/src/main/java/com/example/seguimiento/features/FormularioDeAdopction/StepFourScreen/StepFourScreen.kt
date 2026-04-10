package com.example.seguimiento.features.FormularioDeAdopction.StepFourScreen

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seguimiento.R
import com.example.seguimiento.features.FormularioDeAdopction.AdoptionViewModel

// Colores del proyecto
val NaranjaApp = Color(0xFFE67E22)
val CafeApp = Color(0xFF5D2E17)
val AzulForm = Color(0xFF42A5F5)
val VerdeFinal = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepFourScreen(
    vm: AdoptionViewModel = hiltViewModel(),
    onFinish: (String) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiltros: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showSignaturePad by remember { mutableStateOf(false) }

    // Lanzadores para selección de imágenes
    val launcherDni = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { vm.updateState(vm.state.copy(idPhotoUri = it.toString())) }
    }

    val launcherPatio = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { vm.updateState(vm.state.copy(yardPhotoUri = it.toString())) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(R.string.form_step4_title), color = Color.White, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AzulForm.copy(alpha = 0.9f))
                )
            },
            bottomBar = {
                BottomNavLocal(selectedItem = 0) { index ->
                    when(index) {
                        0 -> onNavigateToHome()
                        1 -> onNavigateToFiltros()
                        3 -> onNavigateToProfile()
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                        shape = RoundedCornerShape(32.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            
                            Text(stringResource(R.string.form_step4_header), fontSize = 22.sp, fontWeight = FontWeight.Black, color = CafeApp)

                            SectionHeaderLocal(Icons.Default.CloudUpload, stringResource(R.string.form_step4_documentation))
                            FilaCargaArchivoLocal(stringResource(R.string.form_step4_id_photo), vm.state.idPhotoUri != null) {
                                launcherDni.launch("image/*")
                            }
                            FilaCargaArchivoLocal(stringResource(R.string.form_step4_home_photo), vm.state.yardPhotoUri != null) {
                                launcherPatio.launch("image/*")
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            SectionHeaderLocal(Icons.Default.Groups, stringResource(R.string.form_step4_references))
                            CustomInputLocalFour(stringResource(R.string.form_step4_ref_name), vm.state.referenceName) { 
                                vm.updateState(vm.state.copy(referenceName = it)) 
                            }
                            CustomInputLocalFour(stringResource(R.string.form_step4_ref_phone), vm.state.referencePhone) { 
                                vm.updateState(vm.state.copy(referencePhone = it)) 
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            SectionHeaderLocal(Icons.Default.Gavel, stringResource(R.string.form_step4_terms_title))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = vm.state.termsAccepted,
                                    onCheckedChange = { vm.updateState(vm.state.copy(termsAccepted = it)) },
                                    colors = CheckboxDefaults.colors(checkedColor = NaranjaApp)
                                )
                                Text(
                                    stringResource(R.string.form_step4_terms_accept),
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // BOTÓN DE FIRMA DIGITAL DENTRO DE LA SECCIÓN DE TÉRMINOS
                            Button(
                                onClick = { showSignaturePad = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (vm.state.signatureBitmap != null) VerdeFinal.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                val textoFirma = if (vm.state.signatureBitmap != null) stringResource(R.string.form_step4_signature_done) else stringResource(R.string.form_step4_signature_pending)
                                Icon(
                                    Icons.Default.Draw, 
                                    contentDescription = null, 
                                    tint = if(vm.state.signatureBitmap != null) VerdeFinal else CafeApp,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = textoFirma, 
                                    color = if(vm.state.signatureBitmap != null) VerdeFinal else CafeApp, 
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = { 
                                    vm.submitForm { id ->
                                        onFinish(id)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VerdeFinal),
                                shape = RoundedCornerShape(16.dp),
                                enabled = vm.state.termsAccepted && vm.state.idPhotoUri != null && vm.state.signatureBitmap != null
                            ) {
                                Text(stringResource(R.string.form_btn_send), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 1.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSignaturePad) {
        Dialog(onDismissRequest = { showSignaturePad = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().height(450.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.form_step4_sig_dialog_title), fontWeight = FontWeight.Bold, color = CafeApp)
                    
                    val paths = remember { mutableStateListOf<Path>() }
                    var currentPath by remember { mutableStateOf<Path?>(null) }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .border(2.dp, Color.LightGray, RoundedCornerShape(12.dp))
                            .background(Color(0xFFFAFAFA))
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        currentPath = Path().apply { moveTo(offset.x, offset.y) }
                                        paths.add(currentPath!!)
                                    },
                                    onDrag = { change, _ ->
                                        currentPath?.lineTo(change.position.x, change.position.y)
                                        if (paths.isNotEmpty()) {
                                            val last = paths.removeAt(paths.size - 1)
                                            paths.add(last)
                                        }
                                    }
                                )
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            paths.forEach { path ->
                                drawPath(path, Color.Black, style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                            }
                        }
                        if (paths.isEmpty()) {
                            Text(stringResource(R.string.form_step4_sig_dialog_hint), modifier = Modifier.align(Alignment.Center), color = Color.LightGray, fontStyle = FontStyle.Italic)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { paths.clear() }, modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.btn_delete), color = Color.Red)
                        }
                        Button(
                            onClick = { 
                                // Simulamos captura de firma para el estado
                                vm.updateState(vm.state.copy(signatureBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)))
                                showSignaturePad = false 
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = VerdeFinal)
                        ) { Text(stringResource(R.string.btn_save_simple)) }
                    }
                }
            }
        }
    }
}

@Composable
fun FilaCargaArchivoLocal(label: String, subido: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(NaranjaApp.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (subido) Icons.Default.CheckCircle else Icons.Default.FileUpload,
                contentDescription = null,
                tint = if (subido) VerdeFinal else NaranjaApp,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(label, modifier = Modifier.weight(1f), fontSize = 14.sp, color = CafeApp, fontWeight = FontWeight.Medium)
        
        Text(
            text = if (subido) stringResource(R.string.form_btn_change) else stringResource(R.string.form_btn_upload),
            color = NaranjaApp,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable { onClick() }
                .padding(4.dp)
        )
    }
}

@Composable
fun SectionHeaderLocal(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
        Icon(icon, null, tint = NaranjaApp, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = CafeApp)
    }
}

@Composable
fun CustomInputLocalFour(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NaranjaApp)
    )
}

@Composable
fun BottomNavLocal(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    NavigationBar(containerColor = Color.White.copy(alpha = 0.9f)) {
        val items = listOf(
            Triple(stringResource(R.string.nav_home), Icons.Default.Home, 0),
            Triple(stringResource(R.string.nav_search), Icons.Default.Search, 1),
            Triple(stringResource(R.string.nav_favorites), Icons.Default.FavoriteBorder, 2),
            Triple(stringResource(R.string.nav_profile), Icons.Default.Person, 3)
        )
        items.forEach { (label, icon, index) ->
            NavigationBarItem(
                icon = { Icon(icon, null) },
                label = { Text(label, fontSize = 10.sp) },
                selected = selectedItem == index,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NaranjaApp,
                    selectedTextColor = NaranjaApp,
                    indicatorColor = Color(0xFFFFF4C2)
                )
            )
        }
    }
}
