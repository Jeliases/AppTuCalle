package com.tech.tucalle.ui.quality

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tech.tucalle.navigation.BottomNavigationBarDynamic
import com.tech.tucalle.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileQualityScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState by profileViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf("Ajustes") }
    var showFotoDialog by remember { mutableStateOf(false) }
    var nuevaFotoUrl by remember { mutableStateOf("") }
    val scroll = rememberScrollState()

    // Diálogo cambio de foto
    if (showFotoDialog) {
        AlertDialog(
            onDismissRequest = { showFotoDialog = false },
            title = { Text("Cambiar foto de perfil", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Pega la URL de tu nueva foto:", color = Color.Gray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nuevaFotoUrl,
                        onValueChange = { nuevaFotoUrl = it },
                        placeholder = { Text("https://...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F))
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nuevaFotoUrl.isNotBlank()) {
                            profileViewModel.onFotoUrlChange(nuevaFotoUrl)
                            profileViewModel.guardarCambios()
                        }
                        showFotoDialog = false
                        nuevaFotoUrl = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showFotoDialog = false; nuevaFotoUrl = "" }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BottomNavigationBarDynamic(
                rol = "QUALITY",
                currentSelection = "Perfil",
                onItemClick = { item -> if (item == "Home") onBack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scroll)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // ── HEADER ─────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    AsyncImage(
                        model = uiState.fotoUrl.ifBlank {
                            "https://ui-avatars.com/api/?name=${uiState.nombre}&background=D32F2F&color=fff&size=100"
                        },
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Color(0xFFD32F2F))
                            .clickable { showFotoDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.CameraAlt,
                            contentDescription = "Cambiar foto",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Mi perfil", color = Color.Gray, fontSize = 13.sp)
                    Text(
                        "${uiState.nombre} ${uiState.apellidos}".trim().ifBlank { "Cargando..." },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Quality", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── STATS CON LOGROS ───────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mis logros", color = Color.Gray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        // Placeholders visuales — se llenarán con LogroDTO en el futuro
                        Text("🎁 🏅 🏆", fontSize = 18.sp)
                    }
                    Divider(modifier = Modifier.width(1.dp).height(50.dp), color = Color.LightGray)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text("${uiState.seguidores} seguidores", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${uiState.totalResenas} reseñas", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(profileViewModel.calcularAntiguedad() + " antigüedad", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── TABS ───────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                listOf(
                    "Ajustes"       to Icons.Outlined.Settings,
                    "Mis reseñas"   to Icons.Outlined.Star,
                    "Mis huariques" to Icons.Outlined.Favorite
                ).forEach { (tab, icon) ->
                    val isSelected = selectedTab == tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { selectedTab = tab }
                    ) {
                        Icon(
                            icon,
                            contentDescription = tab,
                            tint = if (isSelected) Color(0xFFD32F2F) else Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            tab,
                            fontSize = 11.sp,
                            color = if (isSelected) Color(0xFFD32F2F) else Color.Gray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .height(2.dp)
                                    .width(40.dp)
                                    .background(Color(0xFFD32F2F))
                            )
                        }
                    }
                }
            }

            Divider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(top = 8.dp))
            Spacer(modifier = Modifier.height(16.dp))

            // ── CONTENIDO ──────────────────────────────────────────
            when (selectedTab) {
                "Ajustes"       -> AjustesQualityContent(uiState, profileViewModel)
                "Mis reseñas"   -> ProximamenteQualityContent("Mis Reseñas", "Aquí aparecerán todas las evaluaciones CHAS que has publicado en los huariques.")
                "Mis huariques" -> ProximamenteQualityContent("Mis Huariques", "Aquí verás los huariques que sigues y has marcado como favoritos.")
            }

            if (uiState.mensajeGuardado.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    uiState.mensajeGuardado,
                    color = if (uiState.mensajeGuardado.startsWith("✅")) Color(0xFF4CAF50) else Color.Red,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun AjustesQualityContent(
    uiState: com.tech.tucalle.ui.viewmodel.ProfileUiState,
    vm: ProfileViewModel
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text("Información de tu cuenta", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(16.dp))

        QualityField("Nombre(s)", uiState.nombre, vm::onNombreChange)
        QualityField("Apellido(s)", uiState.apellidos, vm::onApellidosChange)

        // Correo — solo lectura
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Correo Electrónico", color = Color.Gray, fontSize = 12.sp)
            TextField(
                value = uiState.correo,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor   = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor  = Color.Transparent,
                    focusedIndicatorColor   = Color.LightGray,
                    unfocusedIndicatorColor = Color.LightGray
                )
            )
        }

        QualityField("Celular", uiState.celular, vm::onCelularChange)
        QualityField("Fecha de nacimiento", uiState.fechaNacimiento, vm::onFechaNacimientoChange,
            placeholder = "DD/MM/AAAA")
        QualityField("Número de identidad", uiState.dni, vm::onDniChange)

        Spacer(modifier = Modifier.height(20.dp))

        // ── DÍAS DISPONIBLES ──────────────────────────────────────
        Text("Días disponibles", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(10.dp))
        val dias = listOf("L", "M", "X", "J", "V", "S", "D")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            dias.forEach { dia ->
                val isSelected = uiState.diasDisponibles.contains(dia)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color(0xFFD32F2F) else Color(0xFFF0F0F0)
                        )
                        .clickable { vm.onDiaToggle(dia) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        dia,
                        color = if (isSelected) Color.White else Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── HORARIO (formato 12h AM/PM) ───────────────────────────
        Text("Horario de disponibilidad", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Desde", color = Color.Gray, fontSize = 12.sp)
                OutlinedTextField(
                    value = uiState.horaDesde,
                    onValueChange = { vm.onHoraDesdeChange(it) },
                    placeholder = { Text("08:00 AM") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Color(0xFFD32F2F),
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Hasta", color = Color.Gray, fontSize = 12.sp)
                OutlinedTextField(
                    value = uiState.horaHasta,
                    onValueChange = { vm.onHoraHastaChange(it) },
                    placeholder = { Text("09:30 PM") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Color(0xFFD32F2F),
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { vm.guardarCambios() },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(28.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Guardar cambios", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun QualityField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = ""
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { if (placeholder.isNotBlank()) Text(placeholder, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor   = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor  = Color.Transparent,
                focusedIndicatorColor   = Color(0xFFD32F2F),
                unfocusedIndicatorColor = Color.LightGray,
                cursorColor             = Color(0xFFD32F2F)
            )
        )
    }
}

@Composable
private fun ProximamenteQualityContent(titulo: String, descripcion: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Outlined.Build,
                contentDescription = null,
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                descripcion,
                color = Color.Gray,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "PRÓXIMAMENTE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray,
                letterSpacing = 2.sp
            )
        }
    }
}