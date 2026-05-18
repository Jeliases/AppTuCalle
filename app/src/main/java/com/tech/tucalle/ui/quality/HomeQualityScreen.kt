package com.tech.tucalle.ui.quality

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tech.tucalle.ui.viewmodel.QualityViewModel
import com.tech.tucalle.navigation.BottomNavigationBarDynamic

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeQualityScreen(
    qualityViewModel: QualityViewModel = viewModel()
) {
    val uiState by qualityViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var expTiendas by remember { mutableStateOf(false) }
    var expPlatos by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomNavigationBarDynamic(rol = "QUALITY", currentSelection = "Reseñas") },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(scrollState)
        ) {
            Text(text = "Nueva Evaluación", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(text = "Método CHAS", color = Color.Gray, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(20.dp))

            if (uiState.mensajeExito.isNotEmpty()) {
                Text(
                    text = uiState.mensajeExito,
                    color = if(uiState.mensajeExito.contains("éxito")) Color(0xFF4CAF50) else Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // 1. DROPDOWN DE TIENDAS
            Text("Selecciona el Huarique a evaluar", fontWeight = FontWeight.SemiBold)
            ExposedDropdownMenuBox(
                expanded = expTiendas,
                onExpandedChange = { expTiendas = it },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.tiendaSeleccionada?.nombre ?: "Elige una tienda...",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expTiendas) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F))
                )
                ExposedDropdownMenu(expanded = expTiendas, onDismissRequest = { expTiendas = false }) {
                    uiState.tiendasDisponibles.forEach { tienda ->
                        DropdownMenuItem(
                            text = { Text(tienda.nombre) },
                            onClick = {
                                qualityViewModel.seleccionarTienda(tienda)
                                expTiendas = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. DROPDOWN MÚLTIPLE DE PLATOS SUGERIDOS
            if (uiState.tiendaSeleccionada != null) {
                Text("Platos Sugeridos (Puedes elegir varios)", fontWeight = FontWeight.SemiBold)
                ExposedDropdownMenuBox(
                    expanded = expPlatos,
                    onExpandedChange = { expPlatos = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    val platosText = if (uiState.platosSeleccionadosIds.isEmpty()) "Selecciona platos..."
                    else "${uiState.platosSeleccionadosIds.size} platos seleccionados"
                    OutlinedTextField(
                        value = platosText,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expPlatos) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F))
                    )
                    ExposedDropdownMenu(expanded = expPlatos, onDismissRequest = { expPlatos = false }) {
                        uiState.platosDeTienda.forEach { plato ->
                            val isSelected = uiState.platosSeleccionadosIds.contains(plato.id)
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(checked = isSelected, onCheckedChange = null, colors = CheckboxDefaults.colors(checkedColor = Color(0xFFD32F2F)))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(plato.nombre)
                                    }
                                },
                                onClick = { qualityViewModel.togglePlato(plato.id) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. SLIDERS DEL MÉTODO CHAS
                Text("Evaluación CHAS (0 a 5 estrellas)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))

                ChasSlider("Confort (Ambiente, asientos)", uiState.confort) { v -> qualityViewModel.onChasChange(v, uiState.higiene, uiState.atencion, uiState.sabrosura) }
                ChasSlider("Higiene (Limpieza general)", uiState.higiene) { v -> qualityViewModel.onChasChange(uiState.confort, v, uiState.atencion, uiState.sabrosura) }
                ChasSlider("Atención (Rapidez, amabilidad)", uiState.atencion) { v -> qualityViewModel.onChasChange(uiState.confort, uiState.higiene, v, uiState.sabrosura) }
                ChasSlider("Sabrosura (Sabor, presentación)", uiState.sabrosura) { v -> qualityViewModel.onChasChange(uiState.confort, uiState.higiene, uiState.atencion, v) }

                Spacer(modifier = Modifier.height(20.dp))

                // 4. COMENTARIO GENERAL
                OutlinedTextField(
                    value = uiState.comentario,
                    onValueChange = { qualityViewModel.onComentarioChange(it) },
                    label = { Text("Escribe tu reseña detallada...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFD32F2F))
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 5. BOTÓN ENVIAR
                Button(
                    onClick = { qualityViewModel.enviarRecomendacion() },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(30.dp),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text("Publicar Recomendación", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ChasSlider(label: String, value: Double, onValueChange: (Double) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 14.sp, color = Color.DarkGray)
            Text(String.format("%.1f ★", value), fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toDouble()) },
            valueRange = 0f..5f,
            steps = 9,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFD32F2F),
                activeTrackColor = Color(0xFFD32F2F)
            )
        )
    }
}