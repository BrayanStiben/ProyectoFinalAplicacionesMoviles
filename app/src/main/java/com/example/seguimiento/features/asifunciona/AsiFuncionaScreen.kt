package com.example.seguimiento.features.asifunciona

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SeccionAsiFunciona(
    viewModel: AsiFuncionaViewModel = hiltViewModel()
) {
    val procesos by viewModel.procesos.collectAsState()
    val cafeApp = Color(0xFF5D2E17)
    val naranjaApp = Color(0xFFE67E22)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Proceso Digestivo 🧬",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = cafeApp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                "Entiende cómo tu mascota transforma el alimento en energía.",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        items(procesos) { proceso ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = proceso.icon),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp).clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(proceso.titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = cafeApp)
                        Text(proceso.descripcion, fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
