package com.tech.tucalle.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Estas son las importaciones correctas para Firebase Slark
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun RegisterScreen(tipo: String, onBack: () -> Unit) {
    // ESTADOS: Captura de datos del usuario
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }

    // ESTADOS DE CHECKBOX
    var aceptoTerminos by remember { mutableStateOf(false) }
    var aceptoPromociones by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 30.dp)
            .verticalScroll(scrollState)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.padding(top = 10.dp)) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás", modifier = Modifier.size(30.dp))
        }

        Text(text = "Regístrate", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        // Mostrar mensaje de error o éxito si existe
        if (statusMessage.isNotEmpty()) {
            Text(
                text = statusMessage,
                color = if (statusMessage.contains("éxito")) Color(0xFF4CAF50) else Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        LoginInput(label = "Nombres y Apellidos*", value = nombre, onValueChange = { nombre = it })
        LoginInput(label = "Email*", value = email, onValueChange = { email = it })
        LoginInput(label = "Contraseña*", value = password, onValueChange = { password = it }, isPassword = true)
        LoginInput(label = "Celular*", value = celular, onValueChange = { celular = it })

        Spacer(modifier = Modifier.height(15.dp))

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
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    // LLAMADA REAL A FIREBASE SLARK
                    Firebase.auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                statusMessage = "¡Usuario creado con éxito!"
                                // Aquí podrías guardar 'nombre' y 'celular' en Firestore después
                                println("Registrado: $nombre - Promo: $aceptoPromociones")
                            } else {
                                // Captura el error real (ej: email mal escrito o clave muy corta)
                                statusMessage = "Error: ${task.exception?.message}"
                            }
                        }
                } else {
                    statusMessage = "Por favor, completa todos los campos"
                }
            },
            enabled = aceptoTerminos,
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Registrarse", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(tipo = "USUARIO", onBack = {})
}