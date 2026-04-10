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
import androidx.compose.ui.res.stringResource
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
            contentDescription = stringResource(id = R.string.about_logo_description),
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            stringResource(id = R.string.about_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = cafeApp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            stringResource(id = R.string.about_mission),
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
                Text(stringResource(id = R.string.about_importance_title), fontWeight = FontWeight.Bold, color = cafeApp, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                BulletPoint(stringResource(id = R.string.about_benefit_1))
                BulletPoint(stringResource(id = R.string.about_benefit_2))
                BulletPoint(stringResource(id = R.string.about_benefit_3))
                BulletPoint(stringResource(id = R.string.about_benefit_4))
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
