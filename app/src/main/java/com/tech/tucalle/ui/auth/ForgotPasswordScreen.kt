package com.tech.tucalle.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onNavigateToVerify: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Instancia de Firestore
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 30.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.padding(top = 10.dp)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.Black)
        }

        Spacer(modifier = Modifier.height(100.dp))

        Text("Recuperar contraseña", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Ingresa tu correo para verificar tu cuenta", color = Color.Gray)

        Spacer(modifier = Modifier.height(30.dp))

        LoginInput(label = "Correo electrónico", value = email, onValueChange = { email = it })

        if (statusMessage.isNotEmpty()) {
            Text(
                text = statusMessage,
                color = Color(0xFFD32F2F),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                val cleanEmail = email.trim().lowercase()

                if (cleanEmail.isNotEmpty()) {
                    isLoading = true
                    statusMessage = ""

                    // PASO 1: Verificar en Firestore
                    db.collection("usuarios")
                        .whereEqualTo("email", cleanEmail)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                isLoading = false
                                statusMessage = "El correo ingresado no está registrado."
                            } else {
                                // PASO 2: Enviar el link
                                FirebaseAuth.getInstance().sendPasswordResetEmail(cleanEmail)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            onNavigateToVerify(cleanEmail)
                                        } else {
                                            statusMessage = "Error al enviar: ${task.exception?.message}"
                                        }
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            statusMessage = "Error de conexión: ${e.message}"
                        }
                } else {
                    statusMessage = "Por favor, ingresa tu correo."
                }
            },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(30.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Continuar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Aviso de spam — siempre visible para orientar al usuario
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text("💡", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp, top = 2.dp))
                Column {
                    Text(
                        text = "¿No ves el correo?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF795548)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Revisa tu carpeta de Spam o Correo no deseado. Si está ahí, márcalo como \"No es spam\" para que futuros correos lleguen directo a tu bandeja.",
                        fontSize = 12.sp,
                        color = Color(0xFF795548),
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}