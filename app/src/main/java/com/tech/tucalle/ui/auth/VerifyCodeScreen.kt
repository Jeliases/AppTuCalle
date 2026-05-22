package com.tech.tucalle.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VerifyCodeScreen(email: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón superior para regresar
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.Black)
        }

        Spacer(modifier = Modifier.height(80.dp))

        // Ícono gigante de correo con fondo circular suave (Estilo profesional)
        Surface(
            modifier = Modifier.size(110.dp),
            shape = CircleShape,
            color = Color(0xFFFDECEB) // Rojo clarito que combina con tu app
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Correo enviado",
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(55.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "¡Revisa tu correo!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Hemos enviado un enlace seguro a:",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Text(
            text = email,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Haz clic en el enlace del mensaje para crear tu nueva contraseña y luego regresa aquí.",
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Botón principal
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Volver al Inicio", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}