package com.example.seguimiento.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val ColorVivoNaranja = Color(0xFFFF6D00)
val ColorBarraBase = Color(0xFFFDF7F2)

data class AdminNavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun AdminBottomBar(
    currentRoute: String,
    onNavigateToEstadisticas: () -> Unit,
    onNavigateToListaSolicitudes: () -> Unit,
    onNavigateToEncontrarMascotas: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorBarraBase)
            .shadow(12.dp)
    ) {
        NavigationBar(
            containerColor = ColorBarraBase,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0, 0, 0, 0),
            modifier = Modifier.height(65.dp)
        ) {
            val navItems = listOf(
                AdminNavItem("Estadísticas", Icons.Default.BarChart, "estadisticas"),
                AdminNavItem("Solicitudes", Icons.AutoMirrored.Filled.ListAlt, "lista_solicitudes"),
                AdminNavItem("Mascotas", Icons.Default.Pets, "encontrar_mascotas"),
                AdminNavItem("Salir", Icons.AutoMirrored.Filled.ExitToApp, "logout")
            )
            navItems.forEach { item ->
                val isSelected = currentRoute == item.route
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        when (item.route) {
                            "estadisticas" -> onNavigateToEstadisticas()
                            "lista_solicitudes" -> onNavigateToListaSolicitudes()
                            "encontrar_mascotas" -> onNavigateToEncontrarMascotas()
                            "logout" -> onLogout()
                        }
                    },
                    icon = {
                        Icon(
                            item.icon,
                            null,
                            tint = if (isSelected) ColorVivoNaranja else Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    label = {
                        Text(
                            item.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) ColorVivoNaranja else Color.Gray
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )
            }
        }
        Spacer(
            Modifier
                .navigationBarsPadding()
                .height(10.dp)
                .fillMaxWidth()
                .background(ColorBarraBase)
        )
    }
}
