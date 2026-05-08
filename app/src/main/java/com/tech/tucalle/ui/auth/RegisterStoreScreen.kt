package com.tech.tucalle.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tech.tucalle.ui.viewmodel.AuthViewModel
import java.util.Locale

@Composable
fun RegisterStoreScreen(
    onBack: () -> Unit,
    onNavigateToStoreDashboard: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var nombreLocal by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }

    // Estados para la Dirección con Mapas
    var direccionTexto by remember { mutableStateOf("") }
    var latitudSeleccionada by remember { mutableDoubleStateOf(0.0) }
    var longitudSeleccionada by remember { mutableDoubleStateOf(0.0) }
    var showMapSelector by remember { mutableStateOf(false) }

    // Estados para el Horario de Atención
    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }
    var showInicioDialog by remember { mutableStateOf(false) }
    var showFinDialog by remember { mutableStateOf(false) }

    var statusMessage by remember { mutableStateOf("") }
    var aceptoTerminos by remember { mutableStateOf(false) }
    var aceptoPromociones by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // 1. Selector de Ubicación por Mapa (Pantalla completa / Dialog)
    if (showMapSelector) {
        Dialog(
            onDismissRequest = { showMapSelector = false },
            properties = DialogProperties(usePlatformDefaultWidth = false) // Permite pantalla completa para el mapa
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
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
    }

    // 2. Diálogos para selección de Horarios
    if (showInicioDialog) {
        CustomTimePickerDialog(
            onDismissRequest = { showInicioDialog = false },
            onConfirm = { hour, minute ->
                horaInicio = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                showInicioDialog = false
            }
        )
    }

    if (showFinDialog) {
        CustomTimePickerDialog(
            onDismissRequest = { showFinDialog = false },
            onConfirm = { hour, minute ->
                horaFin = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                showFinDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 30.dp)
            .verticalScroll(scrollState)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.padding(top = 10.dp)) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Atrás",
                modifier = Modifier.size(30.dp),
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Regístrate", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        if (statusMessage.isNotEmpty()) {
            Text(
                text = statusMessage,
                color = if (statusMessage.contains("éxito")) Color(0xFF4CAF50) else Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Reutilización de tus campos estándar
        LoginInput(label = "Nombre del local*", value = nombreLocal, onValueChange = { nombreLocal = it })
        LoginInput(label = "Email*", value = email, onValueChange = { email = it })
        LoginInput(label = "Contraseña*", value = password, onValueChange = { password = it }, isPassword = true)
        LoginInput(label = "Número de contacto*", value = celular, onValueChange = { celular = it })

        // 3. CAMPO DE DIRECCIÓN INTERACTIVO (Reemplaza al text field plano)
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Text(text = "Dirección del local*", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        }
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .clickable { showMapSelector = true }
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (direccionTexto.isEmpty()) "Seleccionar en el mapa..." else direccionTexto,
                    color = if (direccionTexto.isEmpty()) Color.Gray else Color.Black,
                    fontSize = 14.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Abrir Mapa",
                    tint = Color(0xFFD32F2F)
                )
            }
        }

        // 4. Sección de Horario de Atención (Botones reloj)
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Text(text = "Horario de atención*", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp)
                    .clickable { showInicioDialog = true }
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (horaInicio.isEmpty()) "Abre a las..." else "Abre: $horaInicio",
                        color = if (horaInicio.isEmpty()) Color.Gray else Color.Black,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp)
                    .clickable { showFinDialog = true }
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (horaFin.isEmpty()) "Cierra a las..." else "Cierra: $horaFin",
                        color = if (horaFin.isEmpty()) Color.Gray else Color.Black,
                        fontSize = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = aceptoTerminos,
                onCheckedChange = { aceptoTerminos = it },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFD32F2F))
            )
            Text("Acepto los términos y condiciones", fontSize = 13.sp, color = Color.Black)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = aceptoPromociones,
                onCheckedChange = { aceptoPromociones = it },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFD32F2F))
            )
            Text("Acepto recibir promociones a mi correo", fontSize = 13.sp, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty() && nombreLocal.isNotEmpty() &&
                    direccionTexto.isNotEmpty() && horaInicio.isNotEmpty() && horaFin.isNotEmpty()) {

                    val horarioFinal = "$horaInicio - $horaFin"

                    // Guardamos la estructura optimizada para mapas en un Mapa anidado
                    val storeData = mapOf(
                        "nombre" to nombreLocal,
                        "email" to email,
                        "celular" to celular,
                        "horario" to horarioFinal,
                        "rol" to "TIENDA",
                        "recibirPromociones" to aceptoPromociones,
                        "direccion" to mapOf(
                            "texto" to direccionTexto,
                            "latitud" to latitudSeleccionada,
                            "longitud" to longitudSeleccionada
                        )
                    )

                    authViewModel.registerUserWithRole(
                        email = email,
                        pass = password,
                        userData = storeData,
                        onSuccess = {
                            statusMessage = "¡Tienda registrada con éxito!"
                            onNavigateToStoreDashboard()
                        },
                        onFailure = { error ->
                            statusMessage = error
                        }
                    )
                } else {
                    statusMessage = "Por favor, llena todos los campos obligatorios."
                }
            },
            enabled = aceptoTerminos,
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Registrarse", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

// Componible Auxiliar para el Reloj de Selección
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val state = rememberTimePickerState(is24Hour = true)

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = true)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 6.dp,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Selecciona la hora",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(20.dp))

                TimePicker(state = state)

                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancelar", color = Color(0xFFD32F2F))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { onConfirm(state.hour, state.minute) }
                    ) {
                        Text("Aceptar", color = Color(0xFFD32F2F))
                    }
                }
            }
        }
    }
}