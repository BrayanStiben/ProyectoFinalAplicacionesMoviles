package com.example.seguimiento.features.FormularioDeAdopction.StepOneScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seguimiento.features.FormularioDeAdopction.AdoptionViewModel

@Composable
fun StepOneScreen(
    vm: AdoptionViewModel = viewModel(),
    onNext: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Paso 1: Interés y Motivación", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = vm.state.motivation,
            onValueChange = { vm.updateState(vm.state.copy(motivation = it)) },
            label = { Text("¿Por qué quieres adoptar?") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onNext, modifier = Modifier.align(Alignment.End)) {
            Text("Siguiente")
        }
    }
}