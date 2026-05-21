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
import com.google.firebase.auth.FirebaseAuth
import com.tech.tucalle.navigation.BottomNavigationBarDynamic
import com.tech.tucalle.ui.components.BotonCambiarFoto
import com.tech.tucalle.ui.viewmodel.ProfileViewModel
import com.tech.tucalle.ui.viewmodel.StoreViewModel

@Composable
fun ProfileTiendaScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    profileViewModel: ProfileViewModel = viewModel(),
    storeViewModel: StoreViewModel = viewModel()
) {
    val store by storeViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf("Ajustes") }
    val scroll = rememberScrollState()

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            // Corregido: Usamos el BottomNavigationBarDynamic global
            BottomNavigationBarDynamic(
                rol = "TIENDA",
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
                BotonCambiarFoto(
                    rutaStorage = "tiendas/${FirebaseAuth.getInstance().currentUser?.uid}/logo.jpg",
                    onFotoSubida = { url ->
                        storeViewModel.onLogoUrlChange(url)
                        storeViewModel.guardarCambios()
                    }
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
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(Color(0xFFD32F2F)),
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
                            Box(modifier = Modifier.height(2.dp).width(40.dp).background(Color(0xFFD32F2F)))
                        }
                    }
                }
            }

            Divider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(top = 8.dp))
            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                "Ajustes"     -> PerfilTiendaAjustesContent(store, storeViewModel)
                "Mis reseñas" -> PerfilTiendaMisResenasContent()
            }

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

// Renombradas para evitar choques con otras pantallas (Overload ambiguity)
@Composable
private fun PerfilTiendaAjustesContent(store: com.tech.tucalle.ui.viewmodel.StoreUiState, vm: StoreViewModel) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {

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
                    Text(estado, color = if (isSelected) Color.White else Color.Black, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F), unfocusedBorderColor = Color.LightGray)
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
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F), unfocusedBorderColor = Color.LightGray)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Información de tu tienda", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        PerfilTiendaCustomField("Razón Social",       store.razonSocial,       vm::onRazonSocialChange)
        PerfilTiendaCustomField("Nombre de la tienda",store.nombreTienda,      vm::onNombreChange)
        PerfilTiendaCustomField("Celular",            store.celular,           vm::onCelularChange)
        PerfilTiendaCustomField("WhatsApp",           store.whatsapp,          vm::onWhatsappChange)
        PerfilTiendaCustomField("Dirección",          store.direccion,         vm::onDireccionChange)

        Spacer(modifier = Modifier.height(20.dp))

        Text("Información de Encargado", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        PerfilTiendaCustomField("Nombres y Apellidos",store.encargadoNombre,   vm::onEncargadoNombreChange)
        PerfilTiendaCustomField("Número de contacto", store.encargadoContacto, vm::onEncargadoContactoChange)
        PerfilTiendaCustomField("Correo electrónico", store.encargadoEmail,    vm::onEncargadoEmailChange)

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
private fun PerfilTiendaCustomField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        TextField(
            value = value,
            onValueChange = onValueChange,
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
private fun PerfilTiendaMisResenasContent() {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Reseñas de Qualities", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Aquí aparecerán las evaluaciones CHAS que los Qualities han hecho de tu huarique.", color = Color.Gray, fontSize = 13.sp, lineHeight = 20.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(12.dp))
            Text("PRÓXIMAMENTE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.LightGray, letterSpacing = 2.sp)
        }
    }
}