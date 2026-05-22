package com.tech.tucalle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEditing: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLength: Int = Int.MAX_VALUE
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        OutlinedTextField(
            value = value,
            // Solo permite escribir si no supera el límite y si está en modo edición
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            readOnly = !isEditing,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isEditing) Color(0xFFD32F2F) else Color.Transparent,
                unfocusedBorderColor = if (isEditing) Color.LightGray else Color.Transparent,
                focusedContainerColor = if (isEditing) Color.White else Color(0xFFF9F9F9),
                unfocusedContainerColor = if (isEditing) Color.White else Color(0xFFF9F9F9)
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDateField(
    label: String,
    value: String,
    onDateSelected: (String) -> Unit,
    isEditing: Boolean
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog && isEditing) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val cal = Calendar.getInstance().apply { timeInMillis = millis }
                        val day = String.format(java.util.Locale.getDefault(), "%02d", cal.get(Calendar.DAY_OF_MONTH))
                        val month = String.format(java.util.Locale.getDefault(), "%02d", cal.get(Calendar.MONTH) + 1)
                        val year = cal.get(Calendar.YEAR)
                        onDateSelected("$day/$month/$year")
                    }
                    showDialog = false
                }) { Text("Aceptar", color = Color(0xFFD32F2F)) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar", color = Color.Gray) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Box(modifier = Modifier.fillMaxWidth().clickable { if (isEditing) showDialog = true }) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = if (isEditing) Color(0xFFD32F2F) else Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isEditing) Color(0xFFD32F2F) else Color.Transparent,
                    unfocusedBorderColor = if (isEditing) Color.LightGray else Color.Transparent,
                    focusedContainerColor = if (isEditing) Color.White else Color(0xFFF9F9F9),
                    unfocusedContainerColor = if (isEditing) Color.White else Color(0xFFF9F9F9)
                ),
                shape = RoundedCornerShape(8.dp)
            )
            Box(modifier = Modifier.matchParentSize().background(Color.Transparent).clickable { if (isEditing) showDialog = true })
        }
    }
}

@Composable
fun ProfileDocumentField(
    tipoDocumento: String,
    numeroDocumento: String,
    onTipoChange: (String) -> Unit,
    onNumeroChange: (String) -> Unit,
    isEditing: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    // Dinámico: DNI 8 dígitos, CE 9 dígitos
    val maxLen = if (tipoDocumento == "DNI") 8 else 9

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Documento de Identidad", color = Color.Gray, fontSize = 12.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Dropdown Tipo (DNI/CE)
            Box(modifier = Modifier.weight(0.35f)) {
                OutlinedTextField(
                    value = tipoDocumento,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isEditing) Color(0xFFD32F2F) else Color.Transparent,
                        unfocusedBorderColor = if (isEditing) Color.LightGray else Color.Transparent,
                        focusedContainerColor = if (isEditing) Color.White else Color(0xFFF9F9F9),
                        unfocusedContainerColor = if (isEditing) Color.White else Color(0xFFF9F9F9)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                Box(modifier = Modifier.matchParentSize().background(Color.Transparent).clickable { if (isEditing) expanded = true })
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("DNI") }, onClick = { onTipoChange("DNI"); onNumeroChange(""); expanded = false })
                    DropdownMenuItem(text = { Text("CE") }, onClick = { onTipoChange("CE"); onNumeroChange(""); expanded = false })
                }
            }

            // Textfield Numero
            OutlinedTextField(
                value = numeroDocumento,
                // Validación: Solo números y respeta el límite de DNI o CE
                onValueChange = { if (it.length <= maxLen && it.all { char -> char.isDigit() }) onNumeroChange(it) },
                readOnly = !isEditing,
                modifier = Modifier.weight(0.65f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isEditing) Color(0xFFD32F2F) else Color.Transparent,
                    unfocusedBorderColor = if (isEditing) Color.LightGray else Color.Transparent,
                    focusedContainerColor = if (isEditing) Color.White else Color(0xFFF9F9F9),
                    unfocusedContainerColor = if (isEditing) Color.White else Color(0xFFF9F9F9)
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
fun ProfileMapField(
    label: String,
    direccion: String,
    onMapClick: () -> Unit,
    isEditing: Boolean
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Box(modifier = Modifier.fillMaxWidth().clickable { if (isEditing) onMapClick() }) {
            OutlinedTextField(
                value = direccion,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = if (isEditing) Color(0xFFD32F2F) else Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isEditing) Color(0xFFD32F2F) else Color.Transparent,
                    unfocusedBorderColor = if (isEditing) Color.LightGray else Color.Transparent,
                    focusedContainerColor = if (isEditing) Color.White else Color(0xFFF9F9F9),
                    unfocusedContainerColor = if (isEditing) Color.White else Color(0xFFF9F9F9)
                ),
                shape = RoundedCornerShape(8.dp)
            )
            Box(modifier = Modifier.matchParentSize().background(Color.Transparent).clickable { if (isEditing) onMapClick() })
        }
    }
}

@Composable
fun ProfileDiasSemanaField(
    diasSeleccionados: List<String>,
    onDiasChange: (List<String>) -> Unit,
    isEditing: Boolean
) {
    val diasSemana = listOf("L", "M", "X", "J", "V", "S", "D")
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Días de apertura", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            diasSemana.forEach { dia ->
                val isSelected = diasSeleccionados.contains(dia)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFFD32F2F) else Color(0xFFF5F5F5))
                        .clickable(enabled = isEditing) {
                            val nuevosDias = if (isSelected) diasSeleccionados - dia else diasSeleccionados + dia
                            onDiasChange(nuevosDias)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = dia, color = if (isSelected) Color.White else Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}