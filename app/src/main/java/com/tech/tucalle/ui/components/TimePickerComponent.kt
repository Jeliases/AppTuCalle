package com.tech.tucalle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tech.tucalle.ui.theme.Poppins
import java.util.Locale

// 🔥 1. El Dialog del Reloj
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReusableTimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
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
                    TextButton(onClick = {
                        val amPm = if (state.hour >= 12) "PM" else "AM"
                        var hour12 = state.hour % 12
                        if (hour12 == 0) hour12 = 12
                        val timeString = String.format(Locale.getDefault(), "%02d:%02d %s", hour12, state.minute, amPm)
                        onConfirm(timeString)
                    }) { Text("Aceptar", color = Color(0xFFD32F2F)) }
                }
            }
        }
    }
}

// 2. El Campo visual que parece un Input pero abre el Reloj (Como en tu Figma)
@Composable
fun TimeSelectorField(
    value: String,
    label: String,
    enabled: Boolean = true,
    onTimeSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ReusableTimePickerDialog(
            onDismissRequest = { showDialog = false },
            onConfirm = { time ->
                onTimeSelected(time)
                showDialog = false
            }
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (label.isNotEmpty()) {
            Text(label, fontSize = 12.sp, color = Color.Gray, fontFamily = Poppins)
            Spacer(modifier = Modifier.height(4.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (enabled) Color(0xFFF9F9F9) else Color(0xFFEEEEEE))
                .clickable(enabled = enabled) { showDialog = true }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = value.ifBlank { "00:00 AM" },
                fontSize = 14.sp,
                color = if (value.isBlank() || !enabled) Color.LightGray else Color.Black,
                fontFamily = Poppins
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
        }
    }
}