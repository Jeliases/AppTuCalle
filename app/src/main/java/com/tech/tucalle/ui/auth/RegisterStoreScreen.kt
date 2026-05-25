package com.tech.tucalle.ui.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tech.tucalle.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterStoreScreen(
    onBack: () -> Unit,
    onNavigateToStoreDashboard: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    var nombreLocal by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }

    var direccionTexto by remember { mutableStateOf("") }
    var latitudSeleccionada by remember { mutableDoubleStateOf(0.0) }
    var longitudSeleccionada by remember { mutableDoubleStateOf(0.0) }
    var showMapSelector by remember { mutableStateOf(false) }

    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }
    var showInicioDialog by remember { mutableStateOf(false) }
    var showFinDialog by remember { mutableStateOf(false) }

    // Estado para Días de la Semana
    val diasSemana = listOf("L", "M", "X", "J", "V", "S", "D")
    var diasSeleccionados by remember { mutableStateOf(setOf<String>()) }

    var statusMessage by remember { mutableStateOf("") }
    var aceptoTerminos by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) } // 🔥 Bloquea el botón mientras sube

    // 🔥 ESTADOS PARA LAS IMÁGENES
    var logoUri by remember { mutableStateOf<Uri?>(null) }
    var portadaUri by remember { mutableStateOf<Uri?>(null) }

    // 🔥 LANZADORES DE LA GALERÍA
    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> logoUri = uri }

    val portadaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> portadaUri = uri }

    val scrollState = rememberScrollState()

    if (showMapSelector) {
        Dialog(onDismissRequest = { showMapSelector = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            MapSelectorScreen(
                onLocationSelected = { dirección, lat, lng ->
                    direccionTexto = dirección
                    latitudSeleccionada = lat
                    longitudSeleccionada = lng
                    showMapSelector = false
                },
                onDismiss = { showMapSelector = false }
            )
        }
    }

    if (showInicioDialog) {
        CustomTimePickerDialog(
            onDismissRequest = { showInicioDialog = false },
            onConfirm = { hour, minute ->
                horaInicio = formatTime12h(hour, minute)
                showInicioDialog = false
            }
        )
    }

    if (showFinDialog) {
        CustomTimePickerDialog(
            onDismissRequest = { showFinDialog = false },
            onConfirm = { hour, minute ->
                horaFin = formatTime12h(hour, minute)
                showFinDialog = false
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(horizontal = 30.dp).verticalScroll(scrollState)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.padding(top = 10.dp)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", modifier = Modifier.size(30.dp), tint = Color.Black)
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Regístrate", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        if (statusMessage.isNotEmpty()) {
            Text(text = statusMessage, color = if (statusMessage.contains("éxito")) Color(0xFF4CAF50) else Color.Red, fontSize = 14.sp, modifier = Modifier.padding(vertical = 10.dp))
        }

        // 🔥 HEADER TIPO FACEBOOK (Portada y Logo)
        Box(
            modifier = Modifier.fillMaxWidth().height(220.dp).padding(top = 10.dp)
        ) {
            // PORTADA RECTANGULAR
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clickable {
                        portadaPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                if (portadaUri != null) {
                    AsyncImage(
                        model = portadaUri,
                        contentDescription = "Portada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = "Add", tint = Color.Gray, modifier = Modifier.size(40.dp))
                            Text("Añadir Portada", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
            }

            // LOGO CIRCULAR SUPERPUESTO
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 10.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(4.dp, Color.White, CircleShape) // Borde blanco tipo FB
                    .clickable {
                        logoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                if (logoUri != null) {
                    AsyncImage(
                        model = logoUri,
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFFEEEEEE)), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = "Add", tint = Color.Gray, modifier = Modifier.size(24.dp))
                            Text("Logo", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        LoginInput(label = "Nombre del local*", value = nombreLocal, onValueChange = { nombreLocal = it })
        LoginInput(label = "Email*", value = email, onValueChange = { email = it })
        LoginInput(label = "Contraseña*", value = password, onValueChange = { password = it }, isPassword = true)
        LoginInput(label = "Número de contacto*", value = celular, onValueChange = { celular = it })

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Dirección del local*", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth().height(55.dp).clickable { showMapSelector = true }.shadow(8.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = if (direccionTexto.isEmpty()) "Seleccionar en el mapa..." else direccionTexto, color = if (direccionTexto.isEmpty()) Color.Gray else Color.Black, fontSize = 14.sp, maxLines = 1, modifier = Modifier.weight(1f))
                Icon(Icons.Default.LocationOn, contentDescription = "Mapa", tint = Color(0xFFD32F2F))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Días de apertura*", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            diasSemana.forEach { dia ->
                val isSelected = diasSeleccionados.contains(dia)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFFD32F2F) else Color(0xFFF5F5F5))
                        .clickable {
                            diasSeleccionados = if (isSelected) diasSeleccionados - dia else diasSeleccionados + dia
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = dia, color = if (isSelected) Color.White else Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Horario de atención*", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Card(modifier = Modifier.weight(1f).height(55.dp).clickable { showInicioDialog = true }.shadow(8.dp, RoundedCornerShape(12.dp)), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = if (horaInicio.isEmpty()) "Abre..." else horaInicio, color = if (horaInicio.isEmpty()) Color.Gray else Color.Black)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Card(modifier = Modifier.weight(1f).height(55.dp).clickable { showFinDialog = true }.shadow(8.dp, RoundedCornerShape(12.dp)), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = if (horaFin.isEmpty()) "Cierra..." else horaFin, color = if (horaFin.isEmpty()) Color.Gray else Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = aceptoTerminos, onCheckedChange = { aceptoTerminos = it }, colors = CheckboxDefaults.colors(checkedColor = Color(0xFFD32F2F)))
            Text("Acepto los términos y condiciones", fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty() && nombreLocal.isNotEmpty() &&
                    direccionTexto.isNotEmpty() && horaInicio.isNotEmpty() && horaFin.isNotEmpty() && diasSeleccionados.isNotEmpty()) {

                    coroutineScope.launch {
                        isUploading = true
                        statusMessage = "Subiendo fotos, espera un momento..."

                        val logoUrl = if (logoUri != null) authViewModel.uploadImageSuspend(logoUri!!) else ""
                        val portadaUrl = if (portadaUri != null) authViewModel.uploadImageSuspend(portadaUri!!) else ""
                        val horarioFinal = "$horaInicio - $horaFin"

                        val storeData = mapOf(
                            "nombre" to nombreLocal,
                            "email" to email,
                            "celular" to celular,
                            "diasApertura" to diasSeleccionados.toList(),
                            "horario" to horarioFinal,
                            "rol" to "TIENDA",
                            "direccion" to mapOf("texto" to direccionTexto, "latitud" to latitudSeleccionada, "longitud" to longitudSeleccionada),
                            "portadaUrl" to portadaUrl,
                            "logoUrl" to logoUrl,
                            "estado" to "APROBADO"
                        )

                        authViewModel.registerUserWithRole(
                            email = email, pass = password, userData = storeData,
                            onSuccess = { isUploading = false; onNavigateToStoreDashboard() },
                            onFailure = { isUploading = false; statusMessage = it }
                        )
                    }
                } else {
                    statusMessage = "Por favor, llena todos los campos."
                }
            },
            enabled = aceptoTerminos && !isUploading,
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(30.dp)
        ) {
            if (isUploading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Registrarse", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}
// 🔥 SE MANTIENE INTACTO
private fun formatTime12h(hour: Int, minute: Int): String {
    val amPm = if (hour >= 12) "PM" else "AM"
    var hour12 = hour % 12
    if (hour12 == 0) hour12 = 12
    return String.format(java.util.Locale.getDefault(), "%02d:%02d %s", hour12, minute, amPm)
}

// 🔥 SE MANTIENE INTACTO
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomTimePickerDialog(onDismissRequest: () -> Unit, onConfirm: (Int, Int) -> Unit) {
    val state = rememberTimePickerState(is24Hour = false)

    Dialog(onDismissRequest = onDismissRequest, properties = DialogProperties(usePlatformDefaultWidth = true)) {
        Surface(shape = RoundedCornerShape(28.dp), tonalElevation = 6.dp, color = Color.White, modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Selecciona la hora", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(20.dp))
                TimePicker(state = state)
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismissRequest) { Text("Cancelar", color = Color(0xFFD32F2F)) }
                    TextButton(onClick = { onConfirm(state.hour, state.minute) }) { Text("Aceptar", color = Color(0xFFD32F2F)) }
                }
            }
        }
    }
}