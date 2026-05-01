package com.tech.tucalle.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun UserTypeScreen(
    onTypeSelected: (String) -> Unit,
    onBack: () -> Unit // Solo dejamos este para el botón de atrás
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Queremos conocerte 👇",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Botón Soy Usuario
        Button(
            onClick = { onTypeSelected("USUARIO") },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("Soy Usuario", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Soy Tienda / Huarique
        Button(
            onClick = { onTypeSelected("HUARIQUE") },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("Soy Tienda / Huarique", fontSize = 18.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserTypeScreenPreview() {
    // Aquí le pasamos funciones vacías para que la previa cargue
    UserTypeScreen(
        onTypeSelected = {},
        onBack = {}
    )
}