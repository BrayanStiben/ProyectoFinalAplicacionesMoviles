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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seguimiento.features.FormularioDeAdopction.AdoptionViewModel

// Colores del proyecto
val NaranjaApp = Color(0xFFE67E22)
val CafeApp = Color(0xFF5D2E17)
val AzulForm = Color(0xFF42A5F5)
val FondoApp = Color(0xFFFDF7E7)
val VerdeFinal = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepFourScreen(
    vm: AdoptionViewModel = viewModel(),
    onFinish: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showTermsAndOptionsPopUp by remember { mutableStateOf(false) }
    var showSignaturePad by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Finalizar Solicitud", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AzulForm)
            )
        },
        bottomBar = {
            BottomNavLocal(selectedTab) { selectedTab = it }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(FondoApp),
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
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(32.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        
                        Text("Formalización Final", fontSize = 22.sp, fontWeight = FontWeight.Black, color = CafeApp)

                        // SECCIÓN 1: CARGA DE ARCHIVOS (CORREGIDO)
                        SectionHeaderLocal(Icons.Default.CloudUpload, "Documentación")
                        FilaCargaArchivoLocal("Foto DNI / Cédula", vm.state.idPhotoUri != null) {
                            launcherDni.launch("image/*")
                        }
                        FilaCargaArchivoLocal("Fotos del hogar/patio", vm.state.yardPhotoUri != null) {
                            launcherPatio.launch("image/*")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // SECCIÓN 2: REFERENCIAS
                        SectionHeaderLocal(Icons.Default.Groups, "Referencias")
                        CustomInputLocalFour("Nombre de Referencia", vm.state.referenceName) { 
                            vm.updateState(vm.state.copy(referenceName = it)) 
                        }
                        CustomInputLocalFour("Teléfono", vm.state.referencePhone) { 
                            vm.updateState(vm.state.copy(referencePhone = it)) 
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // SECCIÓN 3: FIRMA
                        SectionHeaderLocal(Icons.Default.Draw, "Firma Digital")
                        Button(
                            onClick = { showTermsAndOptionsPopUp = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (vm.state.termsAccepted || vm.state.signatureBitmap != null) VerdeFinal.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            val textoFirma = if (vm.state.signatureBitmap != null || vm.state.termsAccepted) "FIRMA/ACUERDO LISTO ✓" else "FIRMAR O ACEPTAR"
                            Text(textoFirma, color = if(vm.state.signatureBitmap != null || vm.state.termsAccepted) VerdeFinal else CafeApp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // INDICADOR DE AVANCE
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(4) { index ->
                                Surface(
                                    modifier = Modifier.size(30.dp).padding(4.dp),
                                    shape = CircleShape,
                                    color = NaranjaApp 
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("${index + 1}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                if (index < 3) {
                                    Box(modifier = Modifier.width(15.dp).height(2.dp).background(NaranjaApp))
                                }
                            }
                        }

                        // BOTÓN FINALIZAR
                        Button(
                            onClick = { vm.submitForm(); onFinish() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VerdeFinal),
                            shape = RoundedCornerShape(16.dp),
                            enabled = vm.state.termsAccepted || vm.state.signatureBitmap != null
                        ) {
                            Text("FINALIZAR", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 1.sp)
                        }
                    }
                }
            }
        }

        // POP-UPS Y DIÁLOGOS
        if (showTermsAndOptionsPopUp) {
            AlertDialog(
                onDismissRequest = { showTermsAndOptionsPopUp = false },
                title = { Text("Términos y Condiciones", fontWeight = FontWeight.Black, color = CafeApp) },
                text = { 
                    Text("Al proceder, usted confirma que toda la información proporcionada es verídica y se compromete al cuidado responsable de la mascota. Prohibida su venta, subasta o maltrato.")
                },
                confirmButton = {
                    Button(
                        onClick = { 
                            vm.updateState(vm.state.copy(termsAccepted = true))
                            showTermsAndOptionsPopUp = false 
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeFinal)
                    ) { Text("ACEPTO TÉRMINOS") }
                },
                dismissButton = {
                    Button(
                        onClick = { 
                            showSignaturePad = true
                            showTermsAndOptionsPopUp = false 
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NaranjaApp)
                    ) { Text("FIRMA TÁCTIL") }
                }
            )
        }

        if (showSignaturePad) {
            Dialog(onDismissRequest = { showSignaturePad = false }) {
                Card(
                    modifier = Modifier.fillMaxWidth().height(450.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Dibuje su firma aquí", fontWeight = FontWeight.Bold, color = CafeApp)
                        
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
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { paths.clear() }, modifier = Modifier.weight(1f)) {
                                Text("LIMPIAR", color = Color.Red)
                            }
                            Button(
                                onClick = { 
                                    vm.updateState(vm.state.copy(signatureBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)))
                                    showSignaturePad = false 
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = VerdeFinal)
                            ) { Text("GUARDAR") }
                        }
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
            text = if (subido) "Cambiar" else "Subir",
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
    NavigationBar(containerColor = Color.White) {
        val items = listOf(Triple("Inicio", Icons.Default.Home, 0), Triple("Buscar", Icons.Default.Search, 1), Triple("Favs", Icons.Default.FavoriteBorder, 2), Triple("Perfil", Icons.Default.Person, 3))
        items.forEach { (label, icon, index) ->
            NavigationBarItem(
                icon = { Icon(icon, null) }, label = { Text(label) }, selected = selectedItem == index,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = NaranjaApp, selectedTextColor = NaranjaApp, indicatorColor = Color(0xFFFFF4C2))
            )
        }
    }
}