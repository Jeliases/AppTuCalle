package com.tech.tucalle.ui.huarique

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
import com.tech.tucalle.ui.viewmodel.ProfileViewModel
import com.tech.tucalle.ui.viewmodel.StoreViewModel

@Composable
fun ProfileTiendaScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    profileViewModel: ProfileViewModel = viewModel(),
    storeViewModel: StoreViewModel = viewModel()
) {
    val profile by profileViewModel.uiState.collectAsState()
    val store   by storeViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf("Ajustes") }
    var showFotoDialog by remember { mutableStateOf(false) }
    var nuevaFotoUrl by remember { mutableStateOf("") }
    val scroll = rememberScrollState()

    // Diálogo para cambiar foto
    if (showFotoDialog) {
        AlertDialog(
            onDismissRequest = { showFotoDialog = false },
            title = { Text("Cambiar logo", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Pega la URL de la nueva imagen:", color = Color.Gray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nuevaFotoUrl,
                        onValueChange = { nuevaFotoUrl = it },
                        placeholder = { Text("https://...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD32F2F)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nuevaFotoUrl.isNotBlank()) {
                            storeViewModel.onLogoUrlChange(nuevaFotoUrl)
                            storeViewModel.guardarCambios()
                        }
                        showFotoDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showFotoDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            StoreBottomNav(selected = "Perfil", onSelect = { if (it == "Home") onBack() })
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
                        model = store.logoUrl.ifBlank {
                            "https://ui-avatars.com/api/?name=${store.nombreTienda}&background=D32F2F&color=fff&size=100"
                        },
                        contentDescription = "Logo tienda",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                    // Botón cámara encima de la foto
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
                        store.nombreTienda.ifBlank { "Cargando..." },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Huarique", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── TARJETA STATS ──────────────────────────────────────
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(store.plan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Plan Actual", color = Color.Gray, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Mejora tu plan", color = Color.Gray, fontSize = 9.sp)
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("ACTIVAR", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Divider(modifier = Modifier.width(1.dp).height(70.dp), color = Color.LightGray)

                    Column {
                        Text("${store.seguidores} seguidores", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${store.totalResenas} reseñas", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        val horario = if (store.horarioApertura.isNotBlank())
                            "${store.horarioApertura} – ${store.horarioCierre}"
                        else "Sin horario"
                        Text(horario, fontSize = 12.sp, color = Color.Gray)
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
                    "Ajustes" to Icons.Outlined.Settings,
                    "Mis reseñas" to Icons.Outlined.Star
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
                            fontSize = 12.sp,
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

            // ── CONTENIDO TABS ─────────────────────────────────────
            when (selectedTab) {
                "Ajustes"     -> AjustesTiendaContent(store, storeViewModel)
                "Mis reseñas" -> MisResenasTiendaContent()
            }

            // Mensaje guardado
            if (store.mensajeGuardado.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    store.mensajeGuardado,
                    color = if (store.mensajeGuardado.startsWith("✅")) Color(0xFF4CAF50) else Color.Red,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun AjustesTiendaContent(
    store: com.tech.tucalle.ui.viewmodel.StoreUiState,
    vm: StoreViewModel
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {

        // Estado abierto/cerrado
        Text("Estado de tu tienda", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFE0E0E0))
        ) {
            listOf("Abierto", "Cerrado").forEach { estado ->
                val isSelected = store.estadoLocal == estado
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isSelected) Color(0xFFD32F2F) else Color.Transparent)
                        .clickable { vm.cambiarEstado(estado) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        estado,
                        color = if (isSelected) Color.White else Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Horario
        Text("Horario", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Apertura", color = Color.Gray, fontSize = 12.sp)
                OutlinedTextField(
                    value = store.horarioApertura,
                    onValueChange = { vm.onHorarioAperturaChange(it) },
                    placeholder = { Text("08:00 AM") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD32F2F),
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Cierre", color = Color.Gray, fontSize = 12.sp)
                OutlinedTextField(
                    value = store.horarioCierre,
                    onValueChange = { vm.onHorarioCierreChange(it) },
                    placeholder = { Text("10:00 PM") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD32F2F),
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Info de tienda
        Text("Información de tu tienda", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        CustomField("Razón Social",       store.razonSocial,       vm::onRazonSocialChange)
        CustomField("Nombre de la tienda",store.nombreTienda,      vm::onNombreChange)
        CustomField("Celular",            store.celular,           vm::onCelularChange)
        CustomField("WhatsApp",           store.whatsapp,          vm::onWhatsappChange)
        CustomField("Dirección",          store.direccion,         vm::onDireccionChange)

        Spacer(modifier = Modifier.height(20.dp))

        // Info encargado
        Text("Información de Encargado", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        CustomField("Nombres y Apellidos",store.encargadoNombre,   vm::onEncargadoNombreChange)
        CustomField("Número de contacto", store.encargadoContacto, vm::onEncargadoContactoChange)
        CustomField("Correo electrónico", store.encargadoEmail,    vm::onEncargadoEmailChange)

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { vm.guardarCambios() },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(28.dp),
            enabled = !store.isLoading
        ) {
            if (store.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Guardar cambios", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun MisResenasTiendaContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Outlined.Star,
                contentDescription = null,
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Reseñas de Qualities", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Aquí aparecerán las evaluaciones CHAS que los Qualities han hecho de tu huarique.",
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