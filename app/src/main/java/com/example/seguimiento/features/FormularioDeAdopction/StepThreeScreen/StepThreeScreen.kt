package com.example.seguimiento.features.FormularioDeAdopction.StepThreeScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seguimiento.features.FormularioDeAdopction.AdoptionViewModel

@Composable
fun StepThreeScreen(
    vm: AdoptionViewModel = viewModel(), 
    onNext: () -> Unit = {}
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Paso 3: Logística", style = MaterialTheme.typography.headlineSmall)

        Row {
            Checkbox(
                checked = vm.state.awareOfCosts, 
                onCheckedChange = { vm.updateState(vm.state.copy(awareOfCosts = it)) }
            )
            Text("Entiendo los gastos de veterinaria")
        }

        OutlinedTextField(
            value = vm.state.movingPlan,
            onValueChange = { vm.updateState(vm.state.copy(movingPlan = it)) },
            label = { Text("¿Qué harías si te mudas?") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = onNext, modifier = Modifier.padding(top = 16.dp)) {
            Text("Siguiente")
        }
    }
}