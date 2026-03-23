package com.example.seguimiento.features.FormularioDeAdopction.StepTwoScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seguimiento.features.FormularioDeAdopction.AdoptionViewModel

@Composable
fun StepTwoScreen(
    vm: AdoptionViewModel = viewModel(),
    onNext: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Cuestionario de Adopción", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        Text("Nombre", Modifier.padding(top = 8.dp))
        OutlinedTextField(
            value = vm.state.petName,
            onValueChange = { vm.updateState(vm.state.copy(petName = it)) },
            modifier = Modifier.fillMaxWidth()
        )

        Text("¿Tiene patio cercado?", Modifier.padding(top = 16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = vm.state.hasFencedYard, 
                onClick = { vm.updateState(vm.state.copy(hasFencedYard = true)) }
            )
            Text("Sí")
            Spacer(Modifier.width(8.dp))
            RadioButton(
                selected = !vm.state.hasFencedYard, 
                onClick = { vm.updateState(vm.state.copy(hasFencedYard = false)) }
            )
            Text("No")
        }

        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("Siguiente Paso", color = Color.White)
        }
    }
}