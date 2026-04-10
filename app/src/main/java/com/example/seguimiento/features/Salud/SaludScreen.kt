package com.example.seguimiento.features.Salud

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.example.seguimiento.R
import com.example.seguimiento.Dominio.modelos.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaludScreen(
    mascotaId: String,
    viewModel: SaludViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(mascotaId) {
        viewModel.setMascotaId(mascotaId)
    }

    val mascota by viewModel.mascota.collectAsState()
    val carnet by viewModel.carnet.collectAsState()
    val currentUser by viewModel.authRepository.currentUser.collectAsState()
    val adoptionReq by viewModel.adoptionRequest.collectAsState()
    val aliados by viewModel.aliadosAprobados.collectAsState()
    
    var tabSelected by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var isFlipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "cardFlip"
    )

    val naranjaApp = Color(0xFFE67E22)
    val navyBlue = Color(0xFF1A237E)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.health_title), fontWeight = FontWeight.Black, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = naranjaApp)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = naranjaApp,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFDFBFA))
        ) {
            // --- CARNET FÍSICO (FLIPPABLE) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(220.dp)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                    }
                    .clickable { isFlipped = !isFlipped }
            ) {
                if (rotation <= 90f) {
                    CarnetFrontUI(mascota, carnet, adoptionReq)
                } else {
                    CarnetBackUI(mascota, currentUser, carnet, adoptionReq)
                }
            }

            Text(
                text = stringResource(R.string.health_flip_hint),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            TabRow(
                selectedTabIndex = tabSelected,
                containerColor = Color.White,
                contentColor = naranjaApp,
                divider = {}
            ) {
                Tab(selected = tabSelected == 0, onClick = { tabSelected = 0 }, text = { Text(stringResource(R.string.health_tab_vaccines), fontSize = 12.sp) })
                Tab(selected = tabSelected == 1, onClick = { tabSelected = 1 }, text = { Text(stringResource(R.string.health_tab_deworming), fontSize = 12.sp) })
                Tab(selected = tabSelected == 2, onClick = { tabSelected = 2 }, text = { Text(stringResource(R.string.health_tab_appointments), fontSize = 12.sp) })
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when(tabSelected) {
                    0 -> ListaSalud(carnet.vacunas) { ItemVacuna(it) }
                    1 -> ListaSalud(carnet.desparasitaciones) { ItemDespar(it) }
                    2 -> ListaSalud(carnet.citas) { ItemCita(it) }
                }
            }
        }
    }

    if (showDialog) {
        DialogoRegistro(
            tipo = tabSelected,
            aliados = aliados,
            onDismiss = { showDialog = false },
            onConfirmVacuna = { n, f, p -> viewModel.registrarVacuna(n, f, p) },
            onConfirmDespar = { p, f -> viewModel.registrarDesparasitacion(p, f) },
            onConfirmCita = { m, f, h, c -> viewModel.agendarCita(m, f, h, c) }
        )
    }
}

@Composable
fun CarnetFrontUI(mascota: Mascota?, carnet: CarnetSalud, adoptionReq: AdoptionRequest?) {
    val navyBlue = Color(0xFF1A237E)
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.fondo2),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.05f
            )
            
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.health_carnet_header),
                        color = navyBlue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                    Text(
                        stringResource(R.string.health_carnet_subtitle),
                        color = navyBlue.copy(alpha = 0.7f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, navyBlue.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = mascota?.imagenUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(Modifier.width(20.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        CardDataRow(stringResource(R.string.health_label_name), mascota?.nombre?.uppercase() ?: "---")
                        CardDataRow(stringResource(R.string.health_label_breed), mascota?.raza?.uppercase() ?: "---")
                        CardDataRow(stringResource(R.string.health_label_birth), adoptionReq?.petAge?.uppercase() ?: "---")
                        CardDataRow(stringResource(R.string.health_label_sex), if(mascota?.tipo == "Perro") stringResource(R.string.health_sex_male) else stringResource(R.string.health_sex_female))
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = navyBlue
                ) {
                    Text(
                        stringResource(R.string.health_carnet_footer),
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                        color = Color.White,
                        fontSize = 7.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun CarnetBackUI(mascota: Mascota?, user: User?, carnet: CarnetSalud, adoptionReq: AdoptionRequest?) {
    val navyBlue = Color(0xFF1A237E)
    Card(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { rotationY = 180f },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    stringResource(R.string.health_carnet_subtitle),
                    color = navyBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                
                Spacer(Modifier.height(16.dp))
                
                Text(stringResource(R.string.health_label_responsible), color = navyBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(user?.name?.uppercase() ?: "---", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Black)
                Text(stringResource(R.string.health_label_phone, adoptionReq?.referencePhone ?: "321 XXX XX XX"), color = Color.Gray, fontSize = 12.sp)

                Spacer(Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.qr_datos_detalados),
                        contentDescription = "QR",
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(stringResource(R.string.health_label_pet_id, carnet.petId), fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text(stringResource(R.string.health_label_pin, carnet.pin), color = Color.Gray, fontSize = 10.sp)
                    }
                }

                Spacer(Modifier.weight(1f))

                Text(
                    stringResource(R.string.health_footer_categories),
                    color = navyBlue,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun CardDataRow(label: String, value: String) {
    Column {
        Text(label, color = Color(0xFF1A237E), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun <T> ListaSalud(items: List<T>, itemContent: @Composable (T) -> Unit) {
    if (items.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Inbox, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                Text(stringResource(R.string.health_empty_records), color = Color.Gray, fontWeight = FontWeight.Medium)
            }
        }
    } else {
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items) { itemContent(it) }
        }
    }
}

@Composable
fun ItemVacuna(v: Vacuna) {
    Card(
        shape = RoundedCornerShape(16.dp), 
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFFE3F2FD), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Vaccines, null, tint = Color(0xFF2196F3))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(v.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF5D2E17))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.health_applied_date, v.fecha), fontSize = 12.sp, color = Color.Gray)
                }
                Text(stringResource(R.string.health_next_dose, v.proximaDosis), fontSize = 12.sp, color = Color(0xFFE67E22), fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun ItemDespar(d: Desparasitacion) {
    Card(
        shape = RoundedCornerShape(16.dp), 
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFFE8F5E9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.BugReport, null, tint = Color(0xFF4CAF50))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(d.producto, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF5D2E17))
                Text(stringResource(R.string.health_date_label, d.fecha), fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ItemCita(c: CitaVeterinaria) {
    Card(
        shape = RoundedCornerShape(16.dp), 
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFFFFF3E0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Event, null, tint = Color(0xFFFF9800))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(c.motivo, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF5D2E17))
                Text("${c.fecha} • ${c.hora}", fontSize = 13.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
                Text(c.clinica, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun DialogoRegistro(
    tipo: Int,
    aliados: List<Refugio>,
    onDismiss: () -> Unit,
    onConfirmVacuna: (String, String, String) -> Unit,
    onConfirmDespar: (String, String) -> Unit,
    onConfirmCita: (String, String, String, String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val todayStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)

    var campo1 by remember { mutableStateOf("") }
    var campo2 by remember { mutableStateOf(if(tipo == 0) todayStr else "") }
    var campo3 by remember { mutableStateOf("") }
    var campo4 by remember { mutableStateOf("") }
    
    var expanded by remember { mutableStateOf(false) }

    val datePicker = DatePickerDialog(context, { _, y, m, d ->
        val c = Calendar.getInstance()
        c.set(y, m, d)
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(c.time)
        if (tipo == 1 || tipo == 2) campo2 = date else if (tipo == 0) campo3 = date
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

    val timePicker = TimePickerDialog(context, { _, h, min ->
        campo3 = String.format(Locale.getDefault(), "%02d:%02d", h, min)
    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                when(tipo) { 
                    0 -> stringResource(R.string.health_dialog_new_vaccine)
                    1 -> stringResource(R.string.health_dialog_new_deworming)
                    2 -> stringResource(R.string.health_dialog_new_appointment)
                    else -> "" 
                },
                fontWeight = FontWeight.Black,
                color = Color(0xFF5D2E17)
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when(tipo) {
                    0 -> {
                        OutlinedTextField(value = campo1, onValueChange = { campo1 = it }, label = { Text(stringResource(R.string.health_label_vaccine_name)) }, shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(value = campo2, onValueChange = {}, label = { Text(stringResource(R.string.health_label_today)) }, shape = RoundedCornerShape(12.dp), enabled = false, readOnly = true)
                        OutlinedTextField(
                            value = campo3, onValueChange = {}, label = { Text(stringResource(R.string.health_label_next_dose)) }, 
                            shape = RoundedCornerShape(12.dp), readOnly = true,
                            trailingIcon = { IconButton(onClick = { datePicker.show() }) { Icon(Icons.Default.CalendarMonth, null) } }
                        )
                    }
                    1 -> {
                        OutlinedTextField(value = campo1, onValueChange = { campo1 = it }, label = { Text(stringResource(R.string.health_label_product)) }, shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(
                            value = campo2, onValueChange = {}, label = { Text(stringResource(R.string.health_label_date)) }, 
                            shape = RoundedCornerShape(12.dp), readOnly = true,
                            trailingIcon = { IconButton(onClick = { datePicker.show() }) { Icon(Icons.Default.CalendarMonth, null) } }
                        )
                    }
                    2 -> {
                        OutlinedTextField(value = campo1, onValueChange = { campo1 = it }, label = { Text(stringResource(R.string.health_label_reason)) }, shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(
                            value = campo2, onValueChange = {}, label = { Text(stringResource(R.string.health_label_date)) }, 
                            shape = RoundedCornerShape(12.dp), readOnly = true,
                            trailingIcon = { IconButton(onClick = { datePicker.show() }) { Icon(Icons.Default.CalendarMonth, null) } }
                        )
                        OutlinedTextField(
                            value = campo3, onValueChange = {}, label = { Text(stringResource(R.string.health_label_hour)) }, 
                            shape = RoundedCornerShape(12.dp), readOnly = true,
                            trailingIcon = { IconButton(onClick = { timePicker.show() }) { Icon(Icons.Default.AccessTime, null) } }
                        )
                        
                        // Combo para Clínica
                        Box {
                            OutlinedTextField(
                                value = campo4, onValueChange = {}, label = { Text(stringResource(R.string.health_label_establishment)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp), readOnly = true,
                                trailingIcon = { IconButton(onClick = { expanded = true }) { Icon(Icons.Default.ArrowDropDown, null) } }
                            )
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                aliados.forEach { aliado ->
                                    DropdownMenuItem(
                                        text = { Text("${aliado.nombre} (${if(aliado.tipo == RefugioTipo.VETERINARIA) stringResource(R.string.health_vet_short) else stringResource(R.string.health_shelter_short)})") },
                                        onClick = {
                                            campo4 = aliado.nombre
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when(tipo) {
                        0 -> onConfirmVacuna(campo1, campo2, campo3)
                        1 -> onConfirmDespar(campo1, campo2)
                        2 -> onConfirmCita(campo1, campo2, campo3, campo4)
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)),
                shape = RoundedCornerShape(12.dp)
            ) { Text(stringResource(R.string.btn_save_simple)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_cancel), color = Color.Gray) }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}
