package com.tech.tucalle.ui.huarique

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tech.tucalle.domain.model.Plato
import com.tech.tucalle.ui.viewmodel.AuthViewModel
import com.tech.tucalle.ui.viewmodel.PlatoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionPlatosScreen(
    authViewModel: AuthViewModel = viewModel(),
    platoViewModel: PlatoViewModel = viewModel()
) {
    val uid = authViewModel.obtenerUidActual()
    val platos by platoViewModel.platos.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { platoViewModel.cargarPlatos(uid) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis Platos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = Color(0xFFD32F2F)) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Plato", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(platos) { plato ->
                Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        AsyncImage(model = plato.imagenUrl, contentDescription = null, modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(plato.nombre, style = MaterialTheme.typography.titleMedium)
                            Text("S/ ${plato.precio}", color = Color.Gray)
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AgregarPlatoDialog(
                onDismiss = { showDialog = false },
                onSave = { nombre, desc, precio, uri ->
                    // 1. Subir imagen primero usando el AuthViewModel que ya tienes
                    authViewModel.uploadImageSuspend(uri)?.let { url ->
                        val nuevoPlato = Plato(idTienda = uid, nombre = nombre, descripcion = desc, precio = precio.toDouble(), imagenUrl = url)
                        platoViewModel.guardarPlato(nuevoPlato) { showDialog = false }
                    }
                }
            )
        }
    }
}

@Composable
fun AgregarPlatoDialog(onDismiss: () -> Unit, onSave: (String, String, String, Uri) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri = it }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Plato") },
        text = {
            Column {
                Button(onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                    Text(if (imageUri == null) "Seleccionar Foto" else "Foto Seleccionada")
                }
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") })
            }
        },
        confirmButton = {
            TextButton(onClick = { if (imageUri != null) onSave(nombre, desc, precio, imageUri!!) }) {
                Text("Guardar")
            }
        }
    )
}