package com.example.seguimiento.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seguimiento.R

val ColorVivoNaranjaAdmin = Color(0xFFFF6D00)
val ColorBarraBaseAdmin = Color(0xFFFDF7F2)

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
            .background(ColorBarraBaseAdmin)
            .shadow(12.dp)
    ) {
        NavigationBar(
            containerColor = ColorBarraBaseAdmin,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0, 0, 0, 0),
            modifier = Modifier.height(65.dp)
        ) {
            val navItems = listOf(
                AdminNavItem(stringResource(R.string.nav_admin_panel), Icons.Default.AdminPanelSettings, "estadisticas"),
                AdminNavItem(stringResource(R.string.nav_admin_requests), Icons.AutoMirrored.Filled.ListAlt, "lista_solicitudes"),
                AdminNavItem(stringResource(R.string.nav_admin_pets), Icons.Default.Pets, "encontrar_mascotas"),
                AdminNavItem(stringResource(R.string.nav_admin_logout), Icons.AutoMirrored.Filled.ExitToApp, "logout")
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
                            tint = if (isSelected) ColorVivoNaranjaAdmin else Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    label = {
                        Text(
                            item.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) ColorVivoNaranjaAdmin else Color.Gray,
                            maxLines = 1
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
                .background(ColorBarraBaseAdmin)
        )
    }
}
