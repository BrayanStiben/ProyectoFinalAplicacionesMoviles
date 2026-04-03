package com.example.seguimiento.features.necesidades

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SeccionNecesidades(
    viewModel: NecesidadesViewModel = hiltViewModel()
) {
    val peso by viewModel.peso.collectAsState()
    val actividad by viewModel.actividad.collectAsState()
    val calorias by viewModel.resultadoCalorias.collectAsState()

    val naranjaApp = Color(0xFFE67E22)
    val cafeApp = Color(0xFF5D2E17)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Calculadora de Calorías 🐾",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = cafeApp
                )
                Text(
                    "Estima cuánto debe comer tu mascota diariamente.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = peso,
                    onValueChange = { viewModel.onPesoChanged(it) },
                    label = { Text("Peso de la mascota (kg)") },
                    leadingIcon = { Icon(Icons.Default.Scale, null, tint = naranjaApp) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Nivel de Actividad Física", fontWeight = FontWeight.Bold, color = cafeApp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Baja", "Moderada", "Alta").forEach { nivel ->
                        FilterChip(
                            selected = actividad == nivel,
                            onClick = { viewModel.onActividadChanged(nivel) },
                            label = { Text(nivel) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = naranjaApp,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        if (calorias > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = naranjaApp.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, null, tint = naranjaApp, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Resultado Estimado", fontWeight = FontWeight.Bold, color = cafeApp)
                        Text(
                            "$calorias kcal / día",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = naranjaApp
                        )
                    }
                }
            }
        }
    }
}
