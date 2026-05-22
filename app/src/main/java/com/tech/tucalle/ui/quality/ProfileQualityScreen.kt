package com.tech.tucalle.ui.quality

import androidx.navigation.NavHostController
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tech.tucalle.navigation.BottomNavigationBarDynamic
import com.tech.tucalle.ui.components.*
import com.tech.tucalle.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileQualityScreen(
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
                rol = "QUALITY",
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
                            AsyncImage(model = uiState.fotoUrl.ifBlank { "https://ui-avatars.com/api/?name=${uiState.nombre}&background=D32F2F&color=fff&size=100" }, contentDescription = "Foto de perfil", modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.LightGray), contentScale = ContentScale.Crop)
                            Box(modifier = Modifier.size(26.dp).align(Alignment.BottomEnd).clip(CircleShape).background(Color(0xFFD32F2F)), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.CameraAlt, contentDescription = "Cambiar foto", tint = Color.White, modifier = Modifier.size(14.dp)) }
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Mi perfil", color = Color.Gray, fontSize = 13.sp)
                        Text("${uiState.nombre} ${uiState.apellidos}".trim().ifBlank { "Cargando..." }, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("Quality", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                // BOTÓN CERRAR SESIÓN
                IconButton(onClick = { profileViewModel.cerrarSesion { onLogout() } }) {
                    Icon(Icons.Outlined.ExitToApp, contentDescription = "Cerrar sesión", tint = Color(0xFFD32F2F))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mis logros", color = Color.Gray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("🎁 🏅 🏆", fontSize = 18.sp)
                    }
                    Divider(modifier = Modifier.width(1.dp).height(50.dp), color = Color.LightGray)
                    Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                        Text("${uiState.seguidores} seguidores", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${uiState.totalResenas} reseñas", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(profileViewModel.calcularAntiguedad() + " antigüedad", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                listOf("Ajustes" to Icons.Outlined.Settings, "Mis reseñas" to Icons.Outlined.Star, "Mis huariques" to Icons.Outlined.Favorite).forEach { (tab, icon) ->
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
                "Ajustes"       -> AjustesQualityContent(uiState, profileViewModel, isEditing, onToggleEdit = { isEditing = !isEditing })
                "Mis reseñas"   -> ProximamenteQualityContent("Mis Reseñas", "Aquí aparecerán todas las evaluaciones CHAS que has publicado en los huariques.")
                "Mis huariques" -> ProximamenteQualityContent("Mis Huariques", "Aquí verás los huariques que sigues y has marcado como favoritos.")
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
private fun AjustesQualityContent(uiState: com.tech.tucalle.ui.viewmodel.ProfileUiState, vm: ProfileViewModel, isEditing: Boolean, onToggleEdit: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Información de tu cuenta", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            IconButton(onClick = onToggleEdit) {
                Icon(imageVector = if (isEditing) Icons.Outlined.LockOpen else Icons.Outlined.Lock, contentDescription = "Alternar edición", tint = Color(0xFFD32F2F))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        ProfileTextField("Nombre(s)", uiState.nombre, vm::onNombreChange, isEditing)
        ProfileTextField("Apellido(s)", uiState.apellidos, vm::onApellidosChange, isEditing)
        ProfileTextField("Correo Electrónico", uiState.correo, {}, false) // Solo lectura
        ProfileTextField("Celular", uiState.celular, vm::onCelularChange, isEditing, KeyboardType.Number, 9)
        ProfileDateField("Fecha de nacimiento", uiState.fechaNacimiento, vm::onFechaNacimientoChange, isEditing)
        ProfileDocumentField(uiState.tipoDocumento, uiState.dni, vm::onTipoDocumentoChange, vm::onDniChange, isEditing)

        Spacer(modifier = Modifier.height(20.dp))
        Text("Disponibilidad para evaluaciones", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(10.dp))

        ProfileDiasSemanaField(uiState.diasDisponibles, { vm.onDiasChange(it) }, isEditing)

        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                ProfileTextField("Desde (Ej: 08:00 AM)", uiState.horaDesde, vm::onHoraDesdeChange, isEditing)
            }
            Box(modifier = Modifier.weight(1f)) {
                ProfileTextField("Hasta (Ej: 09:30 PM)", uiState.horaHasta, vm::onHoraHastaChange, isEditing)
            }
        }

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
                else Text("Guardar cambios", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun ProximamenteQualityContent(titulo: String, descripcion: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.Build, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(descripcion, color = Color.Gray, fontSize = 13.sp, lineHeight = 20.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(12.dp))
            Text("PRÓXIMAMENTE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.LightGray, letterSpacing = 2.sp)
        }
    }
}