package com.tech.tucalle.ui.usuario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.tech.tucalle.navigation.BottomNavigationBarDynamic
import com.tech.tucalle.ui.components.*
import com.tech.tucalle.ui.viewmodel.ProfileViewModel
import com.tech.tucalle.ui.theme.Roboto // 🔥 Roboto global

@Composable
fun ProfileUsuarioScreen(
    navController: NavHostController,
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState by profileViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf("Ajustes") }
    var isEditing by remember { mutableStateOf(false) }
    val scroll = rememberScrollState()

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BottomNavigationBarDynamic(
                rol = "USUARIO",
                currentSelection = "Perfil",
                navController = navController
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(scroll)) {
            Spacer(modifier = Modifier.height(20.dp))

            // HEADER CON BOTÓN DE LOGOUT
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BotonCambiarFoto(rutaStorage = "fotos_perfil/${uiState.uid}/perfil.jpg", onFotoSubida = { url -> profileViewModel.onFotoUrlChange(url); profileViewModel.guardarCambios() }) {
                        Box {
                            AsyncImage(model = uiState.fotoUrl.ifBlank { "https://ui-avatars.com/api/?name=${uiState.nombre}+${uiState.apellidos}&background=D32F2F&color=fff&size=100" }, contentDescription = "Foto de perfil", modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.LightGray), contentScale = ContentScale.Crop)
                            Box(modifier = Modifier.size(26.dp).align(Alignment.BottomEnd).clip(CircleShape).background(Color(0xFFD32F2F)), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.CameraAlt, contentDescription = "Cambiar foto", tint = Color.White, modifier = Modifier.size(14.dp)) }
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Mi perfil", color = Color.Gray, fontSize = 13.sp)
                        // 🔥 ROBOTO PARA EL NOMBRE
                        Text(
                            text = "${uiState.nombre} ${uiState.apellidos}".trim().ifBlank { "Cargando..." },
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text("Usuario", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                IconButton(onClick = { profileViewModel.cerrarSesion { onLogout() } }) {
                    Icon(Icons.Outlined.ExitToApp, contentDescription = "Cerrar sesión", tint = Color(0xFFD32F2F))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatItem("${uiState.totalHuariques}", "huariques")
                    Divider(modifier = Modifier.width(1.dp).height(40.dp), color = Color.LightGray)
                    StatItem(profileViewModel.calcularAntiguedad(), "antigüedad")
                    Divider(modifier = Modifier.width(1.dp).height(40.dp), color = Color.LightGray)
                    StatItem("${uiState.totalResenas}", "reseñas")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // TABS
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                listOf("Ajustes" to Icons.Outlined.Settings, "Mis huariques" to Icons.Outlined.Favorite, "Mis pedidos" to Icons.Outlined.ShoppingBag).forEach { (tab, icon) ->
                    val isSelected = selectedTab == tab
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { selectedTab = tab }) {
                        Icon(icon, contentDescription = tab, tint = if (isSelected) Color(0xFFD32F2F) else Color.Gray, modifier = Modifier.size(22.dp))
                        Text(tab, fontSize = 11.sp, color = if (isSelected) Color(0xFFD32F2F) else Color.Gray, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        if (isSelected) Box(modifier = Modifier.height(2.dp).width(40.dp).background(Color(0xFFD32F2F)))
                    }
                }
            }

            Divider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(top = 8.dp))
            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                "Ajustes"       -> AjustesUsuarioContent(uiState, profileViewModel, isEditing, onToggleEdit = { isEditing = !isEditing })
                "Mis huariques" -> ProximamenteContent("Mis Huariques favoritos", "Aquí verás los huariques que has marcado como favoritos.")
                "Mis pedidos"   -> ProximamenteContent("Mis Pedidos", "Aquí verás tu historial de pedidos con opciones para repetir y dejar reseña.")
            }

            if (uiState.mensajeGuardado.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(uiState.mensajeGuardado, color = if (uiState.mensajeGuardado.startsWith("✅")) Color(0xFF4CAF50) else Color.Red, modifier = Modifier.padding(horizontal = 24.dp), fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun StatItem(valor: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // 🔥 ESTILO GLOBALES PARA ESTADÍSTICAS
        Text(valor, style = MaterialTheme.typography.titleLarge)
        Text(label, color = Color.Gray, fontSize = 11.sp)
    }
}

@Composable
private fun AjustesUsuarioContent(uiState: com.tech.tucalle.ui.viewmodel.ProfileUiState, vm: ProfileViewModel, isEditing: Boolean, onToggleEdit: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            // 🔥 ROBOTO
            Text("Información de tu cuenta", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            IconButton(onClick = onToggleEdit) {
                Icon(imageVector = if (isEditing) Icons.Outlined.LockOpen else Icons.Outlined.Lock, contentDescription = "Alternar edición", tint = Color(0xFFD32F2F))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        ProfileTextField("Nombre(s)*", uiState.nombre, vm::onNombreChange, isEditing)
        ProfileTextField("Apellido(s)*", uiState.apellidos, vm::onApellidosChange, isEditing)
        ProfileTextField("Correo Electrónico*", uiState.correo, {}, false)
        ProfileTextField("Celular*", uiState.celular, vm::onCelularChange, isEditing, KeyboardType.Number, 9)
        ProfileDateField("Fecha de nacimiento*", uiState.fechaNacimiento, vm::onFechaNacimientoChange, isEditing)
        ProfileDocumentField(uiState.tipoDocumento, uiState.dni, vm::onTipoDocumentoChange, vm::onDniChange, isEditing)

        if (isEditing) {
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = { vm.guardarCambios(); onToggleEdit() },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(28.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Guardar cambios", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun ProximamenteContent(titulo: String, descripcion: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.Build, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))

            Text(titulo, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(descripcion, color = Color.Gray, fontSize = 13.sp, lineHeight = 20.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(12.dp))
            Text("PRÓXIMAMENTE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.LightGray, letterSpacing = 2.sp)
        }
    }
}