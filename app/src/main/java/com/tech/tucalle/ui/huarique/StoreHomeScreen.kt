package com.tech.tucalle.ui.huarique

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tech.tucalle.ui.viewmodel.AuthViewModel
import com.tech.tucalle.ui.viewmodel.StoreViewModel

@Composable
fun StoreHomeScreen(authViewModel: AuthViewModel) { // Agregado el parámetro para NavGraph
    val storeViewModel: StoreViewModel = viewModel()
    val uiState by storeViewModel.uiState.collectAsState()
    val scroll = rememberScrollState()

    Scaffold(containerColor = Color.White) { p ->
        Column(modifier = Modifier.padding(p).fillMaxSize().verticalScroll(scroll).padding(20.dp)) {

            // CABECERA IDENTICA A TU FOTO (image_6a32b9)
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = "https://tu-logo-aqui.png",
                    contentDescription = null,
                    modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.LightGray)
                )
                Text("Mi perfil", fontSize = 14.sp, color = Color.Gray)
                Text(uiState.nombreTienda.ifBlank { "Cargando..." }, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Huarique", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SELECTOR ESTADO ROJO/GRIS
            Text("Estado de tu tienda", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth().height(45.dp).clip(RoundedCornerShape(23.dp)).background(Color(0xFFE0E0E0))) {
                val mod = Modifier.weight(1f).fillMaxHeight()
                Button(onClick = { storeViewModel.cambiarEstado("Abierto") }, modifier = mod,
                    colors = ButtonDefaults.buttonColors(containerColor = if(uiState.estadoLocal == "Abierto") Color(0xFFD32F2F) else Color.Transparent),
                    shape = RoundedCornerShape(23.dp)) { Text("Abierto", color = if(uiState.estadoLocal == "Abierto") Color.White else Color.Black) }

                Button(onClick = { storeViewModel.cambiarEstado("Cerrado") }, modifier = mod,
                    colors = ButtonDefaults.buttonColors(containerColor = if(uiState.estadoLocal == "Cerrado") Color(0xFFD32F2F) else Color.Transparent),
                    shape = RoundedCornerShape(23.dp)) { Text("Cerrado", color = if(uiState.estadoLocal == "Cerrado") Color.White else Color.Black) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CAMPOS DE TEXTO (Foto 1)
            Text("Información de tu tienda", fontWeight = FontWeight.Bold)
            CustomField("Razón Social", uiState.razonSocial, storeViewModel::onRazonSocialChange)
            CustomField("Nombre de la tienda", uiState.nombreTienda, storeViewModel::onNombreChange)
            CustomField("Celular", uiState.celular, storeViewModel::onCelularChange)
            CustomField("WhatsApp", uiState.whatsapp, storeViewModel::onWhatsappChange)
            CustomField("Dirección", uiState.direccion, storeViewModel::onDireccionChange)

            Spacer(modifier = Modifier.height(24.dp))

            // CAMPOS ENCARGADO (Foto 2)
            Text("Información de Encargado", fontWeight = FontWeight.Bold)
            CustomField("Nombres y Apellidos", uiState.encargadoNombre, storeViewModel::onEncargadoNombreChange)
            CustomField("Número de contacto", uiState.encargadoContacto, storeViewModel::onEncargadoContactoChange)
            CustomField("Correo electrónico", uiState.encargadoEmail, storeViewModel::onEncargadoEmailChange)

            Spacer(modifier = Modifier.height(30.dp))

            // BOTÓN GUARDAR ROJO
            Button(
                onClick = { storeViewModel.guardarCambios() },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Guardar cambios", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CustomField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                // SEPARAMOS LOS COLORES DEL CONTENEDOR (Solución al error)
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,

                // MANTENEMOS TU IDENTIDAD ROJA
                focusedIndicatorColor = Color(0xFFD32F2F),
                unfocusedIndicatorColor = Color.LightGray,
                cursorColor = Color(0xFFD32F2F)
            )
        )
    }
}