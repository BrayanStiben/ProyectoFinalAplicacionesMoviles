package com.example.seguimiento.features.FormularioDeAdopction.StepFourScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seguimiento.features.FormularioDeAdopction.AdoptionViewModel

@Composable
fun StepFourScreen(
    vm: AdoptionViewModel = viewModel()
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Paso 4: Documentación", 
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = vm.state.referenceName,
            onValueChange = { vm.updateState(vm.state.copy(referenceName = it)) },
            label = { Text("Referencia Personal") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = vm.state.referencePhone,
            onValueChange = { vm.updateState(vm.state.copy(referencePhone = it)) },
            label = { Text("Teléfono de Referencia") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { vm.submitForm() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("ENVIAR SOLICITUD", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}