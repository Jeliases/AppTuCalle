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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.tech.tucalle.domain.model.Plato
import com.tech.tucalle.navigation.BottomNavigationBarDynamic
import com.tech.tucalle.ui.viewmodel.AuthViewModel
import com.tech.tucalle.ui.viewmodel.PlatoViewModel
import com.tech.tucalle.ui.viewmodel.StoreViewModel // 🔥 IMPORTADO
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionPlatosScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    platoViewModel: PlatoViewModel = viewModel(),
    storeViewModel: StoreViewModel = viewModel() // 🔥 AÑADIDO PARA OBTENER EL NOMBRE DE LA TIENDA
) {
    val uid = authViewModel.obtenerUidActual()
    val platos by platoViewModel.platos.collectAsState()
    val storeUiState by storeViewModel.uiState.collectAsState() // 🔥 ESTADO DE LA TIENDA
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { platoViewModel.cargarPlatos(uid) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Platos", fontWeight = FontWeight.Bold) })
        },
        bottomBar = {
            BottomNavigationBarDynamic(
                rol = "TIENDA",
                currentSelection = "Platos",
                navController = navController
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFFD32F2F)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Plato", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(platos) { plato ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = plato.imagenUrl,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(plato.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            // 🔥 CORREGIDO: Ahora usa precioOriginal del nuevo modelo
                            Text("S/ ${plato.precioOriginal}", color = Color.Gray)
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AgregarPlatoDialog(
                onDismiss = { showDialog = false },
                onSave = { nombre, desc, precioStr, uri, scope ->
                    scope.launch {
                        authViewModel.uploadImageSuspend(uri)?.let { url ->
                            // Convertimos de forma segura
                            val precioDouble = precioStr.toDoubleOrNull() ?: 0.0

                            val nuevoPlato = Plato(
                                idTienda        = uid,
                                nombreTienda    = storeUiState.nombreTienda, // 🔥 CORREGIDO: Ahora lee correctamente del ViewModel
                                nombre          = nombre,
                                descripcion     = desc,
                                precioOriginal  = precioDouble,
                                precioDescuento = precioDouble,
                                calificacionPlato = storeUiState.calificacionGeneral,
                                imagenUrl       = url
                            )
                            platoViewModel.guardarPlato(nuevoPlato) { showDialog = false }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun AgregarPlatoDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, Uri, kotlinx.coroutines.CoroutineScope) -> Unit
) {
    var nombre   by remember { mutableStateOf("") }
    var precio   by remember { mutableStateOf("") }
    var desc     by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { imageUri = it }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Plato", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (imageUri == null) "Seleccionar Foto" else "✅ Foto seleccionada")
                }
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del plato") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio (S/)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (imageUri != null && nombre.isNotBlank() && precio.isNotBlank()) {
                        isLoading = true
                        onSave(nombre, desc, precio, imageUri!!, scope)
                    }
                },
                enabled = !isLoading && imageUri != null
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color(0xFFD32F2F),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    )
}