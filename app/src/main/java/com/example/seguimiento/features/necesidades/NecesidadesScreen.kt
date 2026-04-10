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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.seguimiento.R
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
                    stringResource(R.string.needs_calc_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = cafeApp
                )
                Text(
                    stringResource(R.string.needs_calc_subtitle),
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = peso,
                    onValueChange = { viewModel.onPesoChanged(it) },
                    label = { Text(stringResource(R.string.needs_label_weight)) },
                    leadingIcon = { Icon(Icons.Default.Scale, null, tint = naranjaApp) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(stringResource(R.string.needs_label_activity), fontWeight = FontWeight.Bold, color = cafeApp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val actividades = listOf(
                        stringResource(R.string.needs_activity_low) to "Baja",
                        stringResource(R.string.needs_activity_moderate) to "Moderada",
                        stringResource(R.string.needs_activity_high) to "Alta"
                    )
                    actividades.forEach { (label, valor) ->
                        FilterChip(
                            selected = actividad == valor,
                            onClick = { viewModel.onActividadChanged(valor) },
                            label = { Text(label) },
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
                        Text(stringResource(R.string.needs_result_title), fontWeight = FontWeight.Bold, color = cafeApp)
                        Text(
                            stringResource(R.string.needs_result_value, calorias),
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
