package com.example.seguimiento.Loading

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
 import androidx.compose.foundation.verticalScroll
 import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seguimiento.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. Definición de colores
val BrandOrange = Color(0xFFE67E00)
val TextSecondary = Color(0xFFFFFFFF).copy(alpha = 0.85f)
val TextPrimary = Color(0xFFFFFFFF)

@Composable
fun LoadingScreen(
    onFinished: () -> Unit,
    viewModel: LoadingViewModel = viewModel()
) {
    val progress by viewModel.progress.collectAsState()
    val isFinished by viewModel.isFinished.collectAsState()

    LaunchedEffect(isFinished) {
        if (isFinished) onFinished()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.cargando),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.08f))

            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.petadopticono),
                contentDescription = "PetAdopta Logo",
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(320.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.weight(0.02f))

            // AQUÍ SE LLAMA A LA FUNCIÓN
            PawProgressBar(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(24.dp)
            )

            Text(
                text = "Buscando nuevos amigos...",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp)
            )

            Text(
                text = "Estamos preparando todo para ti.",
                color = TextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.weight(0.8f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {

// 6. TEXTO DE VERSIÓN Y COPYRIGHT

                Column(

                    horizontalAlignment = Alignment.CenterHorizontally,

                    modifier = Modifier.padding(bottom = 16.dp)

                ) {

                    Text(

                        text = "PetAdopta App v1.2",

                        color = TextPrimary.copy(alpha = 0.6f),

                        fontSize = 12.sp,

                        fontWeight = FontWeight.Medium

                    )

                    Text(

                        text = "© Copyright PetAdopta apy Inc.",

                        color = TextPrimary.copy(alpha = 0.5f),

                        fontSize = 10.sp,

                        fontWeight = FontWeight.Normal

                    )

                }


            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// 2. FUNCIÓN COMPOSABLE (Debe estar fuera de la clase ViewModel)
@Composable
fun PawProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(BrandOrange, RoundedCornerShape(14.dp))
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(progress.coerceAtLeast(0.01f)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(end = 4.dp)
                )
            }
        }
    }
}

// 3. VIEWMODEL
