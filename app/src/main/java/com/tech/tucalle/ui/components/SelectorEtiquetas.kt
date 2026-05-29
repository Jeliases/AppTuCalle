package com.tech.tucalle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tech.tucalle.domain.model.EtiquetasTienda
import com.tech.tucalle.ui.theme.Poppins

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectorEtiquetas(
    etiquetasSeleccionadas: List<String>,
    onEtiquetaToggle: (String) -> Unit,
    maxSeleccion: Int = 4,
    enabled: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Etiquetas de tu huarique (Máximo $maxSeleccion)*",
            fontSize = 12.sp,
            color = Color.Gray,
            fontFamily = Poppins
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EtiquetasTienda.lista.forEach { etiqueta ->
                val isSelected = etiquetasSeleccionadas.contains(etiqueta)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) Color(0xFFD32F2F) else Color(0xFFEEEEEE))
                        .clickable(enabled = enabled) {
                            if (isSelected || etiquetasSeleccionadas.size < maxSeleccion) {
                                onEtiquetaToggle(etiqueta)
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = etiqueta,
                        color = if (isSelected) Color.White else Color.DarkGray,
                        fontSize = 12.sp,
                        fontFamily = Poppins,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
        if (etiquetasSeleccionadas.size >= maxSeleccion) {
            Text(
                text = "Has alcanzado el límite de $maxSeleccion etiquetas.",
                fontSize = 10.sp,
                color = Color(0xFFD32F2F),
                fontFamily = Poppins,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}