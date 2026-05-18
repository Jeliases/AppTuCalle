package com.tech.tucalle.ui.usuario

import androidx.compose.foundation.background
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
import com.tech.tucalle.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileUsuarioScreen(
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(containerColor = Color.White) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 1. HEADER DEL USUARIO
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = uiState.fotoUrl.ifEmpty { "https://ui-avatars.com/api/?name=${uiState.nombre}&background=D32F2F&color=fff" },
                    contentDescription = "Foto",
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Hola,", color = Color.Gray, fontSize = 14.sp)
                    Text(text = uiState.nombre, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 2. SECCIÓN DE LOGROS (Placeholder visual)
            Text("Mis Logros", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Aquí en el futuro mostraremos los iconos de los logros
                Badge("Fundador")
                Badge("Explorador")
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 3. CAMPOS DE EDICIÓN
            Text("Ajustes de cuenta", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = uiState.nombre, onValueChange = {}, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = uiState.correo, onValueChange = {}, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), readOnly = true)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = uiState.celular, onValueChange = {}, label = { Text("Celular") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = { /* Lógica guardar */ },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text("Guardar cambios", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun Badge(text: String) {
    Surface(color = Color(0xFFFDE7E7), shape = RoundedCornerShape(16.dp)) {
        Text(text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}