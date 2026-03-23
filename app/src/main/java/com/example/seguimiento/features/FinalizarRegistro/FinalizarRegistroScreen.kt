package com.example.seguimiento.features.FinalizarRegistro

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seguimiento.R

// Paleta de colores oficial
val NaranjaPrincipal = Color(0xFFE67E22)
val CafeApp = Color(0xFF5D2E17)
val FondoCrema = Color(0xFFFDF7E7)
val VerdeExito = Color(0xFF4CAF50)
val RojoDenuncia = Color(0xFFE53935)

@Composable
fun FinalizarRegistroScreen() {
    val vm: FinalizarRegistroViewModel = viewModel()
    val deptos by vm.departamentos.collectAsState()
    val municipios by vm.municipiosFiltrados.collectAsState()
    val deptoSel by vm.deptoSeleccionado.collectAsState()
    val muniSel by vm.municipioSeleccionado.collectAsState()
    val aceptado by vm.terminosAceptados.collectAsState()
    val estaCargando by vm.estaCargando.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // IMAGEN DE FONDO
        Image(
            painter = painterResource(id = R.drawable.fondo2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay oscuro para contraste
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            
            Text(
                "¡Casi listo!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                "Finaliza tu perfil para empezar",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // CONTENEDOR ENCERRADO
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    
                    if (estaCargando) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                            color = NaranjaPrincipal,
                            trackColor = NaranjaPrincipal.copy(alpha = 0.1f)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    ItemEstadoCard("Estado del perfil", "Completado", Icons.Default.VerifiedUser)
                    ItemEstadoCard("Foto de perfil", "Subida con éxito", Icons.Default.PhotoCamera)

                    Spacer(modifier = Modifier.height(16.dp))

                    SelectorModerno("Departamento", deptoSel, deptos, Icons.Default.Map) { vm.onDepartamentoChanged(it) }
                    Spacer(modifier = Modifier.height(12.dp))
                    SelectorModerno("Municipio / Ciudad", muniSel, municipios, Icons.Default.LocationOn, deptoSel.isNotEmpty()) { vm.onMunicipioChanged(it) }

                    Spacer(modifier = Modifier.height(20.dp))

                    CardTerminosModerno(aceptado = aceptado) { showDialog = true }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BotonIconoAux("Reportar", Icons.Default.Report, RojoDenuncia, Modifier.weight(1f)) {}
                        BotonIconoAux("Contrato", Icons.Default.FileDownload, Color(0xFF2980B9), Modifier.weight(1f)) {}
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = { /* Registro */ },
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CafeApp),
                        shape = RoundedCornerShape(18.dp),
                        enabled = muniSel.isNotEmpty() && aceptado && !estaCargando
                    ) {
                        Text("FINALIZAR REGISTRO", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showDialog) {
        VentanaTerminosDetallada(
            onDismiss = { showDialog = false },
            onConfirm = {
                vm.setTerminosAceptados(true)
                showDialog = false
            }
        )
    }
}

@Composable
fun ItemEstadoCard(titulo: String, subtitulo: String, icon: ImageVector) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        color = Color(0xFFF8F9FA),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).background(VerdeExito.copy(alpha = 0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = VerdeExito, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(titulo, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CafeApp)
                Text(subtitulo, fontSize = 11.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.CheckCircle, null, tint = VerdeExito, modifier = Modifier.size(18.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorModerno(label: String, seleccionado: String, opciones: List<String>, icon: ImageVector, enabled: Boolean = true, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded && enabled, onExpandedChange = { if (enabled) expanded = !expanded }) {
        OutlinedTextField(
            value = seleccionado, onValueChange = {}, readOnly = true, label = { Text(label) },
            leadingIcon = { Icon(icon, null, tint = if (enabled) NaranjaPrincipal else Color.Gray) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(14.dp), enabled = enabled,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = if (enabled) Color.White else Color(0xFFF2F2F2),
                focusedBorderColor = NaranjaPrincipal, unfocusedBorderColor = Color.LightGray
            )
        )
        if (opciones.isNotEmpty() && enabled) {
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Color.White).heightIn(max = 280.dp)) {
                opciones.forEach { o -> DropdownMenuItem(text = { Text(o) }, onClick = { onSelect(o); expanded = false }) }
            }
        }
    }
}

@Composable
fun CardTerminosModerno(aceptado: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (aceptado) VerdeExito.copy(alpha = 0.1f) else Color(0xFFF8F9FA)),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (aceptado) VerdeExito else Color.Transparent)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Description, null, tint = if (aceptado) VerdeExito else NaranjaPrincipal)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Normas y regulaciones", modifier = Modifier.weight(1f), color = if (aceptado) VerdeExito else Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Icon(if (aceptado) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.ArrowForwardIos, null, tint = if (aceptado) VerdeExito else Color.LightGray, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun BotonIconoAux(label: String, icon: ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = modifier.height(48.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, color.copy(alpha = 0.4f))) {
        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun VentanaTerminosDetallada(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    var checkInterno by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(vertical = 20.dp), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = FondoCrema)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Contrato Legal PetAdopta", fontWeight = FontWeight.Black, fontSize = 22.sp, color = CafeApp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = NaranjaPrincipal.copy(alpha = 0.2f))
                Box(modifier = Modifier.heightIn(max = 350.dp)) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text(text = "TÉRMINOS Y CONDICIONES GLOBALES\n\n1. Intermediación: PetAdopta conecta rescatistas con adoptantes.\n2. Venta prohibida: No se permite comercializar seres vivos.\n3. Capacidad Legal: Solo mayores de 18 años.\n\nPOLÍTICA DE PRIVACIDAD\n\nTus datos de ubicación y contacto se usan solo para facilitar la adopción.", fontSize = 14.sp, color = Color.DarkGray, lineHeight = 22.sp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Surface(onClick = { checkInterno = !checkInterno }, color = if(checkInterno) VerdeExito else Color.White, shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, if(checkInterno) VerdeExito else Color.LightGray)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                        Checkbox(
                            checked = checkInterno, onCheckedChange = { checkInterno = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color.White, checkmarkColor = VerdeExito, uncheckedColor = Color.White)
                        )
                        Text("Acepto los términos", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if(checkInterno) Color.White else CafeApp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onConfirm, enabled = checkInterno, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = NaranjaPrincipal), shape = RoundedCornerShape(15.dp)) {
                    Text("ACEPTAR Y CONTINUAR", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}