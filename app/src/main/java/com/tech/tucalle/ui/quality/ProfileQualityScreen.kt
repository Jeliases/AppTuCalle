package com.tech.tucalle.ui.quality

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState by profileViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf("Ajustes") }
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = { BottomNavigationBarDynamic(rol = "QUALITY", currentSelection = "Perfil") },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 1. INFO DE PERFIL (Avatar y Nombres)
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = uiState.fotoUrl.ifEmpty { "https://ui-avatars.com/api/?name=${uiState.nombre}&background=D32F2F&color=fff" },
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Mi perfil", color = Color.Gray, fontSize = 14.sp)
                    Text(
                        text = if (uiState.isLoading) "Cargando..." else "${uiState.nombre} ${uiState.apellidos}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Quality", color = Color.Gray, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. TARJETA DE ESTADÍSTICAS Y LOGROS
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mis logros", color = Color.Gray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("🎁 🏅 🏆", fontSize = 18.sp) // Placeholders visuales
                    }

                    Divider(modifier = Modifier.width(1.dp).height(50.dp), color = Color.LightGray)

                    Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                        Text("${uiState.seguidores} seguidores", fontSize = 13.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${uiState.totalResenas} reseñas", fontSize = 13.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("1 año antigüedad", fontSize = 13.sp, color = Color.DarkGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. SISTEMA DE TABS
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileTab("Ajustes", selectedTab == "Ajustes") { selectedTab = "Ajustes" }
                ProfileTab("Mis reseñas", selectedTab == "Mis reseñas") { selectedTab = "Mis reseñas" }
                ProfileTab("Mis huariques", selectedTab == "Mis huariques") { selectedTab = "Mis huariques" }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. CONTENIDO DINÁMICO SEGÚN EL TAB
            when (selectedTab) {
                "Ajustes" -> AjustesQualityContent(uiState)
                "Mis reseñas" -> Text("Aquí irá la lista de reseñas (Próxima tarea)", modifier = Modifier.padding(24.dp), color = Color.Gray)
                "Mis huariques" -> Text("Aquí irá la lista de huariques favoritos", modifier = Modifier.padding(24.dp), color = Color.Gray)
            }
        }
    }
}

@Composable
fun ProfileTab(title: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        val color = if (selected) Color(0xFFD32F2F) else Color.Gray
        Text(title, color = color, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
        if (selected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.height(3.dp).width(40.dp).background(color))
        }
    }
}

@Composable
fun AjustesQualityContent(uiState: com.tech.tucalle.ui.viewmodel.ProfileUiState) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text("Información de tu cuenta", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = uiState.nombre, onValueChange = {}, label = { Text("Nombre(s)*") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F)))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = uiState.apellidos, onValueChange = {}, label = { Text("Apellidos(s)*") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F)))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = uiState.correo, onValueChange = {}, label = { Text("Correo Electrónico*") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F)))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = uiState.celular, onValueChange = {}, label = { Text("Celular") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F)))

        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = { /* Guardar cambios */ },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Guardar cambios", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}