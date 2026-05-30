package com.tech.tucalle.ui.quality

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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
import androidx.navigation.NavHostController
import com.tech.tucalle.domain.model.Store
import com.tech.tucalle.navigation.BottomNavigationBarDynamic
import com.tech.tucalle.ui.theme.Poppins
import com.tech.tucalle.ui.theme.Roboto
import com.tech.tucalle.ui.viewmodel.EvaluacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaEvaluacionScreen(
    navController: NavHostController,
    viewModel: EvaluacionViewModel = viewModel()
) {
    val tiendasGuardadas by viewModel.tiendasGuardadas.collectAsState()
    val platosTienda by viewModel.platosTienda.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    var tiendaSeleccionada by remember { mutableStateOf<Store?>(null) }
    var platosSeleccionados by remember { mutableStateOf<Set<String>>(emptySet()) }

    var expandedTienda by remember { mutableStateOf(false) }
    var expandedPlatos by remember { mutableStateOf(false) }

    var confort by remember { mutableStateOf(4.5f) }
    var higiene by remember { mutableStateOf(4.0f) }
    var atencion by remember { mutableStateOf(4.0f) }
    var sabrosura by remember { mutableStateOf(2.5f) }
    var reviewText by remember { mutableStateOf("") }

    val colorRojo = Color(0xFFD32F2F)

    // 🔥 CÁLCULO DEL PROMEDIO EN TIEMPO REAL
    val promedioChas = (confort + higiene + atencion + sabrosura) / 4.0

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Nueva Evaluación", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Método CHAS", fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { BottomNavigationBarDynamic(rol = "QUALITY", currentSelection = "Reseñas", navController = navController) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp).verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Selecciona el Huarique a evaluar", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(expanded = expandedTienda, onExpandedChange = { expandedTienda = !expandedTienda }) {
                OutlinedTextField(
                    value = tiendaSeleccionada?.nombre ?: "Selecciona un huarique guardado",
                    onValueChange = {}, readOnly = true, modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colorRojo, unfocusedBorderColor = Color.Gray),
                    shape = RoundedCornerShape(8.dp)
                )
                ExposedDropdownMenu(expanded = expandedTienda, onDismissRequest = { expandedTienda = false }) {
                    if (tiendasGuardadas.isEmpty()) {
                        DropdownMenuItem(text = { Text("No tienes huariques guardados") }, onClick = { expandedTienda = false })
                    } else {
                        tiendasGuardadas.forEach { tienda ->
                            DropdownMenuItem(
                                text = { Text(tienda.nombre) },
                                onClick = {
                                    tiendaSeleccionada = tienda
                                    platosSeleccionados = emptySet()
                                    viewModel.cargarPlatosDeTienda(tienda.uid)
                                    expandedTienda = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Platos Sugeridos (Puedes elegir varios)", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(expanded = expandedPlatos, onExpandedChange = { if (tiendaSeleccionada != null) expandedPlatos = !expandedPlatos }) {
                OutlinedTextField(
                    value = if (platosSeleccionados.isEmpty()) "Seleccionar platos..." else "${platosSeleccionados.size} platos seleccionados",
                    onValueChange = {}, readOnly = true, modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colorRojo, unfocusedBorderColor = if (platosSeleccionados.isNotEmpty()) colorRojo else Color.Gray),
                    shape = RoundedCornerShape(8.dp)
                )
                ExposedDropdownMenu(expanded = expandedPlatos, onDismissRequest = { expandedPlatos = false }) {
                    if (platosTienda.isEmpty()) {
                        DropdownMenuItem(text = { Text("No hay platos registrados") }, onClick = { expandedPlatos = false })
                    } else {
                        platosTienda.forEach { plato ->
                            val isSelected = platosSeleccionados.contains(plato.nombre)
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(checked = isSelected, onCheckedChange = null, colors = CheckboxDefaults.colors(checkedColor = colorRojo))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(plato.nombre)
                                    }
                                },
                                onClick = {
                                    platosSeleccionados = if (isSelected) platosSeleccionados - plato.nombre else platosSeleccionados + plato.nombre
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Evaluación CHAS (0 a 5 estrellas)", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            ChasSliderRow("Confort (Ambiente, asientos)", confort) { confort = it }
            ChasSliderRow("Higiene (Limpieza general)", higiene) { higiene = it }
            ChasSliderRow("Atención (Rapidez, amabilidad)", atencion) { atencion = it }
            ChasSliderRow("Sabrosura (Sabor, presentación)", sabrosura) { sabrosura = it }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("Escribe tu reseña detallada...", fontFamily = Poppins) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colorRojo)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔥 RECUADRO CON EL PROMEDIO DINÁMICO
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF8F8), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Calificación promedio CHAS:", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(String.format("%.1f", promedioChas), fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colorRojo)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (tiendaSeleccionada != null && reviewText.isNotBlank()) {
                        viewModel.publicarEvaluacion(
                            tiendaSeleccionada!!, platosSeleccionados.toList(),
                            confort, higiene, atencion, sabrosura, reviewText
                        ) {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorRojo),
                shape = RoundedCornerShape(28.dp),
                enabled = !isSubmitting && tiendaSeleccionada != null && reviewText.isNotBlank()
            ) {
                if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Publicar Recomendación", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun ChasSliderRow(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontFamily = Poppins, fontSize = 13.sp, color = Color(0xFF555555))
            Text("${String.format("%.1f", value)} ★", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFD32F2F))
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..5f,
            steps = 9,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFD32F2F),
                activeTrackColor = Color(0xFFD32F2F),
                inactiveTrackColor = Color(0xFFD32F2F).copy(alpha = 0.2f)
            )
        )
    }
}