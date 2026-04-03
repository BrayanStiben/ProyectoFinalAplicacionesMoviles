package com.example.seguimiento.features.acercade

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seguimiento.R

@Composable
fun SeccionAcercaDe() {
    val cafeApp = Color(0xFF5D2E17)
    val naranjaApp = Color(0xFFE67E22)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.petadopticono),
            contentDescription = "PetAdopta Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "PetAdopta Nutri",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = cafeApp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Nuestra misión es educar a los dueños de mascotas sobre la importancia de una nutrición balanceada para prolongar la vida y felicidad de sus compañeros.",
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = Color.DarkGray,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = naranjaApp.copy(alpha = 0.05f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("¿Por qué es importante?", fontWeight = FontWeight.Bold, color = cafeApp, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                BulletPoint("Previene enfermedades crónicas.")
                BulletPoint("Mejora la calidad del pelaje y piel.")
                BulletPoint("Aumenta los niveles de energía.")
                BulletPoint("Fortalece el sistema inmune.")
            }
        }
    }
}

@Composable
fun BulletPoint(texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(Icons.Default.Pets, null, tint = Color(0xFFE67E22), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(texto, fontSize = 14.sp, color = Color.Gray)
    }
}
