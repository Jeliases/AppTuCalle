package com.tech.tucalle.ui.huarique

import com.tech.tucalle.ui.theme.Roboto
import com.tech.tucalle.ui.theme.Poppins
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.tech.tucalle.ui.viewmodel.AuthViewModel
import com.tech.tucalle.ui.viewmodel.StoreViewModel

@Composable
fun StoreHomeScreen(
    authViewModel: AuthViewModel,
    navController: NavHostController? = null
) {
    val storeViewModel: StoreViewModel = viewModel()
    val uiState by storeViewModel.uiState.collectAsState()
    val scroll = rememberScrollState()

    // Tab seleccionado en el navbar
    var navSelected by remember { mutableStateOf("Home") }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            StoreBottomNav(
                selected = navSelected,
                onSelect = { item ->
                    navSelected = item
                    when (item) {
                        "Perfil" -> navController?.navigate("perfil_tienda")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(20.dp)
        ) {
            // ── CABECERA ────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = uiState.logoUrl.ifBlank { "https://ui-avatars.com/api/?name=${uiState.nombreTienda}&background=D32F2F&color=fff&size=100" },
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Mi perfil", color = Color.Gray, fontSize = 12.sp,
                            fontFamily = Poppins)
                        Text(
                            uiState.nombreTienda.ifBlank { "Cargando..." },
                            fontSize = 20.sp,
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Huarique", color = Color.Gray, fontSize = 13.sp,
                            fontFamily = Poppins)
                    }
                }
                // Botón de configuración va al perfil
                IconButton(onClick = { navController?.navigate("perfil_tienda") }) {
                    Icon(Icons.Outlined.Settings, contentDescription = "Ajustes", tint = Color(0xFFD32F2F))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── TARJETA DE MÉTRICAS ─────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Plan con botón activar
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(uiState.plan, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                            fontFamily = Poppins)
                        Text("Plan Actual", color = Color.Gray, fontSize = 11.sp,
                            fontFamily = Poppins)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Mejora tu plan", color = Color.Gray, fontSize = 9.sp,
                            fontFamily = Poppins)
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("ACTIVAR", fontSize = 11.sp,
                                fontFamily = Poppins, fontWeight = FontWeight.Bold)
                        }
                    }

                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(80.dp),
                        color = Color.LightGray
                    )

                    // Stats
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("${uiState.seguidores} seguidores", fontSize = 13.sp,
                            fontFamily = Poppins)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${uiState.totalResenas} reseñas", fontSize = 13.sp,
                            fontFamily = Poppins)
                        Spacer(modifier = Modifier.height(4.dp))
                        val horario = if (uiState.horarioApertura.isNotBlank() && uiState.horarioCierre.isNotBlank())
                            "${uiState.horarioApertura} – ${uiState.horarioCierre}"
                        else "Sin horario"
                        Text(horario, fontSize = 12.sp,
                            fontFamily = Poppins, color = Color.Gray)
                        Text("horario", color = Color.Gray, fontSize = 10.sp,
                            fontFamily = Poppins)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── ESTADO ABIERTO / CERRADO ────────────────────────────
            Text("Estado de tu tienda", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                fontFamily = Roboto)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFE0E0E0))
            ) {
                listOf("Abierto", "Cerrado").forEach { estado ->
                    val isSelected = uiState.estadoLocal == estado
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (isSelected) Color(0xFFD32F2F) else Color.Transparent)
                            .clickable { storeViewModel.cambiarEstado(estado) },
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

            Spacer(modifier = Modifier.height(24.dp))

            // ── SECCIÓN "EN DESARROLLO" ─────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F8)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.Build,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Métricas y estadísticas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = Roboto
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Aquí verás tus platos más populares, recomendaciones recibidas y el rendimiento de tu huarique.",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        fontFamily = Poppins,
                        lineHeight = 20.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "ESTAMOS TRABAJANDO EN ELLO",
                        fontSize = 10.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

// ── NAVBAR TIENDA ────────────────────────────────────────────────
@Composable
fun StoreBottomNav(selected: String, onSelect: (String) -> Unit) {
    data class NavItem(val label: String, val icon: ImageVector)

    val items = listOf(
        NavItem("Home",    Icons.Outlined.Home),
        NavItem("Platos",  Icons.Outlined.MenuBook),
        NavItem("Reseñas", Icons.Outlined.Star),
        NavItem("Métricas",Icons.Outlined.BarChart),
        NavItem("Perfil",  Icons.Outlined.Person)
    )

    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        items.forEach { item ->
            val isSelected = selected == item.label
            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) Color(0xFFD32F2F) else Color.Gray
                    )
                },
                label = {
                    Text(
                        item.label,
                        fontSize = 10.sp,
                        fontFamily = Poppins,
                        color = if (isSelected) Color(0xFFD32F2F) else Color.Gray
                    )
                },
                selected = isSelected,
                onClick = { onSelect(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Color(0xFFD32F2F),
                    selectedTextColor   = Color(0xFFD32F2F),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor      = Color.White
                )
            )
        }
    }
}

// ── CAMPO REUTILIZABLE ────────────────────────────────────────────
@Composable
fun CustomField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = Color.Gray, fontSize = 12.sp,
            fontFamily = Poppins)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
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